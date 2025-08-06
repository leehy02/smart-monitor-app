package com.example.smartmonitor.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.smartmonitor.R
import com.example.smartmonitor.screen.ui.theme.SmartMonitorTheme
import com.example.smartmonitor.screen.user.CommonTopBar
import com.example.smartmonitor.screen.user.HamMenu
import kotlinx.coroutines.launch

@Composable
fun CBTreportScreen( navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val guideItems = listOf(
        "상담 배경 상황 (또는 주요 사건 요약)" to "추후 DB 연동",
        "상담 전후 감정 비교" to "추후 DB 연동",
        "자동적 사고 정리" to "추후 DB 연동",
        "인지 왜곡 패턴 분석" to "추후 DB 연동",
        "대안적 사고 제공" to "추후 DB 연동",
        "개선 목표" to "추후 DB 연동",
        "추천 실천 방법" to "추후 DB 연동"
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HamMenu(navController, drawerState) // ✅ 새로운 서브메뉴 화면
        }
    ){
        Scaffold(
            topBar = {
                CommonTopBar(
                    title = "CBT 요약 리포트",
                    navController = navController,
                    onMenuClick = { scope.launch { drawerState.open()} }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(paddingValues) // 상단바 아래부터 콘텐츠 배치
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                guideItems.forEachIndexed { index, (title, content) ->
                    CBTreportList(title, content)
                }
            }
        }
    }
}

@Composable
fun CBTreportList(
    title: String,
    content: String,
    modifier: Modifier = Modifier
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row (
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ){
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(Color(0xFF4469FF), shape = CircleShape)
            )
            Spacer(Modifier.width(5.dp))
            Text(
                text = title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF131313)
            )
        }
        Spacer(Modifier.height(5.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFEEEEEE), shape = RoundedCornerShape(10.dp))
                .padding(8.dp)
        ){
            Text(
                text = content,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF7A7A7A)
            )
        }
    }
}



@Preview
@Composable
fun CBTreportPreview() {
    SmartMonitorTheme {
        CBTreportScreen(navController = rememberNavController())
    }
}
