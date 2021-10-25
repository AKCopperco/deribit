package co.copper.deribit.api

import co.copper.deribit.model.Transaction
import co.copper.deribit.model.UserBalance
import retrofit2.Call
import retrofit2.http.GET
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
}