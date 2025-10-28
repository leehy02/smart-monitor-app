package com.example.smartmonitor.data

import com.google.gson.annotations.SerializedName

data class UserItem(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("user_name") val userName: String,
    @SerializedName("user_age") val userAge: Int,
    @SerializedName("user_gender") val userGender: String
)
