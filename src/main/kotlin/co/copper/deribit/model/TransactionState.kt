package co.copper.deribit.model

import com.fasterxml.jackson.annotation.JsonValue

enum class TransactionState(@JsonValue val value: String) {
    Pending("pending"),
    Completed("completed"),
    Rejected("rejected"),
    Replaced("replaced"),
    Unconfirmed("unconfirmed")
}

