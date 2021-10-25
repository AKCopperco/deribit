package co.copper.deribit.service

import co.copper.deribit.api.DeribitApi
import co.copper.deribit.dto.*
import co.copper.deribit.exception.DeribitException
import co.copper.deribit.model.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.mockito.stubbing.Answer
import java.math.BigDecimal
import java.util.*
import kotlin.math.min

@ExtendWith(MockitoExtension::class)
class DeribitApiServiceTests {
    private lateinit var deribitApiService: DeribitApiService

    private lateinit var validClientId: String
    private lateinit var validClientSecret: String
    private lateinit var validAccessToken: String

    @Mock
    private lateinit var api: DeribitApi

    @BeforeEach
    fun setUp() {
        validClientId = UUID.randomUUID().toString();
        validClientSecret = UUID.randomUUID().toString();
        validAccessToken = UUID.randomUUID().toString();
        deribitApiService = DeribitApiService(api)
    }

    @Test
    fun `Service is created`() {
        assertNotNull(deribitApiService)
    }

    @Test
    fun `Get Account Summary returns user balances for all currencies if authorization successful`() {
        val accountSummaryMap = mapOf(
            "BTC" to DeribitAccountSummaryResult(
                currency = "BTC",
                balance = 15.0.toBigDecimal(),
                availableWithdrawalFunds = 10.0.toBigDecimal(),
                systemName = "TestName1"
            ),
            "ETH" to DeribitAccountSummaryResult(
                currency = "ETH",
                balance = 16.0.toBigDecimal(),
                availableWithdrawalFunds = 4.0.toBigDecimal(),
                systemName = "TestName1"
            ),
            "USDT" to DeribitAccountSummaryResult(
                currency = "USDT",
                balance = 33.0.toBigDecimal(),
                availableWithdrawalFunds = 33.0.toBigDecimal(),
                systemName = "TestName1"
            )
        )

        setupAuthMock()
        setupCurrenciesMock()
        setupAccountSummaryMock(accountSummaryMap)

        val result = deribitApiService.getAccountSummary(validClientId, validClientSecret)

        assertEquals(3, result.size)
        assertEquals(
            UserBalance(
                currency = "BTC",
                balance = 15.0.toBigDecimal(),
                reserved = 5.0.toBigDecimal(),
                username = "TestName1"
            ), result[0]
        )
        assertEquals(
            UserBalance(
                currency = "ETH",
                balance = 16.0.toBigDecimal(),
                reserved = 12.0.toBigDecimal(),
                username = "TestName1"
            ), result[1]
        )
        assertEquals(
            UserBalance(
                currency = "USDT",
                balance = 33.0.toBigDecimal(),
                reserved = 0.0.toBigDecimal(),
                username = "TestName1"
            ), result[2]
        )

        verify(api, times(1)).auth(any(), any(), any())
        verify(api, times(1)).getCurrencies()
        verify(api, times(1)).getAccountSummary(any(), eq("BTC"))
        verify(api, times(1)).getAccountSummary(any(), eq("ETH"))
        verify(api, times(1)).getAccountSummary(any(), eq("USDT"))
        verify(api, times(3)).getAccountSummary(any(), any())
    }

    @Test
    fun `Get Account Summary throws exception if authorization is unsuccessful`() {
        setupAuthMock()

        assertThrows<DeribitException> { deribitApiService.getAccountSummary("invalid", "invalid") }

        verify(api, times(1)).auth(any(), any(), any())
        verify(api, never()).getCurrencies()
        verify(api, never()).getAccountSummary(any(), any())
    }

    @Test
    fun `Get Transactions - zero transactions - success`() {
        val transactions: Map<TransactionType, Map<String, List<DeribitTransactionData>>> = emptyMap()

        setupAuthMock()
        setupCurrenciesMock()
        setupWithdrawMock(transactions)
        setupDepositMock(transactions)

        val result = deribitApiService.getTransactions(validClientId, validClientSecret)

        assertEquals(2, result.size)
        assertTrue(result.containsKey(TransactionType.Deposit))
        assertTrue(result.containsKey(TransactionType.Withdraw))

        val deposits = result[TransactionType.Deposit]
        assertNotNull(deposits)
        assertEquals(0, deposits!!.size)

        val withdrawals = result[TransactionType.Withdraw]
        assertNotNull(withdrawals)
        assertEquals(0, withdrawals!!.size)

        verify(api, times(1)).auth(validClientId, validClientSecret)
        verify(api, times(1)).auth(any(), any(), any())

        verify(api, times(1)).getCurrencies()
        verify(api, times(1)).getDeposits(any(), eq("BTC"), any(), any())
        verify(api, times(1)).getDeposits(any(), eq("ETH"), any(), any())
        verify(api, times(1)).getDeposits(any(), eq("USDT"), any(), any())
        verify(api, times(3)).getDeposits(any(), any(), any(), any())

        verify(api, times(1)).getWithdrawals(any(), eq("BTC"), any(), any())
        verify(api, times(1)).getWithdrawals(any(), eq("ETH"), any(), any())
        verify(api, times(1)).getWithdrawals(any(), eq("USDT"), any(), any())
        verify(api, times(3)).getWithdrawals(any(), any(), any(), any())

    }

