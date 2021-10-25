package co.copper.deribit.api

import co.copper.deribit.dto.TransferRequest
import co.copper.deribit.dto.TransferResult
import co.copper.deribit.dto.WithdrawRequest
import co.copper.deribit.dto.WithdrawResult
import co.copper.deribit.model.Transaction
import co.copper.deribit.model.UserBalance
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface DeribitApplicationApi {

    @GET("/api/balances")
    fun balances(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
    ): Call<List<UserBalance>>

    @GET("/api/transactions")
    fun transactions(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
    ): Call<List<Transaction>>

    @POST("/api/withdraw")
    fun withdraw(
        @Body request: WithdrawRequest,
    ): Call<WithdrawResult>

    @POST("/api/transfer-to-subaccount")
    fun transferToSubAccount(
        @Body request: TransferRequest
    ): Call<TransferResult>

}