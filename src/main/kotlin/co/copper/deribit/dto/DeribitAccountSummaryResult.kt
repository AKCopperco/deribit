package co.copper.deribit.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal


@JsonIgnoreProperties(ignoreUnknown = true)
data class DeribitAccountSummaryResult(
    @JsonProperty("currency") var currency: String,
    @JsonProperty("balance") var balance: BigDecimal,
    @JsonProperty("available_withdrawal_funds") var availableWithdrawalFunds: BigDecimal,
    @JsonProperty("system_name") var systemName: String,
)