    @Test
    fun `Get Transactions - one deposit transaction - success`() {
        val transactions = mapOf(
            TransactionType.Deposit to mapOf(
                "BTC" to deribitTransactions("BTC", 1)
            )
        )

        setupAuthMock()
        setupCurrenciesMock()
        setupWithdrawMock(transactions)
        setupDepositMock(transactions)


        val result = deribitApiService.getTransactions(validClientId, validClientSecret)

        assertEquals(2, result.size)
        assertTrue(result.containsKey(TransactionType.Deposit))
        assertTrue(result.containsKey(TransactionType.Withdraw))

        val deposits = result[TransactionType.Deposit]
        assertNotNull(deposits)
        assertEquals(1, deposits!!.size)

        val withdrawals = result[TransactionType.Withdraw]
        assertNotNull(withdrawals)
        assertEquals(0, withdrawals!!.size)

        verify(api, times(1)).auth(validClientId, validClientSecret)
        verify(api, times(1)).auth(any(), any(), any())

        verify(api, times(1)).getCurrencies()
        verify(api, times(1)).getDeposits(any(), eq("BTC"), any(), any())
        verify(api, times(1)).getDeposits(any(), eq("ETH"), any(), any())
        verify(api, times(1)).getDeposits(any(), eq("USDT"), any(), any())
        verify(api, times(3)).getDeposits(any(), any(), any(), any())

        verify(api, times(1)).getWithdrawals(any(), eq("BTC"), any(), any())
        verify(api, times(1)).getWithdrawals(any(), eq("ETH"), any(), any())
        verify(api, times(1)).getWithdrawals(any(), eq("USDT"), any(), any())
        verify(api, times(3)).getWithdrawals(any(), any(), any(), any())

    }

    @Test
    fun `Get Transactions - one withdraw transaction - success`() {
        val transactions = mapOf(
            TransactionType.Withdraw to mapOf(
                "BTC" to deribitTransactions("BTC", 1)
            )
        )

        setupAuthMock()
        setupCurrenciesMock()
        setupWithdrawMock(transactions)
        setupDepositMock(transactions)

        val result = deribitApiService.getTransactions(validClientId, validClientSecret)

        assertEquals(2, result.size)
        assertTrue(result.containsKey(TransactionType.Deposit))
        assertTrue(result.containsKey(TransactionType.Withdraw))

        val deposits = result[TransactionType.Deposit]
        assertNotNull(deposits)
        assertEquals(0, deposits!!.size)

        val withdrawals = result[TransactionType.Withdraw]
        assertNotNull(withdrawals)
        assertEquals(1, withdrawals!!.size)

        verify(api, times(1)).auth(validClientId, validClientSecret)
        verify(api, times(1)).auth(any(), any(), any())

        verify(api, times(1)).getCurrencies()
        verify(api, times(1)).getDeposits(any(), eq("BTC"), any(), any())
        verify(api, times(1)).getDeposits(any(), eq("ETH"), any(), any())
        verify(api, times(1)).getDeposits(any(), eq("USDT"), any(), any())
        verify(api, times(3)).getDeposits(any(), any(), any(), any())

        verify(api, times(1)).getWithdrawals(any(), eq("BTC"), any(), any())
        verify(api, times(1)).getWithdrawals(any(), eq("ETH"), any(), any())
        verify(api, times(1)).getWithdrawals(any(), eq("USDT"), any(), any())
        verify(api, times(3)).getWithdrawals(any(), any(), any(), any())

    }

