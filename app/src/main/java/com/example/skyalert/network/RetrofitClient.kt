package com.example.skyalert.network

import android.util.Log
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://api.openweathermap.org/data/2.5/"
    val apiService: ApiService by lazy {


        // retrofit client with interceptor
        val client = OkHttpClient.Builder()
            .addInterceptor { chain ->
                val original = chain.request()
                val originalHttpUrl = original.url()
                val url = originalHttpUrl.newBuilder()
                    .addQueryParameter("appid", "fff516e2c3c9188a401a978960382b36")
                    .build()
                val requestBuilder = original.newBuilder().url(url)
                val request = requestBuilder.build()
                Log.d("RetrofitClient", "request: ${request.url()}")
                chain.proceed(request)
            }
            .build()
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
        retrofit.create(ApiService::class.java)
    }
}