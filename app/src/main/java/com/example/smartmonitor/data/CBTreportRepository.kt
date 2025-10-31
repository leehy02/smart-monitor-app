package com.example.smartmonitor.data

import com.example.smartmonitor.network.RetrofitClient

object CBTreportRepository {
    suspend fun getSummaryReport(): CBTreportItem {
        return RetrofitClient.apiService.getSummaryReport()
    }

    suspend fun getEmotionReport(): List<CBTemotionItem>{
        return RetrofitClient.apiService.getEmotionReport()
    }

    suspend fun getUserInfo(): UserItem{
        return RetrofitClient.apiService.getUserInfo()
    }

    suspend fun getDistortionReport(): List<CBTdistortionItem> {
        return RetrofitClient.apiService.getDistortionReport()
    }

    suspend fun getThoughtReport(): CBTthoughtItem {
        return RetrofitClient.apiService.getThoughtReport()
    }

    suspend fun getPlansReport(): List<CBTplanItem> {
        return RetrofitClient.apiService.getPlansReport()
    }

    suspend fun updatePlanCompletion(planId: Int, isCompleted: Boolean): PlanUpdateResponse =
        RetrofitClient.apiService.updatePlan(
            PlanUpdateRequest(planId = planId, isCompleted = isCompleted)
        )
}
