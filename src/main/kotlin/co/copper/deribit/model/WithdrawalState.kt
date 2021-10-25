package co.copper.deribit.model

import com.fasterxml.jackson.annotation.JsonValue

enum class WithdrawalState(@JsonValue val value: String) {
    Unconfirmed("unconfirmed"),
    Confirmed("confirmed"),
    Cancelled("cancelled"),
    Completed("completed"),
    Interrupted("interrupted"),
    Rejected("rejected"),
}