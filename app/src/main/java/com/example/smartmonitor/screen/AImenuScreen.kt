package com.example.smartmonitor.screen

import android.util.Log
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
import com.example.smartmonitor.network.RetrofitClient
import com.example.smartmonitor.screen.modal.ErrorModal
import com.example.smartmonitor.screen.ui.theme.SmartMonitorTheme
import com.example.smartmonitor.screen.user.CommonTopBar
import com.example.smartmonitor.screen.user.HamMenu
import com.example.smartmonitor.screen.modal.ResetModal
import com.example.smartmonitor.screen.ui.theme.GmarketSans
import kotlinx.coroutines.launch

@Composable
fun AImenuScreen(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val isLoading = remember { mutableStateOf(false) } // 로딩 중 상태
    val showResetModal = remember { mutableStateOf(false) }
    val showErrorModal = remember { mutableStateOf(false) }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HamMenu(navController, drawerState) // ✅ 새로운 서브메뉴 화면
        }
    ){
        Scaffold(
            topBar = {
                CommonTopBar(
                    title = "AI 자세 케어",
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
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Spacer(Modifier.height(20.dp))

//                aiinfoButton(
//                    title = "초기화",
//                    guide = "◦ 모니터 암을 초기상태로 되돌립니다.",
//                    image = R.drawable.reset,
//                    onClick = {
//                        isLoading.value = true
//
//                        NetworkManager.sendResetCommand { success ->
//                            isLoading.value = false
//                            if (success) {
//                                showResetModal.value = true
//                            } else {
//                                showErrorModal.value = true
//                            }
//                        }
//                    }
//                )

                Spacer(Modifier.height(30.dp))

                aiinfoButton(
                    title = "교정 가이드",
                    guide = "◦ 올바른 자세를 유지할 수 있도록 적절한 방법을 추천해 줍니다.",
                    image = R.drawable.recommend,
                    onClick = { navController.navigate("guide_screen") }
                )

                Spacer(Modifier.height(30.dp))

                //UI 디자인용
                aiinfoButton(
                    title = "자세 교정 리포트",
                    guide = "◦ 현재 자세의 목각도와 모니터 간 거리를 분석하여 올바른 자세 교정을 위한 피드백을 제공합니다.",
                    image = R.drawable.health_report,
                    onClick = { navController.navigate("aireport_screen") }
                )
            }
        }
    }
    if (isLoading.value) {
        AlertDialog(
            onDismissRequest = {},  // 뒤로가기나 외부 터치로 닫히지 않게
            confirmButton = {},     // 확인 버튼 없음
            title = {
                Text(
                    text = "잠시만 기다려 주세요...",
                    color = Color(0xFF6A6A6A),
                    fontFamily = GmarketSans,
                    fontSize = 20.sp,
                    modifier = Modifier.fillMaxWidth(),
                )
            },
            text = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),              // 높이 확보해서 중앙 정렬 공간 만들기
                    contentAlignment = Alignment.Center  // ✅ 중앙 정렬
                ) {
                    CircularProgressIndicator()
                }
            },
            containerColor = Color.White,  // ✅ 다이얼로그 배경색: 흰색
        )

    }

    if (showResetModal.value) {
        ResetModal {
            showResetModal.value = false
        }
    }

    if (showErrorModal.value) {
        ErrorModal {
            showErrorModal.value = false
        }
    }
}

@Composable
fun aiinfoButton(
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
                contentDescription = "spine",
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
fun AImenuPreview() {
    SmartMonitorTheme {
        AImenuScreen(navController = rememberNavController())
    }
}