package com.example.smartmonitor.data

import com.google.gson.annotations.SerializedName

data class CBTdistortionItem(
    @SerializedName("distortion_id") val distortionId: Int,
    @SerializedName("distortion_name") val distortionName: String,
    @SerializedName("count") val count: Int,
)

data class CBTthoughtItem(
    @SerializedName("automatic_thought") val automaticThought: String,
    @SerializedName("automatic_analysis") val automaticAnalysis: String,
    @SerializedName("alternative_thought") val alternativeThought: String,
    @SerializedName("alternative_analysis") val alternativeAnalysis: String
)
