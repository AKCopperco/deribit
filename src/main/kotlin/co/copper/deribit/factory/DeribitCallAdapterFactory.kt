package co.copper.deribit.factory

import co.copper.deribit.dto.DeribitErrorResponse
import co.copper.deribit.dto.DeribitResponse
import co.copper.deribit.exception.DeribitException
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.Type

class DeribitCallAdapterFactory : CallAdapter.Factory() {
    override fun get(returnType: Type, annotations: Array<out Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        return if (getRawType(returnType) == DeribitResponse::class.java) {
            DeribitCallAdapter<Any>(returnType)
        } else {
            null
        }
    }
}

class DeribitCallAdapter<R>(private val responseType: Type) : CallAdapter<DeribitResponse<R>, DeribitResponse<R>> {

    override fun responseType(): Type {
        return responseType
    }

    override fun adapt(call: Call<DeribitResponse<R>>): DeribitResponse<R> {
        val response = call.execute()
        if (response.isSuccessful) {
            val result = response.body()!!

            result.error?.let {
                throw DeribitException(it.code, it.message)
            }
            return result
        } else {
            jacksonObjectMapper()
                .readValue(response.errorBody()!!.string(), DeribitErrorResponse::class.java)
                .error
                .let { error ->
                    throw  DeribitException(error!!.code, error.message)
                }
        }
    }
}
