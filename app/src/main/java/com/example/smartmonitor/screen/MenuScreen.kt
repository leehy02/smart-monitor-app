package com.example.smartmonitor.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import com.example.smartmonitor.R
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.smartmonitor.screen.ui.theme.SmartMonitorTheme
import com.example.smartmonitor.screen.user.CommonTopBar
import com.example.smartmonitor.screen.user.HamMenu
import kotlinx.coroutines.launch

@Composable
fun MenuScreen(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HamMenu(navController, drawerState) // ✅ 새로운 서브메뉴 화면
        }
    ){
        Scaffold(
            topBar = {
                CommonTopBar(
                    title = "스마트 모니터 기능",
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
                Spacer(Modifier.height(30.dp))

                infoButton(
                    title = "AI 자세 케어",
                    guide = "◦ 실시간 모니터링을 통해 사용자의 자세를 분석하고, 맞춤형 교정 피드백을 제공 합니다.",
                    image = R.drawable.spine,
                    onClick = { navController.navigate("aimenu_screen") }
                )

                Spacer(Modifier.height(50.dp))

//                infoButton(
//                    title = "CBT 모드",
//                    guide = "◦ 챗봇을 활용한 인지 치료 상담을 제공하며, 진단 보고서를 조회할 수 있습니다.",
//                    image = R.drawable.therapy,
//                    onClick = { navController.navigate("cbtmode_screen") }
//                )
            }
        }
    }
}

@Composable
fun infoButton(
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
            .height(150.dp),
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
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }

}

@Preview(showBackground = true)
@Composable
fun MenuPreview() {
    SmartMonitorTheme {
        MenuScreen(navController = rememberNavController())
    }
}