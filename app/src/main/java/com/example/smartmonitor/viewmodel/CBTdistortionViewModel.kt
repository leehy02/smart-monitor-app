package com.example.smartmonitor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartmonitor.data.CBTdistortionItem
import com.example.smartmonitor.data.CBTreportRepository
import com.example.smartmonitor.data.CBTthoughtItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class DistortionState {
    object Idle : DistortionState()
    object Loading : DistortionState()
    data class Success(val distortions: List<CBTdistortionItem>, val thoughts: CBTthoughtItem) : DistortionState()
    data class Error(val message: String) : DistortionState()
}

class CBTdistortionViewModel : ViewModel() {

    private val _uistate = MutableStateFlow<DistortionState>(DistortionState.Idle)
    val uiState: StateFlow<DistortionState> = _uistate

    fun loadDistortionReport(){
        viewModelScope.launch {
            _uistate.value = DistortionState.Loading
            try{
                val data1 = CBTreportRepository.getDistortionReport()
                val data2 = CBTreportRepository.getThoughtReport()
                _uistate.value = DistortionState.Success(data1, data2)
            }catch (e: Exception){
                _uistate.value = DistortionState.Error(
                    e.message ?: "네트워크에 오류가 발생했습니다."
                )
            }
        }
    }

    fun refresh() = loadDistortionReport()

    fun toDistortionItems( distortionList: List<CBTdistortionItem>, thought: CBTthoughtItem)
    : Pair<List<Pair<String, Float>>, List<Pair<String, String>>> {

        val dList = mutableListOf<Pair<String, Float>>()

        for (item in distortionList){
            dList.add(Pair(item.distortionName, item.count.toFloat()))
        }

        val tList = listOf(
            "◾자동적 사고 분석" to "${thought.automaticThought} \n\n ${thought.automaticAnalysis}",
            "◾대안적 사고 제공" to "${thought.alternativeThought} \n\n ${thought.alternativeAnalysis}"
        )

        return Pair(dList, tList)
    }
}
