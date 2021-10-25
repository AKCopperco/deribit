package co.copper.deribit.service

import co.copper.deribit.model.UserBalance
import co.copper.deribit.storage.BalanceRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.times
import org.mockito.kotlin.verify
import java.util.*

@ExtendWith(MockitoExtension::class)
class DeribitServiceTests {
    private lateinit var service: DeribitService

    @Mock
    private lateinit var apiService: DeribitApiService

    @Mock
    private lateinit var repository: BalanceRepository

    private lateinit var clientId: String
    private lateinit var clientSecret: String

    @BeforeEach
    fun setUp() {
        clientId = UUID.randomUUID().toString()
        clientSecret = UUID.randomUUID().toString()
        service = DeribitService(apiService, repository)
    }

    @Test
    fun `Service is created`() {
        assertNotNull(service)
    }

    @Test
    fun `Get Account Summary success`() {
        `when`(apiService.getAccountSummary(clientId, clientSecret)).thenReturn(mockAccountSummary())

        assertDoesNotThrow { service.getAccountSummary(clientId, clientSecret) }
    }

    @Test
    fun `Get Account Summary - api service get called`() {
        service.getAccountSummary(clientId, clientSecret)

        verify(apiService, times(1)).getAccountSummary(clientId, clientSecret)
    }

    @Test
    fun `Get Account Summary - zero currencies -  correct values are returned`() {
        `when`(apiService.getAccountSummary(clientId, clientSecret)).thenReturn(emptyList())

        val result = service.getAccountSummary(clientId, clientSecret)

        assertEquals(0, result.size)
    }

    @Test
    fun `Get Account Summary - two currencies -  correct values are returned`() {
        val mockAccountSummary = mockAccountSummary()
        `when`(apiService.getAccountSummary(clientId, clientSecret)).thenReturn(mockAccountSummary)

        val result = service.getAccountSummary(clientId, clientSecret)

        assertEquals(2, result.size)
        assertEquals(mockAccountSummary[0], result[0])
        assertEquals(mockAccountSummary[1], result[1])
    }

    @Test
    fun `Get Account Summary - two currencies - initial save to storage`() {
        val mockAccountSummary = mockAccountSummary()
        `when`(apiService.getAccountSummary(clientId, clientSecret)).thenReturn(mockAccountSummary)

        service.getAccountSummary(clientId, clientSecret)

        verify(repository, times(1)).getUserBalance(mockAccountSummary[0].username, mockAccountSummary[0].currency)
        verify(repository, times(1)).update(mockAccountSummary[0])

        verify(repository, times(1)).getUserBalance(mockAccountSummary[1].username, mockAccountSummary[1].currency)
        verify(repository, times(1)).update(mockAccountSummary[1])

        verify(repository, times(2)).getUserBalance(any(), any())
        verify(repository, times(2)).update(any())
    }

    @Test
    fun `Get Account Summary - two currencies - both balances changed`() {
        val mockAccountSummary = mockAccountSummary()
        val firstSummary = mockAccountSummary.first()
        val secondSummary = mockAccountSummary[1]

        `when`(apiService.getAccountSummary(clientId, clientSecret)).thenReturn(mockAccountSummary)
        `when`(
            repository.getUserBalance(
                firstSummary.username,
                firstSummary.currency
            )
        ).thenReturn(firstSummary.copy(balance = 12.0.toBigDecimal()))

        `when`(
            repository.getUserBalance(
                secondSummary.username,
                secondSummary.currency
            )
        ).thenReturn(secondSummary.copy(balance = 13.0.toBigDecimal()))

        service.getAccountSummary(clientId, clientSecret)

        verify(repository, times(1)).getUserBalance(firstSummary.username, firstSummary.currency)
        verify(repository, times(1)).update(firstSummary)

        verify(repository, times(1)).getUserBalance(secondSummary.username, secondSummary.currency)
        verify(repository, times(1)).update(secondSummary)

        verify(repository, times(2)).getUserBalance(any(), any())
        verify(repository, times(2)).update(any())
    }

    @Test
    fun `Get Account Summary - two currencies - both balances not changed`() {
        val mockAccountSummary = mockAccountSummary()
        val firstSummary = mockAccountSummary.first()
        val secondSummary = mockAccountSummary[1]

        `when`(apiService.getAccountSummary(any(), any())).thenReturn(mockAccountSummary)
        `when`(
            repository.getUserBalance(
                firstSummary.username,
                firstSummary.currency
            )
        ).thenReturn(firstSummary)

        `when`(
            repository.getUserBalance(
                secondSummary.username,
                secondSummary.currency
            )
        ).thenReturn(secondSummary)

        service.getAccountSummary(clientId, clientSecret)

        verify(repository, times(1)).getUserBalance(firstSummary.username, firstSummary.currency)
        verify(repository, times(0)).update(firstSummary)

        verify(repository, times(1)).getUserBalance(secondSummary.username, secondSummary.currency)
        verify(repository, times(0)).update(secondSummary)

        verify(repository, times(2)).getUserBalance(any(), any())
        verify(repository, times(0)).update(any())
    }


    @Test
    fun `Get Account Summary - two currencies - first balance not changed, second balance changed`() {
        val mockAccountSummary = mockAccountSummary()
        val firstSummary = mockAccountSummary.first()
        val secondSummary = mockAccountSummary[1]

        `when`(apiService.getAccountSummary(clientId, clientSecret)).thenReturn(mockAccountSummary)
        `when`(
            repository.getUserBalance(
                firstSummary.username,
                firstSummary.currency
            )
        ).thenReturn(firstSummary)

        `when`(
            repository.getUserBalance(
                secondSummary.username,
                secondSummary.currency
            )
        ).thenReturn(secondSummary.copy(balance = 33.0.toBigDecimal()))

        service.getAccountSummary(clientId, clientSecret)

        verify(repository, times(1)).getUserBalance(firstSummary.username, firstSummary.currency)
        verify(repository, times(0)).update(firstSummary)

        verify(repository, times(1)).getUserBalance(secondSummary.username, secondSummary.currency)
        verify(repository, times(1)).update(secondSummary)

        verify(repository, times(2)).getUserBalance(any(), any())
        verify(repository, times(1)).update(any())
    }

    private fun mockAccountSummary(): List<UserBalance> {
        return listOf(
            UserBalance(
                currency = "BTC",
                balance = 10.0.toBigDecimal(),
                reserved = 8.0.toBigDecimal(),
                username = "username"
            ),
            UserBalance(
                currency = "ETH",
                balance = 15.5.toBigDecimal(),
                reserved = 0.0.toBigDecimal(),
                username = "username"
            )
        )
    }


}