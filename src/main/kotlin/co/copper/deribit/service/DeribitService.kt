package co.copper.deribit.service

import co.copper.deribit.model.Transaction
import co.copper.deribit.model.UserBalance
import co.copper.deribit.storage.BalanceRepository
import org.springframework.stereotype.Service

@Service
class DeribitService(
    private val deribitApiService: DeribitApiService,
    private val userBalanceRepository: BalanceRepository
) {

    fun getAccountSummary(clientId: String, clientSecret: String) =
        deribitApiService.getAccountSummary(clientId, clientSecret)
            .onEach { storeBalance(it) }


    fun getTransactions(clientId: String, clientSecret: String): List<Transaction> =
        deribitApiService.getTransactions(clientId, clientSecret)
            .flatMap { entry -> entry.value }

    private fun storeBalance(balance: UserBalance) {
        if (userBalanceRepository.getUserBalance(balance.username, balance.currency) != balance) {
            userBalanceRepository.update(balance)
        }
    }

}