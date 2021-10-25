package co.copper.deribit.model

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class Transaction(
    @JsonProperty("address") val address: String,
    @JsonProperty("amount") val amount: BigDecimal,
    @JsonProperty("currency") val currency: String,
    @JsonProperty("state") val state: TransactionState,
    @JsonProperty("type") val type: TransactionType
)

