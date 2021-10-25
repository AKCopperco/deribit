package co.copper.deribit.dto

import co.copper.deribit.model.TransferState
import co.copper.deribit.model.TransferType
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
data class DeribitTransferResult(
    @JsonProperty("amount") val amount: BigDecimal,
    @JsonProperty("other_side") val otherSide: String,
    @JsonProperty("currency") val currency: String,
    @JsonProperty("state") val state: TransferState,
    @JsonProperty("type") val type: TransferType,
    @JsonProperty("direction") val direction: String
)

