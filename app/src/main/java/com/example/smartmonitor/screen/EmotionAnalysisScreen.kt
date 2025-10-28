package com.example.smartmonitor.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.smartmonitor.data.UserItem
import com.example.smartmonitor.screen.ui.theme.SmartMonitorTheme
import com.example.smartmonitor.screen.user.CommonTopBar
import com.example.smartmonitor.screen.user.HamMenu
import com.example.smartmonitor.viewmodel.CBTemotionViewModel
import com.example.smartmonitor.viewmodel.CBTreportViewModel
import com.example.smartmonitor.viewmodel.EmotionUiState
import com.example.smartmonitor.viewmodel.ReportUiState
import com.example.smartmonitor.viewmodel.UserUiState
import com.example.smartmonitor.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin


private const val TAG = "EmotionLog"

@Composable
fun EmotionAnalysisScreen(
    navController: NavController,
    vm: CBTemotionViewModel = viewModel(),
    vm2: CBTreportViewModel = viewModel(),
    vm3: UserViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())

    LaunchedEffect(Unit) {
        Log.d(TAG, "Composable 진입 → vm.loadEmotionReport() 호출")
        vm.loadEmotionReport()
        vm2.loadSummaryReport()
        vm3.loadUserInfo()
    }

    val uiState by vm.uiState.collectAsState()
    val reportState by vm2.uiState.collectAsState()
    val userState by vm3.uiState.collectAsState()

    LaunchedEffect(uiState){
        when(uiState){
            is EmotionUiState.Idle -> Log.d(TAG, "uiState=Idle")
            is EmotionUiState.Error ->  Log.d(TAG, "uiState=Error: ${(uiState as EmotionUiState.Error).message}")
            is EmotionUiState.Success -> Log.d(TAG, "uiState=Success: ${(uiState as EmotionUiState.Success).emotions}")
            is EmotionUiState.Loading -> Log.d(TAG, "uiState=Loading")
        }
    }

    LaunchedEffect(reportState){
        when(reportState){
            is ReportUiState.Idle -> Log.d(TAG, "reportState=Idle")
            is ReportUiState.Error ->  Log.d(TAG, "reportState=Error: ${(reportState as ReportUiState.Error).message}")
            is ReportUiState.Success -> Log.d(TAG, "reportState=Success: ${(reportState as ReportUiState.Success).report}")
            is ReportUiState.Loading -> Log.d(TAG, "reportState=Loading")
        }
    }


    LaunchedEffect(userState){
        when(userState){
            is UserUiState.Idle -> Log.d(TAG, "userState=Idle")
            is UserUiState.Error ->  Log.d(TAG, "userState=Error: ${(userState as UserUiState.Error).message}")
            is UserUiState.Success -> Log.d(TAG, "userState=Success: ${(userState as UserUiState.Success).user}")
            is UserUiState.Loading -> Log.d(TAG, "userState=Loading")
        }
    }

    //리포트 결과 가져오기
    val emotionReport = remember(reportState) {
        when (reportState) {
            is ReportUiState.Success -> {
                val report = (reportState as ReportUiState.Success).report
                vm2.toGuideItems(report).getOrNull(1)?.second ?: "감정 비교 정보 없음"
            }
            else -> "감정 비교 정보 없음"
        }
    }

    //개인정보 가져오기
    val successUser = (userState as? UserUiState.Success)?.user

    val name: String = successUser?.userName
        ?.takeIf { it.isNotBlank() }
        ?: "이름없음"

    val ageText: String = successUser?.userAge
        ?.toString()
        ?: "-"

    val genderRaw: String? = successUser?.userGender
    val gender: String = when (genderRaw) {
        "M" -> "남성"
        "F" -> "여성"
        "Others" -> "기타"
        null, "" -> "-"
        else -> genderRaw  // 서버가 다른 값을 주면 그대로 표시
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HamMenu(navController, drawerState) // ✅ 새로운 서브메뉴 화면
        }
    ) {
        Scaffold(
            topBar = {
                CommonTopBar(
                    title = "감정 분석 결과",
                    navController = navController,
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            }
        ) { paddingValues ->

            when (val s = uiState){

                is EmotionUiState.Idle,
                is EmotionUiState.Loading ->{
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
                            Text("감정 분석 결과를 불러오는 중...")
                        }
                    }
                }

                is EmotionUiState.Success -> {

                    val emotionItems = vm.toEmotionItems(s.emotions)
                    val beforeItems = emotionItems.filter { it.third == "전" }
                    val afterItems  = emotionItems.filter { it.third == "후" }

                    Log.d(TAG, "전 감정 리스트 = $beforeItems")
                    Log.d(TAG, "후 감정 리스트 = $afterItems")
                    Log.d(TAG, "렌더링 직전 emotionItems.size=${emotionItems.size} " +
                            "firstTitle='${emotionItems.firstOrNull()?.first ?: "-"}'")

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

                        Text(
                            text = "\uD83D\uDCC5 $currentDate",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF131313),
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(Modifier.height(8.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White)
                                .border(
                                    shape = RoundedCornerShape(5.dp),
                                    width = 2.dp,
                                    color = Color(0xFFA2A2A2)
                                )
                        ) {
                            Text(
                                text = " 사용자 프로필 : $name ${ageText}살 $gender",
                                fontWeight = FontWeight.Medium,
                                fontSize = 20.sp,
                                color = Color(0xFFA2A2A2),
                                modifier = Modifier
                                    .align(Alignment.CenterStart)
                                    .padding(5.dp)
                            )
                        }

                        Spacer(Modifier.height(8.dp))

                        EmotionBox(
                            "◾상담 시작 전",
                            emotionData = beforeItems.map { it.first to it.second.toFloat() }
                        )

                        Spacer(Modifier.height(15.dp))

                        EmotionBox(
                            "◾상담 시작 후",
                            emotionData = afterItems.map { it.first to it.second.toFloat() }
                        )

                        Spacer(Modifier.height(10.dp))

                        Text(
                            text = "◾감정 분석 결과",
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
                            color = Color(0xFF000000),
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(Modifier.height(7.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color(0xFFF3F3F3), shape = RoundedCornerShape(10.dp))
                                .padding(15.dp)
                        ) {
                            Text(
                                text = emotionReport,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color(0xFF7C7C7C)
                            )
                        }
                    }
                }

                is EmotionUiState.Error -> {
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
            }
        }
    }
}

