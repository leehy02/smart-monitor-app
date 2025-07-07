package com.example.smartmonitor.network
import com.example.smartmonitor.data.ReportItem
import com.example.smartmonitor.data.SaveItem
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("/get_latest_report/")
    suspend fun getLatestReport(): List<ReportItem>

    @POST("/save_report")
    suspend fun saveReport(): SaveItem
}