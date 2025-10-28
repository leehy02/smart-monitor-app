package com.example.smartmonitor.screen

import android.content.ContentValues
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.smartmonitor.screen.ui.theme.SmartMonitorTheme
import com.example.smartmonitor.screen.user.CommonTopBar
import com.example.smartmonitor.screen.user.HamMenu
import com.example.smartmonitor.viewmodel.CBTdistortionViewModel
import com.example.smartmonitor.viewmodel.DistortionState
import com.example.smartmonitor.viewmodel.ReportUiState
import com.example.smartmonitor.viewmodel.UserUiState
import com.example.smartmonitor.viewmodel.UserViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CognitiveDistortionScreen(
    navController: NavController,
    vm: CBTdistortionViewModel = viewModel(),
    vm2: UserViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        Log.d(ContentValues.TAG, "Composable 진입 → vm.loadDistortionReport() 호출")
        vm.loadDistortionReport()
        vm2.loadUserInfo()
    }

    val uiState by vm.uiState.collectAsState()
    val userState by vm2.uiState.collectAsState()

    LaunchedEffect(uiState) {
        when (uiState) {
            is DistortionState.Idle -> Log.d(ContentValues.TAG, "uiState=Idle")
            is DistortionState.Loading -> Log.d(ContentValues.TAG, "uiState=Loading")
            is DistortionState.Error ->
                Log.e(ContentValues.TAG, "uiState=Error: ${(uiState as DistortionState.Error).message}")
            is DistortionState.Success -> {
                val d = (uiState as DistortionState.Success).distortions
                Log.d(ContentValues.TAG, "uiState=Success: $d")
            }
        }
    }

//    val cognitiveContentList = listOf(
//        "◾자동적 사고 분석" to "나는 머리도 나쁘고 해도 안되는거같다, 친구는 날 싫어하는것 같아, 내가 귀찮은 존재였나봐라는 자기평가가 나타났습니다.",
//        "◾대안적 사고 제공" to "자기평가에 대해서 시험 점수가 낮더라고 계속 노력하면 개선될 수 있어, 친구에게 시간을 주고 다시 대화를 시도해보는 것도 좋은 방법이야.라는 생각으로 바꾸어 보세요."
//    )

    val currentDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())

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
                    title = "인지 왜곡 패턴 분석 결과",
                    navController = navController,
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            }
        ) { paddingValues ->

            when(val d = uiState){
                is DistortionState.Idle,
                is DistortionState.Loading -> {
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
                            Text("인지 왜곡 패턴 내용을 불러오는 중...")
                        }
                    }
                }

                is DistortionState.Error -> {
                    // 에러 UI
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.White)
                            .padding(paddingValues),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("에러: ${d.message}")
                            Spacer(Modifier.height(12.dp))
                            Button(onClick = { vm.refresh() }) {
                                Text("다시 시도")
                            }
                        }
                    }
                }

                is DistortionState.Success -> {

                    val distortionItem = vm.toDistortionItems(d.distortions, d.thoughts)

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
                                .border(shape = RoundedCornerShape(5.dp), width = 2.dp, color = Color(0xFFA2A2A2))
                        ){
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

                        CognitiveBox("◾금일 상담",
                            cognitiveData = distortionItem.first)

                        Spacer(Modifier.height(20.dp))

                        Text(
                            text = "◾인지 왜곡 패턴 분석 결과",
                            fontWeight = FontWeight.Bold,
                            fontSize = 25.sp,
                            color = Color(0xFF000000),
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(Modifier.height(5.dp))

                        CognitiveContentBox(cognitiveContentData = distortionItem.second)
                    }
                }
            }
        }
    }
}

@Composable
fun CognitiveContentBox(
    cognitiveContentData: List<Pair<String, String>>
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFF3F3F3), shape = RoundedCornerShape(10.dp))
            .padding(15.dp)
    ) {
        Column {
            cognitiveContentData.forEach { (title, content) ->
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(5.dp))
                Box(
                    modifier = Modifier
                        .background(shape = RoundedCornerShape(5.dp),color = Color.White)
                        .fillMaxWidth()
                        .padding(10.dp)
                ){
                    Text(
                        text = content,
                        fontSize = 18.sp,
                        color = Color(0xFF7C7C7C)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
}


@Composable
fun CognitiveBox(
    title: String,
    cognitiveData: List<Pair<String, Float>>
) {
    val sortedEmotionData = cognitiveData.sortedByDescending { it.second }
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
                        cognitiveData.forEach { (label, value) ->
                            CognitiveTextDesign("$label ${value.toInt()}")
                        }
                    }
                }
            }

            CognitivePieChart(
                values = cognitiveData.map { it.second },
                labels = cognitiveData.map { it.first },
                colors = cognitiveData.map { (label, _) -> colorMap[label] ?: Color.LightGray },
                modifier = Modifier.padding(16.dp)
            )

        }
    }
}

@Composable
fun CognitivePieChart(
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
                    textSize = 30f
                    textAlign = android.graphics.Paint.Align.CENTER
                    isFakeBoldText = true
                }
            )

            startAngle += sweepAngles[i]
        }
    }
}

@Composable
fun CognitiveTextDesign(
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
fun CognitiveDistortionPreview() {
    SmartMonitorTheme {
        CognitiveDistortionScreen(navController = rememberNavController())
    }
}
