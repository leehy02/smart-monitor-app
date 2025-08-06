package com.example.smartmonitor.screen.user

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import kotlinx.coroutines.launch

@Composable
fun HamMenu(navController: NavController, drawerState: DrawerState) {
    val scope = rememberCoroutineScope()

    val menuItems = listOf(
        "◦ 시작 화면" to "main_screen",
        "◦ 스마트 모니터 기능" to "menu_screen",
        "◦ AI 자세 케어" to "aimenu_screen",
        "◦ 교정 가이드" to "guide_screen",
        "◦ 자세 분석 및 교정 안내" to "aireport_screen",
        "◦ CBT 모드" to "cbtmode_screen",
        "◦ CBT 리포트" to "cbtreport_screen",
        "◦ 감정 분석" to "emotionanalysis_screen",
        "◦ 인지 왜곡 패턴 분석" to "cognitivedistortion_screen",
        "◦ 주간 실천 계획" to "weeklyplan_screen"
    )

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(250.dp) // ✅ 메뉴 너비 지정
            .background(Color.White)
            .padding(10.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "\uD83D\uDD3D 전체 기능 조회",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF414141),
            modifier = Modifier.padding(bottom = 10.dp, top = 5.dp, start = 5.dp)
        )

        menuItems.forEachIndexed { index, (text, route) ->
            MenuItem(text, onClick = {
                scope.launch { drawerState.close() }
                navController.navigate(route)
            })

            if (index < menuItems.lastIndex) {
                Divider(color = Color.Gray, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))
            }
        }
    }
}

@Composable
fun MenuItem(text: String, onClick: () -> Unit) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.Medium,
        color = Color(0xFF575757),
        modifier = Modifier
            .fillMaxWidth()
            .padding(top=4.dp, start = 12.dp, bottom = 5.dp)
            .clickable { onClick() }
    )
}
