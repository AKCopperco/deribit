package co.copper.deribit.service

import co.copper.deribit.api.DeribitApi
import co.copper.deribit.dto.DeribitAuthResult
import co.copper.deribit.dto.DeribitCurrencyResult
import co.copper.deribit.dto.DeribitTransactionData
import co.copper.deribit.exception.DeribitException
import co.copper.deribit.extension.toTransaction
import co.copper.deribit.extension.toUserBalance
import co.copper.deribit.model.Transaction
import co.copper.deribit.model.TransactionType
import co.copper.deribit.model.UserBalance
import org.springframework.stereotype.Service

private const val TRANSACTION_COUNT_PER_REQUEST: Int = 10

@Service
class DeribitApiService(private val deribitApi: DeribitApi) {

    @Throws(DeribitException::class)
    fun getAccountSummary(clientId: String, clientSecret: String): List<UserBalance> {
        val token = getBearerToken(clientId, clientSecret)
        val currencies = getCurrencies().map { it.currency }

        return currencies.map { currency ->
            deribitApi.getAccountSummary(token, currency)
                .result
                .toUserBalance()
        }
    }

    @Throws(DeribitException::class)
    fun getTransactions(clientId: String, clientSecret: String): Map<TransactionType, List<Transaction>> {
        val token = getBearerToken(clientId, clientSecret)
        val currencies = getCurrencies().map { it.currency }

        val deposits = getTransactionsByType(
            TransactionType.Deposit,
            currencies,
            token
        ).map { it.toTransaction(TransactionType.Deposit) }

        val withdrawals = getTransactionsByType(
            TransactionType.Withdraw,
            currencies,
            token
        ).map { it.toTransaction(TransactionType.Withdraw) }

        return mapOf(TransactionType.Deposit to deposits, TransactionType.Withdraw to withdrawals)
    }


    private fun getBearerToken(clientId: String, clientSecret: String) =
        "Bearer ".plus(auth(clientId, clientSecret).access_token)

    private fun auth(clientId: String, clientSecret: String): DeribitAuthResult =
        deribitApi
            .auth(clientId, clientSecret)
            .result

    private fun getCurrencies(): List<DeribitCurrencyResult> =
        deribitApi
            .getCurrencies()
            .result

    private fun getTransactionsByType(type: TransactionType, currencies: List<String>, token: String) =
        currencies.flatMap { currency -> getAllTransactionsByType(type, currency, token) }

    private fun getAllTransactionsByType(
        type: TransactionType,
        currency: String,
        token: String
    ): List<DeribitTransactionData> {
        val apiMethod = when (type) {
            TransactionType.Deposit -> DeribitApi::getDeposits
            TransactionType.Withdraw -> DeribitApi::getWithdrawals
        }

        var offset = 0
        val transactionsForCurrency = mutableListOf<DeribitTransactionData>()
        do {
            val result = apiMethod.invoke(deribitApi, token, currency, TRANSACTION_COUNT_PER_REQUEST, offset)
                .result

            transactionsForCurrency += result.data
            offset += TRANSACTION_COUNT_PER_REQUEST
        } while (result.count > offset)

        return transactionsForCurrency
    }


}