@Composable
fun EmotionBox(
    title: String,
    emotionData: List<Pair<String, Float>>
) {
    val sortedEmotionData = emotionData.sortedByDescending { it.second }
    val defaultColors = listOf(
        Color(0xFF3A62FF),
        Color(0xFF5891FF),
        Color(0xFF80ABFF),
        Color(0xFFA7C1FF)
    )

    val colorMap = sortedEmotionData.mapIndexed { index, (label, _) ->
        label to defaultColors.getOrElse(index) { Color.LightGray }
    }.toMap()

    Box(
        modifier = Modifier
            .background(color = Color(0xFFEAEAEA), shape = RoundedCornerShape(5.dp))
            .border(shape = RoundedCornerShape(5.dp), color = Color(0xFFEAEAEA), width = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min)
                .padding(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxHeight()
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(Modifier.height(3.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight()
                        .padding(5.dp)
                        .background(Color.White, shape = RoundedCornerShape(5.dp))
                        .border(shape = RoundedCornerShape(5.dp), color = Color.White, width = 1.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(5.dp)
                    ) {
                        emotionData.forEach { (label, value) ->
                            EmotionTextDesign("$label ${value.toInt()}")
                        }
                    }
                }
            }

            EmotionPieChart(
                values = emotionData.map { it.second },
                labels = emotionData.map { it.first },
                colors = emotionData.map { (label, _) -> colorMap[label] ?: Color.LightGray },
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}


@Composable
fun EmotionPieChart(
    modifier: Modifier = Modifier,
    values: List<Float>,
    labels: List<String>,
    colors: List<Color>
) {
    val total = values.sum()
    val sweepAngles = values.map { 360f * (it / total) }

    Canvas(modifier = modifier.size(250.dp)) {
        var startAngle = -90f

        for (i in values.indices) {
            // 1. 파이 조각 그리기
            drawArc(
                color = colors[i],
                startAngle = startAngle,
                sweepAngle = sweepAngles[i],
                useCenter = true
            )

            // 2. 텍스트 위치 계산
            val angleInRad = Math.toRadians((startAngle + sweepAngles[i] / 2).toDouble())
            val radius = size.minDimension / 3
            val centerX = center.x + cos(angleInRad).toFloat() * radius
            val centerY = center.y + sin(angleInRad).toFloat() * radius

            // 3. 텍스트 그리기
            drawContext.canvas.nativeCanvas.drawText(
                labels[i],
                centerX,
                centerY,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.WHITE
                    textSize = 40f
                    textAlign = android.graphics.Paint.Align.CENTER
                    isFakeBoldText = true
                }
            )

            startAngle += sweepAngles[i]
        }
    }
}


@Composable
fun EmotionTextDesign(
    content : String
){
    Text(
        text = content,
        fontWeight = FontWeight.Medium,
        fontSize = 25.sp,
        color = Color(0xFF8D8D8D),
    )

    Spacer(Modifier.height(15.dp))
}

@Preview
@Composable
fun EmotionAnalysisPreview() {
    SmartMonitorTheme {
        EmotionAnalysisScreen(navController = rememberNavController())
    }
}
