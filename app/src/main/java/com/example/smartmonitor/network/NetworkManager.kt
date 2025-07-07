package com.example.smartmonitor.network

import android.util.Log
import okhttp3.*              // OkHttp 라이브러리 (HTTP 통신용)
import org.json.JSONObject   // JSON 데이터 구성용
import java.io.IOException
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody

// object 키워드 → 싱글톤 객체 (한 번만 생성되는 고정 객체)
// 서버 통신을 담당하는 네트워크 매니저 클래스
object NetworkManager {

    // OkHttpClient 인스턴스 생성 → 네트워크 요청을 보낼 때 사용됨
    private val client = OkHttpClient()

    // "reset" 명령을 서버에 POST 방식으로 보내는 함수
    fun sendResetCommand(onResult: (Boolean) -> Unit) {
        // JSON 객체 생성 후 "command": "reset" 키-값 넣음SS
        val json = JSONObject()
        json.put("command", "reset")  // 서버에 보낼 데이터

        // JSON 데이터를 요청 바디로 변환
        val requestBody = json.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())

        // POST 요청 생성: 서버 URL과 바디 세팅
        val request = Request.Builder()
            .url("http://18.204.77.217:5000/reset") // ← 너의 Flask 서버 IP 주소로 수정해야 함!
            .post(requestBody)
            .build()

        // 서버로 요청 보내기 (비동기)
        client.newCall(request).enqueue(object : Callback {

            // 요청 실패했을 때 실행 (예: 서버 꺼져 있음, 인터넷 끊김 등)
            override fun onFailure(call: Call, e: IOException) {
                Log.e("NetworkManager", "❌ 서버 전송 실패: ${e.message}")
                onResult(false)
            }

            // 요청 성공했을 때 실행 (서버가 응답함)
            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    // HTTP 200~299 응답이면 성공 처리
                    Log.d("NetworkManager", "✅ 응답 성공: ${response.body?.string()}")
                    onResult(true)
                } else {
                    // 응답은 왔지만 실패 상태 (예: 400, 500 오류 등)
                    Log.e("NetworkManager", "⚠️ 응답 실패: ${response.code}")
                    onResult(false)
                }
            }
        })
    }
}
