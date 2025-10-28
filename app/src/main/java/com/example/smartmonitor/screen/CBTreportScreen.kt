package com.example.smartmonitor.screen

import android.content.ContentValues.TAG
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import android.util.Log
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.smartmonitor.R
import com.example.smartmonitor.screen.ui.theme.SmartMonitorTheme
import com.example.smartmonitor.screen.user.CommonTopBar
import com.example.smartmonitor.screen.user.HamMenu
import com.example.smartmonitor.viewmodel.CBTreportViewModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import com.example.smartmonitor.viewmodel.ReportUiState

@Composable
fun CBTreportScreen(
    navController: NavController,
    vm: CBTreportViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        Log.d(TAG, "Composable 진입 → vm.loadSummaryReport() 호출")
        vm.loadSummaryReport()
    }

    val uiState by vm.uiState.collectAsState()

    // 상태 변화 추적
    LaunchedEffect(uiState) {
        when (uiState) {
            is ReportUiState.Idle -> Log.d(TAG, "uiState=Idle")
            is ReportUiState.Loading -> Log.d(TAG, "uiState=Loading")
            is ReportUiState.Error ->
                Log.e(TAG, "uiState=Error: ${(uiState as ReportUiState.Error).message}")
            is ReportUiState.Success -> {
                val r = (uiState as ReportUiState.Success).report
                Log.d(TAG, "uiState=Success: $r")
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { HamMenu(navController, drawerState) }
    ) {
        Scaffold(
            topBar = {
                CommonTopBar(
                    title = "CBT 요약 리포트",
                    navController = navController,
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            }
        ) { paddingValues ->

            when (val s = uiState) {
                is ReportUiState.Idle,
                is ReportUiState.Loading -> {
                    // 로딩 UI
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator()
                            Spacer(Modifier.height(12.dp))
                            Text("리포트를 불러오는 중...")
                        }
                    }
                }

                is ReportUiState.Error -> {
                    // 에러 UI
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("에러: ${s.message}")
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = { vm.refresh() }) {
                                Text("다시 시도")
                            }
                        }
                    }
                }

                is ReportUiState.Success -> {
                    val guideItems = vm.toGuideItems(s.report)
                    Log.d(TAG, "렌더링 직전 guideItems.size=${guideItems.size} " +
                            "firstTitle='${guideItems.firstOrNull()?.first ?: "-"}'")

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(paddingValues)
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        guideItems.forEach { (title, content) ->
                            val safe = if (content.isBlank()) "— 내용 없음 —" else content
                            CBTreportList(title, safe)
                        }
                    }
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
