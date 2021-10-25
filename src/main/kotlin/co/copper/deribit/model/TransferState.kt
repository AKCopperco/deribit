package co.copper.deribit.model

import com.fasterxml.jackson.annotation.JsonValue

enum class TransferState(@JsonValue val value: String) {
    Prepared("prepared"),
    Confirmed("confirmed"),
    Cancelled("cancelled"),
    WaitingForAdmin("waiting_for_admin"),
    RejectionReason("rejection_reason");
}