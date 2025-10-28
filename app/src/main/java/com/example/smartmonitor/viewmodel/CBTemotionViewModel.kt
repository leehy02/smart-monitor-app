package com.example.smartmonitor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartmonitor.data.CBTemotionItem
import com.example.smartmonitor.data.CBTreportItem
import com.example.smartmonitor.data.CBTreportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class EmotionUiState {
    object Idle : EmotionUiState()
    object Loading : EmotionUiState()
    data class Success(val emotions: List<CBTemotionItem>) : EmotionUiState()
    data class Error(val message: String) : EmotionUiState()
}

class CBTemotionViewModel : ViewModel() {

    private val _uistate = MutableStateFlow<EmotionUiState>(EmotionUiState.Idle)
    val uiState: StateFlow<EmotionUiState> = _uistate

    fun loadEmotionReport(){
        viewModelScope.launch {
            _uistate.value = EmotionUiState.Loading
            try{
                val data = CBTreportRepository.getEmotionReport()
                _uistate.value = EmotionUiState.Success(data)
            } catch (e: Exception){
                _uistate.value = EmotionUiState.Error(
                    e.message ?: "네트워크에 오류가 발생했습니다."
                )
            }
        }
    }

    fun refresh() = loadEmotionReport()

    fun toEmotionItems(reportList: List<CBTemotionItem>): List<Triple<String, Int, String>>{

        val emotionList = mutableListOf<Triple<String, Int, String>>()

        for (item in reportList){
            emotionList.add(Triple(item.emotionName, item.emotionScore, item.division))
        }

        return emotionList
    }
}