    @Test
    fun `Get Transactions - many deposit transactions - success`() {

        val transactions = mapOf(
            TransactionType.Deposit to mapOf(
                "BTC" to deribitTransactions("BTC", 1555)
            )
        )

        setupAuthMock()
        setupCurrenciesMock()
        setupWithdrawMock(transactions)
        setupDepositMock(transactions)

        val result = deribitApiService.getTransactions(validClientId, validClientSecret)

        assertEquals(2, result.size)
        assertTrue(result.containsKey(TransactionType.Deposit))
        assertTrue(result.containsKey(TransactionType.Withdraw))

        val deposits = result[TransactionType.Deposit]
        assertNotNull(deposits)
        assertEquals(1555, deposits!!.size)

        val withdrawals = result[TransactionType.Withdraw]
        assertNotNull(withdrawals)
        assertEquals(0, withdrawals!!.size)

        verify(api, times(1)).auth(validClientId, validClientSecret)
        verify(api, times(1)).auth(any(), any(), any())

        verify(api, times(1)).getCurrencies()
        verify(api, times(156)).getDeposits(any(), eq("BTC"), any(), any())
        verify(api, times(1)).getDeposits(any(), eq("ETH"), any(), any())
        verify(api, times(1)).getDeposits(any(), eq("USDT"), any(), any())
        verify(api, times(158)).getDeposits(any(), any(), any(), any())

        verify(api, times(1)).getWithdrawals(any(), eq("BTC"), any(), any())
        verify(api, times(1)).getWithdrawals(any(), eq("ETH"), any(), any())
        verify(api, times(1)).getWithdrawals(any(), eq("USDT"), any(), any())
        verify(api, times(3)).getWithdrawals(any(), any(), any(), any())

    }

    @Test
    fun `Get Transactions - many withdraw transactions - success`() {

        val transactions = mapOf(
            TransactionType.Withdraw to mapOf(
                "BTC" to deribitTransactions("BTC", 2159)
            )
        )

        setupAuthMock()
        setupCurrenciesMock()
        setupWithdrawMock(transactions)
        setupDepositMock(transactions)

        val result = deribitApiService.getTransactions(validClientId, validClientSecret)

        assertEquals(2, result.size)
        assertTrue(result.containsKey(TransactionType.Deposit))
        assertTrue(result.containsKey(TransactionType.Withdraw))

        val deposits = result[TransactionType.Deposit]
        assertNotNull(deposits)
        assertEquals(0, deposits!!.size)

        val withdrawals = result[TransactionType.Withdraw]
        assertNotNull(withdrawals)
        assertEquals(2159, withdrawals!!.size)

        verify(api, times(1)).auth(validClientId, validClientSecret)
        verify(api, times(1)).auth(any(), any(), any())

        verify(api, times(1)).getCurrencies()
        verify(api, times(1)).getDeposits(any(), eq("BTC"), any(), any())
        verify(api, times(1)).getDeposits(any(), eq("ETH"), any(), any())
        verify(api, times(1)).getDeposits(any(), eq("USDT"), any(), any())
        verify(api, times(3)).getDeposits(any(), any(), any(), any())

        verify(api, times(216)).getWithdrawals(any(), eq("BTC"), any(), any())
        verify(api, times(1)).getWithdrawals(any(), eq("ETH"), any(), any())
        verify(api, times(1)).getWithdrawals(any(), eq("USDT"), any(), any())
        verify(api, times(218)).getWithdrawals(any(), any(), any(), any())

    }

    @Test
    fun `Get Transactions - many withdraw and many deposit transactions - success`() {

        val transactions = mapOf(
            TransactionType.Deposit to mapOf(
                "BTC" to deribitTransactions("BTC", 4213)
            ),
            TransactionType.Withdraw to mapOf(
                "BTC" to deribitTransactions("BTC", 9424)
            ),
        )

        setupAuthMock()
        setupCurrenciesMock()
        setupWithdrawMock(transactions)
        setupDepositMock(transactions)

        val result = deribitApiService.getTransactions(validClientId, validClientSecret)

        assertEquals(2, result.size)
        assertTrue(result.containsKey(TransactionType.Deposit))
        assertTrue(result.containsKey(TransactionType.Withdraw))

        val deposits = result[TransactionType.Deposit]
        assertNotNull(deposits)
        assertEquals(4213, deposits!!.size)

        val withdrawals = result[TransactionType.Withdraw]
        assertNotNull(withdrawals)
        assertEquals(9424, withdrawals!!.size)

        verify(api, times(1)).auth(validClientId, validClientSecret)
        verify(api, times(1)).auth(any(), any(), any())

        verify(api, times(1)).getCurrencies()
        verify(api, times(422)).getDeposits(any(), eq("BTC"), any(), any())
        verify(api, times(1)).getDeposits(any(), eq("ETH"), any(), any())
        verify(api, times(1)).getDeposits(any(), eq("USDT"), any(), any())
        verify(api, times(424)).getDeposits(any(), any(), any(), any())

        verify(api, times(943)).getWithdrawals(any(), eq("BTC"), any(), any())
        verify(api, times(1)).getWithdrawals(any(), eq("ETH"), any(), any())
        verify(api, times(1)).getWithdrawals(any(), eq("USDT"), any(), any())
        verify(api, times(945)).getWithdrawals(any(), any(), any(), any())

    }

