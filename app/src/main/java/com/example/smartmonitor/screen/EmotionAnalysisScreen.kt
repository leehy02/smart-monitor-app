package com.example.smartmonitor.screen

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
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.nativeCanvas
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun EmotionAnalysisScreen(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val currentDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())

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
                        text = " 사용자 프로필 : 김철수 27살 남자",
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
                    emotionData = listOf(
                    "우울감" to 60f,
                    "불안감" to 25f,
                    "기쁨" to 15f )
                )

                Spacer(Modifier.height(15.dp))

                EmotionBox(
                    "◾상담 시작 후",
                    emotionData = listOf(
                        "우울감" to 20f,
                        "불안감" to 35f,
                        "기쁨" to 40f )
                )

                Spacer(Modifier.height(10.dp))

                Text(
                    text = "◾감정 분석 결과",
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    color = Color(0xFF000000),
                    modifier = Modifier
                        .align(Alignment.Start)
                )

                Spacer(Modifier.height(7.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF3F3F3), shape = RoundedCornerShape(10.dp))
                        .padding(15.dp)
                ){
                    Text(
                        text = "추후 DB 연동 예정",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF7C7C7C)
                    )
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
        Color(0xFF4469FF),
        Color(0xFF7CA9FF),
        Color(0xFFA7C7FF)
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
