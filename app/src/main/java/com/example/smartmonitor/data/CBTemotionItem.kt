package com.example.smartmonitor.data

import com.google.gson.annotations.SerializedName

data class CBTemotionItem(
    @SerializedName("session_id") val sessionId: Int,
    @SerializedName("emotion_name") val emotionName: String,
    @SerializedName("emotion_score") val emotionScore: Int,
    @SerializedName("division") val division: String
)
