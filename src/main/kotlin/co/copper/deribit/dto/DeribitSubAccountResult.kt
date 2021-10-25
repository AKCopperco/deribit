package co.copper.deribit.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DeribitSubAccountResult(
    @JsonProperty("username") val username: String,
    @JsonProperty("id") val id: Int,
)