package com.example.smartmonitor.network
import com.example.smartmonitor.data.ReportItem
import com.example.smartmonitor.data.SaveItem
import com.example.smartmonitor.data.distanceItem
import com.example.smartmonitor.data.pitchItem
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("/get_latest_report/")
    suspend fun getLatestReport(): List<ReportItem>

    @POST("/save_report")
    suspend fun saveReport(): SaveItem

    @GET("/get_distance")
    suspend fun getDistance(): distanceItem

    @GET("/get_pitch")
    suspend fun getPitch(): pitchItem
}