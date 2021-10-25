package co.copper.deribit.config

import co.copper.deribit.api.DeribitApi
import co.copper.deribit.factory.DeribitCallAdapterFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.create

@Configuration
class AppConfig {

    @Value("\${deribit.baseUrl}")
    private lateinit var baseUrl: String

    @Bean
    fun deribitClient(): DeribitApi = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(JacksonConverterFactory.create())
        .addCallAdapterFactory(DeribitCallAdapterFactory())
        .build()
        .create()
}