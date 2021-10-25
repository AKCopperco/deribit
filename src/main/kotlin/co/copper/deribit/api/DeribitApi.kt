package co.copper.deribit.api

import co.copper.deribit.dto.DeribitAccountSummaryResult
import co.copper.deribit.dto.DeribitAuthResult
import co.copper.deribit.dto.DeribitCurrencyResult
import co.copper.deribit.dto.DeribitResponse
import co.copper.deribit.exception.DeribitException
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface DeribitApi {

    @Throws(DeribitException::class)
    @GET("/api/v2/private/get_account_summary?extended=true")
    fun getAccountSummary(
        @Header("Authorization") authorization: String,
        @Query("currency") currency: String
    ): DeribitResponse<DeribitAccountSummaryResult>

    @Throws(DeribitException::class)
    @GET("/api/v2/public/get_currencies")
    fun getCurrencies(): DeribitResponse<List<DeribitCurrencyResult>>

    @Throws(DeribitException::class)
    @GET("/api/v2/public/auth")
    fun auth(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("grant_type") grantType: String = "client_credentials"
    ): DeribitResponse<DeribitAuthResult>

}
