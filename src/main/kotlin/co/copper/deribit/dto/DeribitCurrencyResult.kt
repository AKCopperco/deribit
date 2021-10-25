package co.copper.deribit.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

@JsonIgnoreProperties(ignoreUnknown = true)
data class DeribitCurrencyResult(@JsonProperty("currency") var currency: String)

