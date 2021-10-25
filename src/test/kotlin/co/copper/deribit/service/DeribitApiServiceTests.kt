package co.copper.deribit.service

import co.copper.deribit.api.DeribitApi
import co.copper.deribit.dto.DeribitAccountSummaryResult
import co.copper.deribit.dto.DeribitAuthResult
import co.copper.deribit.dto.DeribitCurrencyResult
import co.copper.deribit.dto.DeribitResponse
import co.copper.deribit.exception.DeribitException
import co.copper.deribit.model.UserBalance
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.*
import org.mockito.stubbing.Answer
import java.util.*

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

    private fun <T> deribitResponseSuccess(data: T): DeribitResponse<T> {
        return DeribitResponse<T>(0, "2.0", data, null)
    }

}