    @Test
    fun `Get Transactions throws exception if authorization is unsuccessful`() {
        setupAuthMock()

        assertThrows<DeribitException> { deribitApiService.getTransactions("invalid", "invalid") }

        verify(api, times(1)).auth(any(), any(), any())
        verify(api, never()).getCurrencies()
        verify(api, never()).getWithdrawals(any(), any(), any(), any())
        verify(api, never()).getDeposits(any(), any(), any(), any())
    }

    @Test
    fun `Withdraw throws exception if authorization is unsuccessful`() {
        setupAuthMock()

        assertThrows<DeribitException> {
            deribitApiService.withdraw(
                "invalid",
                "invalid",
                "BTC",
                10.0.toBigDecimal(),
                "SomeAddress"
            )
        }

        verify(api, times(1)).auth(any(), any(), any())
        verify(api, never()).getCurrencies()
        verify(api, never()).withdraw(any(), any(), any(), any())
    }

    @Test
    fun `Withdraw - success`() {
        setupAuthMock()
        val address = UUID.randomUUID().toString()
        `when`(api.withdraw(any(), eq("BTC"), eq(15.0.toBigDecimal()), eq(address))).thenReturn(
            deribitResponseSuccess(
                DeribitWithdrawResult(
                    address = address,
                    amount = 15.0.toBigDecimal(),
                    currency = "BTC",
                    state = WithdrawalState.Completed
                )
            )
        )

        val result = deribitApiService.withdraw(validClientId, validClientSecret, "BTC", 15.0.toBigDecimal(), address)

        assertNotNull(result)
        assertEquals(address, result.address)
        assertEquals(15.0.toBigDecimal(), result.amount)
        assertEquals("BTC", result.currency)
        assertEquals(WithdrawalState.Completed, result.state)

        verify(api, times(1)).auth(any(), any(), any())
        verify(api, never()).getCurrencies()
        verify(api, times(1)).withdraw(any(), any(), any(), any())
    }

    @Test
    fun `Transfer To SubAccount - SubAccount exists - success`() {
        setupAuthMock()
        setupGetSubAccountsMock()

        `when`(api.transferToSubAccount(any(), any(), any(), any())).thenReturn(
            deribitResponseSuccess(
                DeribitTransferResult(
                    amount = 10.0.toBigDecimal(),
                    otherSide = "SubAccount1",
                    currency = "BTC",
                    state = TransferState.Confirmed,
                    type = TransferType.SubAccount,
                    direction = "direction"
                )
            )
        )

        val subAcountName = "SubAccount1"

        val result = deribitApiService.transferToSubAccount(
            validClientId,
            validClientSecret,
            "BTC",
            10.0.toBigDecimal(),
            subAcountName
        )

        assertNotNull(result)
        assertEquals("BTC", result.currency)
        assertEquals(10.0.toBigDecimal(), result.amount)
        assertEquals(TransferState.Confirmed, result.state)
        assertEquals(TransferType.SubAccount, result.type)
        assertEquals("direction", result.direction)
        assertEquals(subAcountName, result.otherSide)

        verify(api, times(1)).auth(any(), any(), any())
        verify(api, times(1)).getSubAccounts(any(), any())
        verify(api, times(1)).transferToSubAccount(any(), any(), any(), any())
    }

