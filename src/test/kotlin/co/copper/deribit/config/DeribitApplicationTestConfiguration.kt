package co.copper.deribit.config

import co.copper.deribit.api.DeribitApi
import co.copper.deribit.factory.DeribitCallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.MockWebServer
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

@TestConfiguration
class DeribitApplicationTestConfiguration {

    @Bean
    fun mockWebServer(): MockWebServer {
        return MockWebServer()
    }

    @Bean
    @Primary
    fun deribitTestClient(@Autowired mockWebServer: MockWebServer): DeribitApi {

        return Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .client(client)
            .addConverterFactory(JacksonConverterFactory.create())
            .addCallAdapterFactory(DeribitCallAdapterFactory())
            .build()
            .create(DeribitApi::class.java)
    }


    private val client = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .build()
}