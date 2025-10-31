package com.example.smartmonitor.data

import com.google.gson.annotations.SerializedName

data class CBTplanItem(
    @SerializedName("session_id") val sessionId: Int,
    @SerializedName("session_datetime") val sessionDatetime: String? = null,
    @SerializedName("plan_id") val planId: Int,
    @SerializedName("plan_text") val planText: String,
    @SerializedName("is_completed") val isCompleted: Int
) {
    val isDone: Boolean get() = (isCompleted == 1)
}

// POST 바디용
data class PlanUpdateRequest(
    @SerializedName("plan_id") val planId: Int,
    @SerializedName("is_completed") val isCompleted: Boolean
)

// POST 응답용(서버 예시에 맞춤)
data class PlanUpdateResponse(
    val status: String,
    @SerializedName("plan_id") val planId: Int,
    @SerializedName("is_completed") val isCompleted: Int,
    val updated: Int
)
