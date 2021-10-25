package co.copper.deribit.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DeribitErrorResponse(
    @JsonProperty("id") var id: Int,
    @JsonProperty("jsonrpc") val jsonrpc: String,
    @JsonProperty("error") var error: DeribitErrorObject?
)