    @Test
    fun `Transfer To SubAccount - SubAccount doesn't exists - Exception thrown`() {
        setupAuthMock()
        setupGetSubAccountsMock()

        val subAcountName = "SubAccount3"

        assertThrows<DeribitException> {
            val result = deribitApiService.transferToSubAccount(
                validClientId,
                validClientSecret,
                "BTC",
                10.0.toBigDecimal(),
                subAcountName
            )
        }

        verify(api, times(1)).auth(any(), any(), any())
        verify(api, times(1)).getSubAccounts(any(), any())
        verify(api, never()).transferToSubAccount(any(), any(), any(), any())
    }

    @Test
    fun `Transfer To SubAccount throws exception if authorization is unsuccessful`() {
        setupAuthMock()

        assertThrows<DeribitException> {
            deribitApiService.transferToSubAccount(
                "invalid", "invalid",
                "BTC", 10.0.toBigDecimal(), "Some SubAccount Name"
            )
        }

        verify(api, times(1)).auth(any(), any(), any())
        verify(api, never()).getCurrencies()
        verify(api, never()).transferToSubAccount(any(), any(), any(), any())
    }

    private fun setupAuthMock() {
        `when`(api.auth(any(), any(), any())).thenAnswer {
            val clientId = it.arguments[0] as String
            val clientSecret = it.arguments[1] as String
            if (clientId == validClientId && clientSecret == validClientSecret) {
                deribitResponseSuccess(
                    DeribitAuthResult(validAccessToken)
                )
            } else {
                throw DeribitException(13004, "invalid_credentials")
            }
        }
    }

    private fun setupCurrenciesMock() {
        `when`(api.getCurrencies()).thenReturn(
            deribitResponseSuccess(
                listOf(
                    DeribitCurrencyResult("BTC"),
                    DeribitCurrencyResult("ETH"),
                    DeribitCurrencyResult("USDT"),
                )
            )
        )
    }

    private fun setupWithdrawMock(transactions: Map<TransactionType, Map<String, List<DeribitTransactionData>>>) {
        `when`(
            api.getWithdrawals(any(), any(), any(), any())
        ).thenAnswer(Answer {
            val currency = it.arguments[1] as String
            val count = it.arguments[2] as Int
            val offset = it.arguments[3] as Int
            val transactionDataList =
                transactions.getOrDefault(TransactionType.Withdraw, emptyMap()).getOrDefault(currency, emptyList())

            return@Answer deribitResponseSuccess(
                DeribitTransactionResult(
                    count = transactionDataList.size,
                    data = transactionDataList.subList(offset, min(transactionDataList.size, offset + count))
                )
            )
        })
    }

    private fun setupDepositMock(transactions: Map<TransactionType, Map<String, List<DeribitTransactionData>>>) {
        `when`(
            api.getDeposits(any(), any(), any(), any())
        ).thenAnswer(Answer {
            val currency = it.arguments[1] as String
            val count = it.arguments[2] as Int
            val offset = it.arguments[3] as Int
            val transactionDataList =
                transactions.getOrDefault(TransactionType.Deposit, emptyMap()).getOrDefault(currency, emptyList())

            return@Answer deribitResponseSuccess(
                DeribitTransactionResult(
                    count = transactionDataList.size,
                    data = transactionDataList.subList(offset, min(transactionDataList.size, offset + count))
                )
            )
        })
    }

    private fun setupAccountSummaryMock(accountSummaryMap: Map<String, DeribitAccountSummaryResult>) {
        `when`(
            api.getAccountSummary(any(), any())
        ).thenAnswer(Answer {
            val currency = it.arguments[1] as String
            val data =
                accountSummaryMap[currency]!!

            return@Answer deribitResponseSuccess(data)
        })
    }

    private fun setupGetSubAccountsMock() {
        `when`(api.getSubAccounts(any(), any())).thenReturn(
            deribitResponseSuccess(
                listOf(
                    DeribitSubAccountResult(username = "Main", id = 1),
                    DeribitSubAccountResult(username = "SubAccount1", id = 2),
                    DeribitSubAccountResult(username = "SubAccount2", id = 3),
                )
            )
        )
    }

    private fun <T> deribitResponseSuccess(data: T): DeribitResponse<T> {
        return DeribitResponse<T>(0, "2.0", data, null)
    }

    private fun deribitTransactions(currency: String, count: Int): List<DeribitTransactionData> {
        return IntArray(count)
            .map {
                DeribitTransactionData(
                    address = UUID.randomUUID().toString(),
                    amount = BigDecimal.valueOf(0.1 * it),
                    currency = currency,
                    state = TransactionState.Pending,
                )
            }
    }
}