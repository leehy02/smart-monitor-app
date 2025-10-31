package com.example.smartmonitor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartmonitor.data.CBTplanItem
import com.example.smartmonitor.data.CBTreportRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class PlansUiState {
    object Idle : PlansUiState()
    object Loading : PlansUiState()
    data class Success(val plans: List<CBTplanItem>) : PlansUiState()
    data class Error(val message: String) : PlansUiState()
}

class CBTweeklyViewModel : ViewModel() {

    private val _uiState = MutableStateFlow<PlansUiState>(PlansUiState.Idle)
    val uiState: StateFlow<PlansUiState> = _uiState

    fun loadPlans() {
        viewModelScope.launch {
            _uiState.value = PlansUiState.Loading
            try {
                val plans = CBTreportRepository.getPlansReport()
                _uiState.value = PlansUiState.Success(plans)
            } catch (e: Exception) {
                _uiState.value = PlansUiState.Error(e.message ?: "계획을 불러오지 못했습니다.")
            }
        }
    }

    fun togglePlan(plan: CBTplanItem, checked: Boolean) {
        val current = _uiState.value
        if (current is PlansUiState.Success) {
            // ✅ 1️⃣ 낙관적 업데이트 (UI 즉시 반영)
            val optimistic = current.plans.map {
                if (it.planId == plan.planId) it.copy(isCompleted = if (checked) 1 else 0) else it
            }
            _uiState.value = PlansUiState.Success(optimistic)
        }

        viewModelScope.launch {
            try {
                // ✅ 2️⃣ 서버에 업데이트 요청
                val res = CBTreportRepository.updatePlanCompletion(plan.planId, checked)

                // ✅ 3️⃣ 서버 응답 기반으로 최종 갱신 (서버에서 수정된 값 반영)
                if (current is PlansUiState.Success) {
                    val updated = current.plans.map {
                        if (it.planId == res.planId) it.copy(isCompleted = res.isCompleted) else it
                    }
                    _uiState.value = PlansUiState.Success(updated)
                }

            } catch (e: Exception) {
                // ✅ 4️⃣ 실패 시 롤백
                if (current is PlansUiState.Success) {
                    _uiState.value = current
                } else {
                    _uiState.value = PlansUiState.Error(e.message ?: "업데이트 실패")
                }
            }
        }
    }
}
