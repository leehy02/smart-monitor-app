package com.example.smartmonitor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartmonitor.data.CBTreportItem
import com.example.smartmonitor.data.CBTreportRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import android.util.Log

sealed class ReportUiState { // 화면이 무엇을 그려야 하는지를 표현함
    object Idle : ReportUiState()          // 아직 아무 것도 안 한 최초 상태(선택)
    object Loading : ReportUiState()       // 로딩 중
    data class Success(val report: CBTreportItem) : ReportUiState() // 성공(데이터 보유)
    data class Error(val message: String) : ReportUiState()          // 실패(에러 메시지)
}
// 코드 설명 : 상태를 여러가지로 나누고 싶을 때 사용하는 클래스

class CBTreportViewModel : ViewModel() {

    private val _uistate = MutableStateFlow<ReportUiState>(ReportUiState.Idle)
    val uiState: StateFlow<ReportUiState> = _uistate

    fun loadSummaryReport(){
        viewModelScope.launch {
            _uistate.value = ReportUiState.Loading
            try{
                val data = CBTreportRepository.getSummaryReport()
                _uistate.value = ReportUiState.Success(data)
            } catch (e: Exception){
                _uistate.value = ReportUiState.Error(
                    e.message ?: "네트워크에 오류가 발생했습니다."
                )
            }
        }
    }

    fun refresh() = loadSummaryReport()

    fun toGuideItems(report: CBTreportItem): List<Pair<String, String>>{
        return listOf(
            "상담 배경 상황" to (report.background ?: ""),
            "상담 전후 감정 비교" to (report.emotionChange ?: ""),
            "자동적 사고 정리" to (report.automaticThought ?: ""),
            "인지 왜곡 패턴 분석" to (report.cognitiveDistortionSummary ?: ""),
            "대안적 사고 제공" to (report.alternativeThought ?: ""),
            "추천 실천 방법" to (report.planRecommendation ?: ""),
            "개선 목표" to (report.improvementGoal ?: "")
        )
    }
}