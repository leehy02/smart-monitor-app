package com.example.smartmonitor.screen

import android.R.attr.text
import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.smartmonitor.data.DistanceList
import com.example.smartmonitor.data.PitchList
import com.example.smartmonitor.data.ReportItem
import com.example.smartmonitor.data.SaveItem
import com.example.smartmonitor.network.RetrofitClient
import com.example.smartmonitor.screen.modal.ErrorModal
import com.example.smartmonitor.screen.modal.ResetModal
import com.example.smartmonitor.screen.ui.theme.GmarketSans
import com.example.smartmonitor.screen.ui.theme.SmartMonitorTheme
import com.example.smartmonitor.screen.user.CommonTopBar
import com.example.smartmonitor.screen.user.HamMenu
import com.example.smartmonitor.viewmodel.ReportViewModel
import kotlinx.coroutines.launch
import java.nio.file.Files.size

@Composable
fun AIreportScreen(
    navController: NavController,
    viewModel: ReportViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val isLoading = viewModel.isLoading.collectAsState()
    val showErrorModal = viewModel.error.collectAsState()
    val reportItems = viewModel.reportItems.collectAsState()
    val pitch = viewModel.pitch.collectAsState()
    val distance = viewModel.distance.collectAsState()
    val pitch10List = viewModel.pitchList.collectAsState()
    val distance10List = viewModel.distanceList.collectAsState()

    val pitchColor = if (pitch.value < 50) Color(0xFFF64E4E) else Color(0xFF48861A)
    val distanceColor = if (distance.value < 30 || distance.value > 50) Color(0xFFF64E4E) else Color(0xFF48861A)

    LaunchedEffect(Unit) {
        viewModel.saveReportAndFetch()
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            HamMenu(navController, drawerState)
        }
    ) {
        Scaffold(
            topBar = {
                CommonTopBar(
                    title = "자세 분석 및 교정 안내",
                    navController = navController,
                    onMenuClick = { scope.launch { drawerState.open() } }
                )
            }
        ) { paddingValues ->
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
                LineStatus(pitch10List.value, distance10List.value)
                StatusBox(
                    angle = pitch.value,
                    distance = distance.value,
                    angle_f = pitch.value / 90f,
                    distance_f = distance.value / 90f,
                    angle_c = pitchColor,
                    distance_c = distanceColor
                )

                reportItems.value.forEach {
                    reportList(title = it.title, content = it.content)
                }
            }
        }
    }

    if (isLoading.value) {
        AlertDialog(
            onDismissRequest = {},
            confirmButton = {},
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
                        .height(80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            },
            containerColor = Color.White
        )
    }

    if (showErrorModal.value) {
        ErrorModal {
            viewModel.dismissError()
        }
    }
}

@Composable
fun LineStatus(
    angleList : List<Int>,
    distanceList : List<Int>
){
    val angleData = angleList.reversed()
    val distanceData = distanceList.reversed()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(12.dp)
            .background(Color.White, RoundedCornerShape(5.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(5.dp))
            .padding(12.dp)
    ) {
        Text(
            text = "[10초 간 목각도, 거리 변화 분석]",
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        MultiLineGraph(angleData,distanceData)
        Spacer(modifier = Modifier.height(10.dp))

        Column{
            Row {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color.Red, shape = CircleShape)
                )

                Text(
                    text = " 사용자-모니터 간 거리 정상 범위 : 30~50cm",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }

            Row {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color.Blue, shape = CircleShape)
                )

                Text(
                    text = " 목각도 정상 범위 : 50도 이상",
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        }
    }
}

