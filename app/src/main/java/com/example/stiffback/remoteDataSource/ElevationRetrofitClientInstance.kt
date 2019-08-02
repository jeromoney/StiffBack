package com.example.stiffback.remoteDataSource

import java.util.concurrent.TimeUnit

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ElevationRetrofitClientInstance {

    lateinit var retrofit: Retrofit
    private val BASE_URL = "https://nationalmap.gov"

    // set custom timeouts
    val retrofitInstance: Retrofit
        get() {
            val TIMEOUT = 30
            val client = OkHttpClient.Builder()
            client.connectTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
            client.readTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)
            client.writeTimeout(TIMEOUT.toLong(), TimeUnit.SECONDS)



            retrofit = retrofit2.Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client.build())
                    .build()
            return retrofit
        }
}