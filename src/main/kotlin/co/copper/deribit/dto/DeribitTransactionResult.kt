package co.copper.deribit.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DeribitTransactionResult(
    @JsonProperty("count") var count: Int,
    @JsonProperty("data") var data: List<DeribitTransactionData>,
)
