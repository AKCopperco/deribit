package co.copper.deribit.extension

import co.copper.deribit.dto.DeribitAccountSummaryResult
import co.copper.deribit.dto.DeribitTransactionData
import co.copper.deribit.model.TransactionState
import co.copper.deribit.model.TransactionType
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

    @Test
    fun `DeribitTransactionData to Transaction - Withdraw`() {
        val value = DeribitTransactionData(
            address = "Some Address 1",
            amount = 10.0.toBigDecimal(),
            currency = "BTC",
            state = TransactionState.Completed,
        )

        val result = value.toTransaction(TransactionType.Withdraw)
        assertEquals(value.address, result.address)
        assertEquals(value.amount, result.amount)
        assertEquals(value.currency, result.currency)
        assertEquals(value.state, result.state)
        assertEquals(TransactionType.Withdraw, result.type)
    }

    @Test
    fun `DeribitTransactionData to Transaction - Deposit`() {
        val value = DeribitTransactionData(
            address = "Some Address 1",
            amount = 10.0.toBigDecimal(),
            currency = "BTC",
            state = TransactionState.Completed,
        )

        val result = value.toTransaction(TransactionType.Deposit)
        assertEquals(value.address, result.address)
        assertEquals(value.amount, result.amount)
        assertEquals(value.currency, result.currency)
        assertEquals(value.state, result.state)
        assertEquals(TransactionType.Deposit, result.type)

    }

}