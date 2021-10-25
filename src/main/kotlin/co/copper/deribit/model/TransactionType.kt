package co.copper.deribit.model

import com.fasterxml.jackson.annotation.JsonValue

enum class TransactionType(@JsonValue val value: String) {
    Deposit("deposit"),
    Withdraw("withdraw")
}