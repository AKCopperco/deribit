package co.copper.deribit.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DeribitResponse<T>(
    @JsonProperty("id") var id: Int,
    @JsonProperty("jsonrpc") val jsonrpc: String,
    @JsonProperty("result") var result: T,
    @JsonProperty("error") var error: DeribitErrorObject?
)