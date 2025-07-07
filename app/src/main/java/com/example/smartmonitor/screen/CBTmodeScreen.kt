package com.example.smartmonitor.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import com.example.smartmonitor.R
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.smartmonitor.network.NetworkManager
import com.example.smartmonitor.screen.modal.ErrorModal
import com.example.smartmonitor.screen.ui.theme.SmartMonitorTheme
import com.example.smartmonitor.screen.user.CommonTopBar
import com.example.smartmonitor.screen.user.HamMenu
import com.example.smartmonitor.screen.modal.ResetModal
import com.example.smartmonitor.screen.ui.theme.GmarketSans
import kotlinx.coroutines.launch

@Composable
fun CBTmodeScreen(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val scrollState = rememberScrollState()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HamMenu(navController, drawerState) // ✅ 새로운 서브메뉴 화면
        }
    ){
        Scaffold(
            topBar = {
                CommonTopBar(
                    title = "CBT 모드",
                    navController = navController,
                    onMenuClick = { scope.launch { drawerState.open()} }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .background(Color.White)
                    .padding(paddingValues) // 상단바 아래부터 콘텐츠 배치
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(Modifier.height(20.dp))

                cbtinfoButton(
                    title = "CBT 시작하기",
                    guide = "◦ CBT 상담을 시작합니다.",
                    image = R.drawable.consult,
                    onClick = { navController.navigate("guide_screen") }
                )

                Spacer(Modifier.height(30.dp))

                cbtinfoButton(
                    title = "CBT 리포트",
                    guide = "◦ CBT 상담 결과 리포트 조회가 가능합니다.",
                    image = R.drawable.cbtreport,
                    onClick = { navController.navigate("guide_screen") }
                )

                Spacer(Modifier.height(30.dp))

                cbtinfoButton(
                    title = "감정 분석",
                    guide = "◦ 상담 전과 후의 감정 분석 결과를 그래프를 통해 제공합니다.",
                    image = R.drawable.emotion,
                    onClick = { navController.navigate("guide_screen") }
                )

                Spacer(Modifier.height(30.dp))

                cbtinfoButton(
                    title = "인지 왜곡 분석",
                    guide = "◦ 상담 전과 후의 인지 왜곡 패턴 분석 결과를 그래프를 통해 제공 합니다.",
                    image = R.drawable.cognitive,
                    onClick = { navController.navigate("aireport_screen") }
                )

                Spacer(Modifier.height(30.dp))

                cbtinfoButton(
                    title = "주간 실천 계획",
                    guide = "◦ 날짜별 목표 수행 여부를 체크하며 활동량 변화를 그래프를 통해 제공합니다.",
                    image = R.drawable.plan,
                    onClick = { navController.navigate("aireport_screen") }
                )
            }
        }
    }
}

@Composable
fun cbtinfoButton(
    title: String,
    guide: String,
    image: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .width(310.dp)
            .height(120.dp),
        shape = RoundedCornerShape(10.dp),
        colors = ButtonDefaults.buttonColors(Color(0xFF4469FF))
    ) {
        Row (
            modifier = Modifier
                .fillMaxSize(),
            horizontalArrangement = Arrangement.Center, // ✅ 가로 방향 정렬
            verticalAlignment = Alignment.CenterVertically // ✅ 세로 방향 정렬
        ){
            Image(
                painter = painterResource(id = image),
                contentDescription = "image icon",
                modifier = Modifier.size(80.dp)
            )
            Spacer(Modifier.width(20.dp))
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = guide,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Start,
                    letterSpacing = (-0.3).sp,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun CBTmodePreview() {
    SmartMonitorTheme {
        CBTmodeScreen(navController = rememberNavController())
    }
}