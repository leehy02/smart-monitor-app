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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.smartmonitor.data.ReportItem
import com.example.smartmonitor.data.SaveItem
import com.example.smartmonitor.network.RetrofitClient
import com.example.smartmonitor.screen.ui.theme.SmartMonitorTheme
import com.example.smartmonitor.screen.user.CommonTopBar
import com.example.smartmonitor.screen.user.HamMenu
import kotlinx.coroutines.launch

@Composable
fun AIreportScreen(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val reportItems = remember { mutableStateOf(
        navController.previousBackStackEntry
            ?.savedStateHandle
            ?.get<List<ReportItem>>("reportItems") ?: emptyList()
    ) }
//    val saveItems = remember { mutableStateOf<List<SaveItem>>(emptyList()) }

//    LaunchedEffect(Unit) {
//        try {
//            val response = RetrofitClient.apiService.saveReport()
//            saveItems.value = response
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }

//    val guideItems = listOf(
//        "머리 위치 이상 감지" to "머리가 화면과 너무 가깝거나 기울어져 있음",
//        "등 굽음 감지" to "허리가 구부정하여 장시간 유지 시 피로 유발 가능",
//        "모니터 거리 조정 필요" to "화면과의 거리가 너무 가까움 (50cm 미만)",
//        "자세 불균형" to "체중이 한쪽으로 쏠려 있음",
//        "장시간 동일 자세 유지" to "1시간 이상 움직임이 없어 스트레칭 필요"
//    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HamMenu(navController, drawerState) // ✅ 새로운 서브메뉴 화면
        }
    ){
        Scaffold(
            topBar = {
                CommonTopBar(
                    title = "자세 분석 및 교정 안내",
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
                    painterResource(id = R.drawable.example),
                    contentDescription = "guide",
                    modifier = Modifier.size(width = 320.dp, height = 120.dp)
                )

//                guideItems.forEachIndexed { index, (title, content) ->
//                    reportList(title, content)
//                }

                reportItems.value.forEach {
                    reportList(title = it.title, content = it.content)
                }
            }
        }
    }
}

@Composable
fun reportList(
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
fun AIreportPreview() {
    SmartMonitorTheme {
        AIreportScreen(navController = rememberNavController())
    }
}