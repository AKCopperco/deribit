package co.copper.deribit.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DeribitErrorObject(
    @JsonProperty("code") var code: Int,
    @JsonProperty("message") val message: String,
)