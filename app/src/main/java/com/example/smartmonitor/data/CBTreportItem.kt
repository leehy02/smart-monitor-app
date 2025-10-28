package com.example.smartmonitor.data

import com.google.gson.annotations.SerializedName

data class CBTreportItem( //이름 설정때문에 Serialized 필요(snake -> camel)
    @SerializedName("session_id") val sessionId: Int,
    @SerializedName("background") val background: String?,
    @SerializedName("emotion_change") val emotionChange: String?,
    @SerializedName("automatic_thought") val automaticThought: String?,
    @SerializedName("cognitive_distortion_summary") val cognitiveDistortionSummary: String?,
    @SerializedName("alternative_thought") val alternativeThought: String?,
    @SerializedName("improvement_goal") val improvementGoal: String?,
    @SerializedName("plan_recommendation") val planRecommendation: String?
)
