package com.netcast.radio.request

import androidx.viewbinding.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RemoteDataSource {
    companion object {
    }

    fun <Api> buildApi(api: Class<Api>): Api {
        return Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .client(OkHttpClient.Builder().also { client ->
                if (BuildConfig.DEBUG) {
                    val logginInt = HttpLoggingInterceptor();
                    logginInt.setLevel(HttpLoggingInterceptor.Level.BODY)
                    client.addInterceptor(logginInt)
                }
            }.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }

    fun <Api> buildAuthenticatedApi(api: Class<Api>, authToken: String? = null): Api {
        return Retrofit.Builder()
            .baseUrl(AppConstants.BASE_URL)
            .client(
                OkHttpClient.Builder().addInterceptor { chain ->
                    chain.proceed(chain.request().newBuilder().also {
                        it.addHeader("Authorization", "Bearer$authToken")
                    }.build())

                }.also { client ->
                    if (BuildConfig.DEBUG) {
                        val logginInt = HttpLoggingInterceptor();
                        logginInt.setLevel(HttpLoggingInterceptor.Level.BODY)
                        client.addInterceptor(logginInt)
                    }
                }.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(api)
    }

}