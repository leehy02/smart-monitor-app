package com.example.smartmonitor.network

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private  const val BASE_URL = "http://18.204.77.217:5000"

    val gson = GsonBuilder()
        .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
        .create()

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS) // 연결 시도 최대 30초
        .readTimeout(60, TimeUnit.SECONDS)    // 응답 기다리는 시간 최대 60초
        .writeTimeout(30, TimeUnit.SECONDS)   // 요청 보내는 시간 최대 60초
        .build()

    val apiService: ApiService by lazy{
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}