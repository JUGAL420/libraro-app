package com.techito.libraro.data.remote

import android.util.Log
import com.techito.libraro.BuildConfig
import com.techito.libraro.LibraroApp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * RetrofitClient provides a singleton instance of ApiService.
 * It includes security interceptors for Auth Tokens and Logging.
 */
object RetrofitClient {

    private val loggingInterceptor = HttpLoggingInterceptor { message ->
        if (BuildConfig.DEBUG) {
            // Replaces ampersands with new lines for easier debugging of query params
            Log.d("okhttpRes :: -> ", message.replace("&", "\n"))
        }
    }.apply {
        // Only log body in Debug mode for security
        level = if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor.Level.BODY
        } else {
            HttpLoggingInterceptor.Level.NONE
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val original = chain.request()
            val requestBuilder = original.newBuilder()
                .addHeader("lc", ApiConstants.LANGUAGE)
                .addHeader("Accept", "application/json")
                .addHeader("Content-Type", "application/json")
                .addHeader("device-type", "android")

            // Security: Automatically add Auth Token if available in DataStore
            // Interceptors are synchronous, so we use runBlocking to fetch from DataStore
            val token = runBlocking { LibraroApp.preferenceManager.authToken.first() }
            
            if (!token.isNullOrEmpty()) {
                requestBuilder.addHeader("Authorization", "Bearer $token")
            } else {
                // If no token, use a fallback API key if defined in build.gradle
                 requestBuilder.addHeader("x-api-key", BuildConfig.API_TOKEN)
            }

            chain.proceed(requestBuilder.build())
        }
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .writeTimeout(1, TimeUnit.MINUTES)
        .retryOnConnectionFailure(true)
        .connectionPool(ConnectionPool(5, 5, TimeUnit.MINUTES))
        .protocols(listOf(Protocol.HTTP_2, Protocol.HTTP_1_1))
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(ApiConstants.BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}
