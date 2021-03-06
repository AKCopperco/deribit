package co.copper.deribit.mock

import co.copper.deribit.helper.TestHelper
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.RecordedRequest

class DeribitApiMockDispatcher(
    private val clientId: String,
    private val clientSecret: String,
) : Dispatcher() {

    override fun dispatch(request: RecordedRequest): MockResponse {
        request.requestUrl.let { requestUrl ->
            return when (requestUrl.url().path) {
                "/api/v2/public/auth" -> {
                    if (requestUrl.queryParameter("client_id") == clientId && requestUrl.queryParameter("client_secret") == clientSecret) {
                        MockResponse()
                            .setResponseCode(200)
                            .setBody(TestHelper.apiResponseResourceAsText("auth-response-success.json"))
                    } else {
                        MockResponse()
                            .setResponseCode(200)
                            .setBody(TestHelper.apiResponseResourceAsText("auth-response-invalid-credentials.json"))
                    }
                }
                "/api/v2/public/get_currencies" -> MockResponse()
                    .setResponseCode(200)
                    .setBody(TestHelper.apiResponseResourceAsText("get-currencies-response-success.json"))

                "/api/v2/private/get_account_summary" ->
                    requestUrl.queryParameter("currency").let { currency ->
                        MockResponse().setResponseCode(200)
                            .setBody(TestHelper.apiResponseResourceAsText("get-account-summary-${currency!!.lowercase()}-success.json"))
                    }
                "/api/v2/private/get_deposits" -> {
                    val currency = requestUrl.queryParameter("currency")!!
                    val offset = requestUrl.queryParameter("offset")!!
                    MockResponse().setResponseCode(200)
                        .setBody(TestHelper.apiResponseResourceAsText("get-deposits-${currency.lowercase()}-offset-${offset}-success.json"))
                }
                "/api/v2/private/get_withdrawals" -> {
                    val currency = requestUrl.queryParameter("currency")!!
                    val offset = requestUrl.queryParameter("offset")!!
                    MockResponse().setResponseCode(200)
                        .setBody(TestHelper.apiResponseResourceAsText("get-withdrawals-${currency.lowercase()}-offset-${offset}-success.json"))
                }
                "/api/v2/private/withdraw" -> {
                    val currency = requestUrl.queryParameter("currency")!!
                    val address = requestUrl.queryParameter("address")!!

                    MockResponse().setResponseCode(200)
                        .setBody(TestHelper.apiResponseResourceAsText("withdraw-${currency.lowercase()}-${address}.json"))
                }
                "/api/v2/private/get_subaccounts" -> {
                    MockResponse().setResponseCode(200)
                        .setBody(TestHelper.apiResponseResourceAsText("get-subaccounts-success.json"))
                }
                "/api/v2/private/submit_transfer_to_subaccount" -> {
                    val currency = requestUrl.queryParameter("currency")!!
                    val destination = requestUrl.queryParameter("destination")!!

                    MockResponse().setResponseCode(200)
                        .setBody(TestHelper.apiResponseResourceAsText("submit_transfer_to_subaccount-${currency.lowercase()}-${destination}-success.json"))
                }
                else -> {
                    throw IllegalArgumentException("Unknown request")
                }
            }
        }
    }
}

