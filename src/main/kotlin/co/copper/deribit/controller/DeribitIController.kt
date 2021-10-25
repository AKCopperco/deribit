package co.copper.deribit.controller

import co.copper.deribit.model.UserBalance
import co.copper.deribit.service.DeribitService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class DeribitController(private val deribitService: DeribitService) {

    @GetMapping("/api/balances")
    fun getAccountSummary(
        @RequestParam("client_id") clientId: String,
        @RequestParam("client_secret") clientSecret: String
    ): List<UserBalance> {
        return deribitService.getAccountSummary(clientId, clientSecret)
    }

}