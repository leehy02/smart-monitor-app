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
    val avg_pitch_angle: Int
)

class distanceItem (
    val status: Boolean,
    val avg_distance_cm: Int
)

class PitchList (
    val status: Boolean,
    val pitch_10angle: List<Int>
)

class DistanceList (
    val status: Boolean,
    val distance_10cm: List<Int>
)