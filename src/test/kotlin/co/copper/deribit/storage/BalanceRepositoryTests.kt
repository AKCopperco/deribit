package co.copper.deribit.storage

import co.copper.deribit.model.UserBalance
import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate
import org.springframework.test.context.jdbc.Sql


@JdbcTest
@AutoConfigureEmbeddedDatabase
@Sql("/db.migration/UserBalanceTestData.sql")
class BalanceRepositoryTests {

    @Autowired
    private lateinit var jdbcTemplate: NamedParameterJdbcTemplate

    private lateinit var balanceRepository: BalanceRepository

    @BeforeEach
    fun setUp() {
        balanceRepository = BalanceRepository(jdbcTemplate)
    }

    @Test
    fun `Repository created`() {
        assertNotNull(balanceRepository)
    }

    @Test
    fun `Get User Balance for unknown pair of username and currency returns null`() {
        assertNull(balanceRepository.getUserBalance("abc", "BTC"))
    }

    @Test
    fun `Get User Balance for existing user for BTC returns correct values`() {
        val btcBalance = balanceRepository.getUserBalance("username1", "BTC")

        assertEquals(
            UserBalance(
                balance = 10.0.toBigDecimal(),
                username = "username1",
                currency = "BTC",
                reserved = 2.0.toBigDecimal()
            ), btcBalance
        )
    }

    @Test
    fun `Get User Balance for existing user for ETH returns correct values`() {
        val ethBalance = balanceRepository.getUserBalance("username1", "ETH")

        assertEquals(
            UserBalance(
                balance = 20.0.toBigDecimal(),
                username = "username1",
                currency = "ETH",
                reserved = 0.0.toBigDecimal()
            ), ethBalance
        )
    }

    @Test
    fun `Get User Balance for existing user for USDT returns correct values`() {
        val usdtBalance = balanceRepository.getUserBalance("username1", "USDT")

        assertEquals(
            UserBalance(
                balance = 30.0.toBigDecimal(),
                username = "username1",
                currency = "USDT",
                reserved = 3.0.toBigDecimal()
            ), usdtBalance
        )
    }


    @Test
    fun `Get User Balance return null on first check and get created by update call`() {
        val initialBtcBalance = balanceRepository.getUserBalance("username2", "BTC")
        val balanceToStore = UserBalance(
            balance = 100.5.toBigDecimal(),
            username = "username2",
            currency = "BTC",
            reserved = 15.6.toBigDecimal()
        )
        balanceRepository.update(balanceToStore)
        val balanceAfterStore = balanceRepository.getUserBalance("username2", "BTC")


        assertNull(initialBtcBalance)
        assertNotNull(balanceAfterStore)
        assertEquals(balanceToStore, balanceAfterStore)
    }

    @Test
    fun `Get User Balance return initial value on first check and get updated by update call`() {
        val expectedInitialBalance = UserBalance(
            balance = 250.5.toBigDecimal(),
            username = "username3",
            currency = "BTC",
            reserved = 55.6.toBigDecimal()
        )

        val initialBtcBalance = balanceRepository.getUserBalance("username3", "BTC")
        val balanceToStore = UserBalance(
            balance = 350.5.toBigDecimal(),
            username = "username3",
            currency = "BTC",
            reserved = 65.6.toBigDecimal()
        )
        balanceRepository.update(balanceToStore)
        val balanceAfterStore = balanceRepository.getUserBalance("username3", "BTC")


        assertNotNull(initialBtcBalance)
        assertEquals(expectedInitialBalance, initialBtcBalance)
        assertNotNull(balanceAfterStore)
        assertEquals(balanceToStore, balanceAfterStore)
    }


}