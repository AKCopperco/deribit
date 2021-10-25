package co.copper.deribit

import co.copper.deribit.api.DeribitApplicationApi
import co.copper.deribit.config.DeribitApplicationTestConfiguration
import co.copper.deribit.dto.WithdrawRequest
import co.copper.deribit.mock.DeribitApiMockDispatcher
import co.copper.deribit.model.UserBalance
import io.zonky.test.db.AutoConfigureEmbeddedDatabase
import okhttp3.mockwebserver.MockWebServer
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.create


@SpringBootTest(classes = [DeribitApplication::class], webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureEmbeddedDatabase
@Import(DeribitApplicationTestConfiguration::class)
class DeribitApplicationTests {
    @LocalServerPort
    private var port: Int = 0

    @Value("\${deribit.client_id}")
    private lateinit var deribitClientId: String

    @Value("\${deribit.client_secret}")
    private lateinit var deribitClientSecret: String

    @Autowired
    private lateinit var mockWebServer: MockWebServer

    private lateinit var applicationApi: DeribitApplicationApi

    @BeforeEach
    fun setUp() {
        applicationApi = Retrofit.Builder()
            .baseUrl("http://localhost:$port")
            .addConverterFactory(JacksonConverterFactory.create())
            .build()
            .create()
        mockWebServer.dispatcher = DeribitApiMockDispatcher(deribitClientId, deribitClientSecret)
    }

    @Test
    fun `Application context is initialized`() {
        assertNotNull(applicationApi)
    }

    @Test
    fun `Get User Balance success`() {
        val expected = listOf(
            UserBalance(
                currency = "USDT",
                balance = 0.toBigDecimal(),
                reserved = 0.toBigDecimal(),
                username = "AKcopperco"
            ),
            UserBalance(
                currency = "ETH",
                balance = 0.toBigDecimal(),
                reserved = 0.toBigDecimal(),
                username = "AKcopperco"
            ),
            UserBalance(
                currency = "BTC",
                balance = 8.1.toBigDecimal(),
                reserved = 0.0.toBigDecimal(),
                username = "AKcopperco"
            )
        )

        val response = applicationApi.balances(deribitClientId, deribitClientSecret).execute()
        val balances = response.body()

        assertTrue(response.isSuccessful)
        assertNotNull(balances)
        assertEquals(3, balances!!.size)

        assertTrue(balances.all { expected.indexOf(it) >= 0 })
    }

    @Test
    fun `Get User Balance invalid client_id returns BadRequest`() {
        val balancesResponse = applicationApi.balances("invalid", deribitClientSecret).execute()

        assertFalse(balancesResponse.isSuccessful)
        assertEquals(400, balancesResponse.code())
    }

    @Test
    fun `Get User Balance invalid client_secret returns BadRequest`() {
        val balancesResponse = applicationApi.balances(deribitClientId, "invalid").execute()

        assertFalse(balancesResponse.isSuccessful)
        assertEquals(400, balancesResponse.code())
    }

    @Test
    fun `Get User Balance invalid client_id and client_secret returns BadRequest`() {
        val balancesResponse = applicationApi.balances("invalid", "invalid").execute()

        assertFalse(balancesResponse.isSuccessful)
        assertEquals(400, balancesResponse.code())
    }

    @Test
    fun `Get Transactions success`() {
        val response = applicationApi.transactions(deribitClientId, deribitClientSecret).execute()
        val transactions = response.body()

        assertTrue(response.isSuccessful)
        assertNotNull(transactions)
        assertEquals(12, transactions!!.size)
    }

    @Test
    fun `Get Transactions invalid client_secret returns BadRequest`() {
        val balancesResponse = applicationApi.transactions(deribitClientId, "invalid").execute()

        assertFalse(balancesResponse.isSuccessful)
        assertEquals(400, balancesResponse.code())
    }

    @Test
    fun `Get Transactions invalid client_id returns BadRequest`() {
        val balancesResponse = applicationApi.transactions("invalid", deribitClientSecret).execute()

        assertFalse(balancesResponse.isSuccessful)
        assertEquals(400, balancesResponse.code())
    }

    @Test
    fun `Get Transactions invalid client_id and client_secret returns BadRequest`() {
        val balancesResponse = applicationApi.transactions("invalid", "invalid").execute()

        assertFalse(balancesResponse.isSuccessful)
        assertEquals(400, balancesResponse.code())
    }


    @Test
    fun `Withdraw to external address success`() {
        val request = WithdrawRequest(
            deribitClientId,
            deribitClientSecret,
            "BTC",
            0.01.toBigDecimal(),
            "2Mz9oJZ7MPD2Bhq2zXV6jMmKgc8JtXn9i7o"
        )

        val response = applicationApi.withdraw(request).execute()
        val result = response.body()

        assertTrue(response.isSuccessful)
        assertNotNull(result)
    }

    @Test
    fun `Withdraw - address not in address book returns BadRequest`() {
        val request = WithdrawRequest(
            deribitClientId,
            deribitClientSecret,
            "BTC",
            0.01.toBigDecimal(),
            "15xKGc8iuvRuz4ciKGs1zkHVLAjzkyLZE7"
        )
        val response = applicationApi.withdraw(request).execute()
        val result = response.body()

        assertFalse(response.isSuccessful)
        assertEquals(400, response.code())
    }

}

