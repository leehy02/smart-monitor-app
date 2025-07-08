package com.example.smartmonitor.data

class ReportItem (
    val title: String,
    val content: String
)

class SaveItem (
    val status: String,
    val report_id: Int
)

class pitchItem (
    val status: Boolean,
    val pitch_angle: Int
)

class distanceItem (
    val status: Boolean,
    val distance_cm: Int
)