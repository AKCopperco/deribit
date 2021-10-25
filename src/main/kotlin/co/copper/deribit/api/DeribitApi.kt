package co.copper.deribit.api

import co.copper.deribit.dto.*
import co.copper.deribit.exception.DeribitException
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.math.BigDecimal

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

    @Throws(DeribitException::class)
    @GET("/api/v2/private/get_deposits")
    fun getDeposits(
        @Header("Authorization") authorization: String,
        @Query("currency") currency: String,
        @Query("count") count: Int,
        @Query("offset") offset: Int
    ): DeribitResponse<DeribitTransactionResult>

    @Throws(DeribitException::class)
    @GET("/api/v2/private/get_withdrawals")
    fun getWithdrawals(
        @Header("Authorization") authorization: String,
        @Query("currency") currency: String,
        @Query("count") count: Int,
        @Query("offset") offset: Int
    ): DeribitResponse<DeribitTransactionResult>

    @Throws(DeribitException::class)
    @GET("/api/v2/private/withdraw")
    fun withdraw(
        @Header("Authorization") authorization: String,
        @Query("currency") currency: String,
        @Query("amount") amount: BigDecimal,
        @Query("address") address: String,
    ): DeribitResponse<DeribitWithdrawResult>

    @Throws(DeribitException::class)
    @GET("/api/v2/private/submit_transfer_to_subaccount")
    fun transferToSubAccount(
        @Header("Authorization") authorization: String,
        @Query("currency") currency: String,
        @Query("amount") amount: BigDecimal,
        @Query("destination") destination: Int
    ): DeribitResponse<DeribitTransferResult>

    @Throws(DeribitException::class)
    @GET("/api/v2/private/get_subaccounts")
    fun getSubAccounts(
        @Header("Authorization") authorization: String,
        @Query("with_portfolio") withPortfolio: Boolean = false,
    ): DeribitResponse<List<DeribitSubAccountResult>>
}
