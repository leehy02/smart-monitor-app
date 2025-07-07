package com.example.smartmonitor.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartmonitor.data.ReportItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReportViewModel : ViewModel() {
    private val _reportItems = MutableStateFlow<List<ReportItem>>(emptyList())
    val reportItems: StateFlow<List<ReportItem>> = _reportItems

    private val _error = MutableStateFlow(false)
    val error: StateFlow<Boolean> = _error

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun saveReportandFetch(){
        viewModelScope.launch {
            _isLoading.value = true
            try {

            } catch (e: Exception){
                _error.value = true
            }finally {
                _isLoading.value = false
            }
        }
    }
}