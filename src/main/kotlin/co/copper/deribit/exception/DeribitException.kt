package co.copper.deribit.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus

@ResponseStatus(HttpStatus.BAD_REQUEST, reason = "Some parameters are invalid")
class DeribitException(val code: Int, message: String) : Exception(message)