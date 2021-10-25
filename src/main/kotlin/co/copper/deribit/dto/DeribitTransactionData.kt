package co.copper.deribit.dto

import co.copper.deribit.model.TransactionState
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class DeribitTransactionData(
    @JsonProperty("address") var address: String,
    @JsonProperty("amount") var amount: BigDecimal,
    @JsonProperty("currency") var currency: String,
    @JsonProperty("state") var state: TransactionState,
)