package co.copper.deribit.extension

import co.copper.deribit.dto.DeribitAccountSummaryResult
import co.copper.deribit.model.UserBalance

fun DeribitAccountSummaryResult.toUserBalance(): UserBalance =
    UserBalance(this.systemName, this.currency, this.balance, this.balance - this.availableWithdrawalFunds)
