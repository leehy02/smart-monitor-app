package com.example.smartmonitor.network
import com.example.smartmonitor.data.CBTdistortionItem
import com.example.smartmonitor.data.CBTemotionItem
import com.example.smartmonitor.data.CBTreportItem
import com.example.smartmonitor.data.CBTthoughtItem
import com.example.smartmonitor.data.ReportItem
import com.example.smartmonitor.data.SaveItem
import com.example.smartmonitor.data.distanceItem
import com.example.smartmonitor.data.DistanceList
import com.example.smartmonitor.data.pitchItem
import com.example.smartmonitor.data.PitchList
import com.example.smartmonitor.data.UserItem
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @GET("/get_latest_report/")
    suspend fun getLatestReport(): List<ReportItem>

    @POST("/save_report")
    suspend fun saveReport(): SaveItem

    @GET("/get_distance_avg")
    suspend fun getDistance(): distanceItem

    @GET("/get_pitch_avg")
    suspend fun getPitch(): pitchItem

    @GET("/get_distance_10")
    suspend fun get10Distance(): DistanceList

    @GET("/get_pitch_10")
    suspend fun get10Pitch(): PitchList

    @GET("/user_info")
    suspend fun getUserInfo(): UserItem

    @GET("/summary_report")
    suspend fun getSummaryReport(): CBTreportItem

    @GET("/emotion_report")
    suspend fun getEmotionReport(): List<CBTemotionItem>

    @GET("/distortions_report")
    suspend fun getDistortionReport(): List<CBTdistortionItem>

    @GET("/thoughts_report")
    suspend fun getThoughtReport(): CBTthoughtItem
}