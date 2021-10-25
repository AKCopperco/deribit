package co.copper.deribit.controller

import co.copper.deribit.dto.TransferRequest
import co.copper.deribit.dto.WithdrawRequest
import co.copper.deribit.model.Transaction
import co.copper.deribit.model.UserBalance
import co.copper.deribit.service.DeribitService
import org.springframework.web.bind.annotation.*

@RestController
class DeribitController(private val deribitService: DeribitService) {

    @GetMapping("/api/balances")
    fun getAccountSummary(
        @RequestParam("client_id") clientId: String,
        @RequestParam("client_secret") clientSecret: String
    ): List<UserBalance> {
        return deribitService.getAccountSummary(clientId, clientSecret)
    }

    @GetMapping("/api/transactions")
    fun getTransactions(
        @RequestParam("client_id") clientId: String,
        @RequestParam("client_secret") clientSecret: String
    ): List<Transaction> {
        return deribitService.getTransactions(clientId, clientSecret)
    }

    @PostMapping("/api/withdraw")
    fun withdraw(
        @RequestBody request: WithdrawRequest
    ) = deribitService.withdraw(
        request.clientId,
        request.clientSecret,
        request.currency,
        request.amount,
        request.address
    )

    @PostMapping("/api/transfer-to-subaccount")
    fun transferToSubAccount(
        @RequestBody request: TransferRequest
    ) = deribitService.transferToSubAccount(
        request.clientId,
        request.clientSecret,
        request.currency,
        request.amount,
        request.username
    )
}