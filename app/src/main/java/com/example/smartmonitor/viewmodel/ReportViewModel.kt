package com.example.smartmonitor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartmonitor.data.*
import com.example.smartmonitor.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportViewModel : ViewModel() {
    private val _reportItems = MutableStateFlow<List<ReportItem>>(emptyList())
    val reportItems: StateFlow<List<ReportItem>> = _reportItems

    private val _error = MutableStateFlow(false)
    val error: StateFlow<Boolean> = _error

    fun dismissError() {
        _error.value = false
    }

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _pitch = MutableStateFlow(0)
    val pitch: StateFlow<Int> = _pitch

    private val _distance = MutableStateFlow(0)
    val distance: StateFlow<Int> = _distance

    private val _pitchList = MutableStateFlow<List<Int>>(emptyList())
    val pitchList: StateFlow<List<Int>> = _pitchList

    private val _distanceList = MutableStateFlow<List<Int>>(emptyList())
    val distanceList: StateFlow<List<Int>> = _distanceList

    fun saveReportAndFetch() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // 평균값
                val pitchResponse = RetrofitClient.apiService.getPitch()
                val distanceResponse = RetrofitClient.apiService.getDistance()
                _pitch.value = pitchResponse.avg_pitch_angle.coerceAtMost(90)
                _distance.value = distanceResponse.avg_distance_cm.coerceAtMost(90)

                // 최근 10개 데이터
                _pitchList.value = RetrofitClient.apiService.get10Pitch().pitch_10angle
                _distanceList.value = RetrofitClient.apiService.get10Distance().distance_10cm

                // GPT 리포트 생성 및 조회
                val saveResult = RetrofitClient.apiService.saveReport()
                if (saveResult.status == "success") {
                    _reportItems.value = RetrofitClient.apiService.getLatestReport()
                } else {
                    _error.value = true
                }

            } catch (e: Exception) {
                _error.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }
}
