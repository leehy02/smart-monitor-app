package com.example.smartmonitor.screen

import android.R.attr.text
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
fun GuideScreen(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val guideItems = listOf(
        "모니터 거리 유지" to "모니터 화면과 눈 사이 최소 50cm 이상 유지. 모니터 암을 사용해서 거리를 유지 해보세요!",
        "모니터 높이 조정" to "화면 상단이 눈높이와 일직선이 되도록 조정. 모니터 암을 활용하면 쉽게 높이 조절 가능해요!",
        "허리와 등 곧게 유지" to "허리를 등받이에 붙이고 바른 자세 유지. 인체공학 적 의자나 허리 쿠션을 사용하면 도움이 돼요!",
        "어깨 힘 빼기" to "어깨에 긴장감을 줄이고 자연스럽게 내리기. 팔걸이가 있는 의자를 사용하면 부담이 감소해요!",
        "정기적인 스트레칭" to "1시간마다 일어나서 기지개를 켜고 몸을 움직이기. 앱 알림이 오면 스트레칭을 시작하세요!",
        "다리 올바르게 위치" to "발바닥이 바닥에 닿도록 조정하거나 발 받침대를 활용해 편안한 자세 유지하세요!"
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
                    title = "교정 가이드",
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
                Image(
                    painterResource(id = R.drawable.guide),
                    contentDescription = "guide",
                    modifier = Modifier.size(width = 320.dp, height = 120.dp)
                )

                guideItems.forEachIndexed { index, (title, content) ->
                    guideList(title, content)
                }
            }
        }
    }
}

@Composable
fun guideList(
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

@Preview(showBackground = true)
@Composable
fun GuidePreview() {
    SmartMonitorTheme {
        GuideScreen(navController = rememberNavController())
    }
}