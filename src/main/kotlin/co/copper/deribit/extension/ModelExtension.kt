package co.copper.deribit.extension

import co.copper.deribit.dto.DeribitAccountSummaryResult
import co.copper.deribit.dto.DeribitTransactionData
import co.copper.deribit.model.Transaction
import co.copper.deribit.model.TransactionType
import co.copper.deribit.model.UserBalance

fun DeribitAccountSummaryResult.toUserBalance(): UserBalance =
    UserBalance(this.systemName, this.currency, this.balance, this.balance - this.availableWithdrawalFunds)

fun DeribitTransactionData.toTransaction(type: TransactionType) =
    Transaction(this.address, this.amount, this.currency, this.state, type)

