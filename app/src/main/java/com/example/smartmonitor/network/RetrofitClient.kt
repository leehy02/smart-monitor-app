package com.example.smartmonitor.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private  const val BASE_URL = "http://18.204.77.217:5000"

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // 연결 시도 최대 30초
        .readTimeout(30, TimeUnit.SECONDS)    // 응답 기다리는 시간 최대 60초
        .writeTimeout(30, TimeUnit.SECONDS)   // 요청 보내는 시간 최대 60초
        .build()

    val apiService: ApiService by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}