package co.copper.deribit.dto

import co.copper.deribit.model.WithdrawalState
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class DeribitWithdrawResult(
    @JsonProperty("address") var address: String,
    @JsonProperty("amount") val amount: BigDecimal,
    @JsonProperty("currency") val currency: String,
    @JsonProperty("state") val state: WithdrawalState,
)


