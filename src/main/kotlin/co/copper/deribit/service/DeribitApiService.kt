package co.copper.deribit.service

import co.copper.deribit.api.DeribitApi
import co.copper.deribit.dto.DeribitAuthResult
import co.copper.deribit.dto.DeribitCurrencyResult
import co.copper.deribit.exception.DeribitException
import co.copper.deribit.extension.toUserBalance
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

}