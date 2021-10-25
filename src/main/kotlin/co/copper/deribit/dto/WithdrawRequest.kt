package co.copper.deribit.dto

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class WithdrawRequest(
    @JsonProperty val clientId: String,
    @JsonProperty val clientSecret: String,
    @JsonProperty val currency: String,
    @JsonProperty val amount: BigDecimal,
    @JsonProperty val address: String,
)