@Composable
fun MultiLineGraph(
    angleData: List<Int>,
    distanceData: List<Int>,
    modifier: Modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
) {
    val minY = 10f
    val maxY = 90f

    Canvas(modifier = modifier.padding(horizontal = 12.dp)) {
        val xStep = size.width / (angleData.size - 1)
        val height = size.height

        fun scaleY(value: Int): Float {
            return height - ((value.toFloat() - minY) / (maxY - minY)) * height
        }

        // 🟨 1. 가로 격자 (y축 기준선)
        val yIntervals = 5
        val yStepValue = (maxY - minY) / yIntervals
        val yStepPx = height / yIntervals

        for (i in 0..yIntervals) {
            val y = i * yStepPx
            drawLine(
                color = Color(0xFF818181),
                start = Offset(0f, y),
                end = Offset(size.width, y),
                strokeWidth = 1.5f
            )
        }

        // 🔵 목각도 선
        val anglePoints = angleData.mapIndexed { i, y ->
            Offset(i * xStep, scaleY(y))
        }

        for (i in 0 until anglePoints.size - 1) {
            drawLine(
                color = Color.Blue,
                start = anglePoints[i],
                end = anglePoints[i + 1],
                strokeWidth = 4f
            )
        }

        // 🔵 목각도 점 + 수치
        anglePoints.forEachIndexed { i, point ->
            drawCircle(color = Color.Blue, radius = 10f, center = point) // 테두리
            drawCircle(color = Color.White, radius = 6f, center = point) // 내부

            drawContext.canvas.nativeCanvas.drawText(
                "${angleData[i]}",
                point.x,
                point.y - 12f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.BLUE
                    textSize = 28f
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }
            )
        }

        // 🔴 거리 선
        val distPoints = distanceData.mapIndexed { i, y ->
            Offset(i * xStep, scaleY(y))
        }

        for (i in 0 until distPoints.size - 1) {
            drawLine(
                color = Color.Red,
                start = distPoints[i],
                end = distPoints[i + 1],
                strokeWidth = 4f
            )
        }

        // 🔴 거리 점 + 수치
        distPoints.forEachIndexed { i, point ->
            drawCircle(color = Color.Red, radius = 10f, center = point)  // 테두리
            drawCircle(color = Color.White, radius = 6f, center = point) // 내부

            drawContext.canvas.nativeCanvas.drawText(
                "${distanceData[i]}",
                point.x,
                point.y - 12f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.RED
                    textSize = 28f
                    textAlign = android.graphics.Paint.Align.CENTER
                    isAntiAlias = true
                }
            )
        }
    }
}





@Composable
fun StatusBox(
    angle : Int,
    distance : Int,
    angle_f : Float,
    distance_f : Float,
    angle_c : Color,
    distance_c : Color
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(12.dp)
            .background(Color.White, RoundedCornerShape(5.dp))
            .border(1.dp, Color.Gray, RoundedCornerShape(5.dp))
            .padding(12.dp)
    ) {
        Column (
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(12.dp)
                .background(Color.White, RoundedCornerShape(5.dp))
                .border(1.dp, Color.Gray, RoundedCornerShape(5.dp))
                .padding(12.dp)
        ){
            ProgressBar(value = "[사용자-모니터 거리 평균 : ${distance}cm] *범위 0~90cm",label = "\uD83D\uDCCD거리", progress = distance_f , color = distance_c)
            Spacer(modifier = Modifier.height(20.dp))
            ProgressBar(value = "[목각도 평균 : ${angle}도] *범위 0~90도", label = "\uD83D\uDCCD목각도", progress = angle_f, color = angle_c) // 연한 빨강
        }
    }
}

@Composable
fun ProgressBar(label: String, value: String, progress: Float, color: Color) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(bottom = 4.dp)
        ) {
            Text(
                text = label,
                fontSize = 16.sp,
                modifier = Modifier.width(70.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp)
                    .background(Color(0xFFF1F1F1), RoundedCornerShape(8.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress)
                        .background(color, RoundedCornerShape(8.dp))
                )
            }
        }

        Text( // ✅ 수치를 밑에 깔끔하게 출력
            text = value,
            fontSize = 14.sp,
            modifier = Modifier.padding(start = 70.dp) // ✅ 라벨 너비만큼 들여쓰기
        )
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