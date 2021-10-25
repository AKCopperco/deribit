package co.copper.deribit.extension

import co.copper.deribit.dto.*
import co.copper.deribit.model.Transaction
import co.copper.deribit.model.TransactionType
import co.copper.deribit.model.UserBalance

fun DeribitAccountSummaryResult.toUserBalance(): UserBalance =
    UserBalance(this.systemName, this.currency, this.balance, this.balance - this.availableWithdrawalFunds)

fun DeribitTransactionData.toTransaction(type: TransactionType) =
    Transaction(this.address, this.amount, this.currency, this.state, type)

fun DeribitWithdrawResult.toWithdrawResult() = WithdrawResult(address, amount, currency, state)

fun DeribitTransferResult.toTransferResult() = TransferResult(amount, otherSide, currency, state, type, direction)
