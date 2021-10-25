package co.copper.deribit.model

import com.fasterxml.jackson.annotation.JsonValue

enum class TransferType(@JsonValue val value: String) {
    User("user"),
    SubAccount("subaccount"),
}