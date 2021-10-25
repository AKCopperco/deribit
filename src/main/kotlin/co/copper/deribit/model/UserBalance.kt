package co.copper.deribit.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class UserBalance(
    @JsonProperty("username") var username: String,
    @JsonProperty("currency") val currency: String,
    @JsonProperty("balance") val balance: BigDecimal,
    @JsonProperty("reserved") val reserved: BigDecimal
)
