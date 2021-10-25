package co.copper.deribit.extension

import co.copper.deribit.dto.DeribitAccountSummaryResult
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class ExtensionsTests {
    @Test
    fun `DeribitAccountSummaryResult to UserBalance`() {
        val value = DeribitAccountSummaryResult(
            currency = "BTC",
            balance = 10.0.toBigDecimal(),
            availableWithdrawalFunds = 8.0.toBigDecimal(),
            systemName = "username"
        )

        val result = value.toUserBalance()

        assertEquals(value.balance, result.balance)
        assertEquals(value.systemName, result.username)
        assertEquals(value.currency, result.currency)
        assertEquals(value.balance - value.availableWithdrawalFunds, result.reserved)
    }

}