package com.example.smartmonitor.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.smartmonitor.data.CBTemotionItem
import com.example.smartmonitor.data.CBTreportRepository
import com.example.smartmonitor.data.UserItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class UserUiState{
    object Idle : UserUiState()
    object Loading : UserUiState()
    data class Success(val user: UserItem) : UserUiState()
    data class Error(val message: String) : UserUiState()
}

class UserViewModel : ViewModel() {

    private val _uistate = MutableStateFlow<UserUiState>(UserUiState.Idle)
    val uiState: StateFlow<UserUiState> = _uistate

    fun loadUserInfo(){
        viewModelScope.launch {
            _uistate.value = UserUiState.Loading
            Log.d("UserDebug", "유저 데이터 로딩중")
            try{
                val data = CBTreportRepository.getUserInfo()
                Log.d("UserDebug", "받은 유저 데이터 = $data")
                _uistate.value = UserUiState.Success(data)
            } catch (e: Exception){
                _uistate.value = UserUiState.Error(
                    e.message ?: "네트워크에 오류가 발생했습니다."
                )
            }
        }
    }

    fun refresh() = loadUserInfo()

    fun toUserItems(userInfo: UserItem): List<Pair<String, String>> {
        return listOf(
            "ID" to userInfo.userId.toString(),
            "name" to userInfo.userName,
            "age" to userInfo.userAge.toString(),
            "gender" to when (userInfo.userGender) {
                "M" -> "남성"
                "F" -> "여성"
                "Others" -> "기타"
                else -> userInfo.userGender
            }
        )
    }
}