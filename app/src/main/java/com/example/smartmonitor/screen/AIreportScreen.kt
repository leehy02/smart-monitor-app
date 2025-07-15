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
import kotlinx.coroutines.launch
import java.nio.file.Files.size

@Composable
fun AIreportScreen(navController: NavController) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val angle = remember { mutableStateOf(0) }
    val distance = remember {mutableStateOf(0) }

    val angle_f = remember { mutableStateOf(0.1f) }
    val distance_f = remember {mutableStateOf(0.1f) }

    val angle_color = remember { mutableStateOf(Color(0xFFB2B2B2)) }
    val distance_color = remember {mutableStateOf(Color(0xFFB2B2B2)) }

    val isLoading = remember { mutableStateOf(true) }
    val showErrorModal = remember { mutableStateOf(false) }

    val reportItems = remember { mutableStateOf<List<ReportItem>>(emptyList()) }
    val distance10List = remember { mutableStateOf<List<Int>>(emptyList()) }
    val pitch10List = remember { mutableStateOf<List<Int>>(emptyList()) }

    LaunchedEffect(Unit) {
        try {
            // ✅ 최근 pitch, distance 조회
            val pitchResponse = RetrofitClient.apiService.getPitch()
            val distanceResponse = RetrofitClient.apiService.getDistance()

            val pitchValue = if (pitchResponse.pitch_angle >= 90) 90 else pitchResponse.pitch_angle
            angle_color.value = if (pitchResponse.pitch_angle < 50) Color(0xFFF64E4E) else Color(0xFF48861A)

            val distanceValue = if (distanceResponse.distance_cm >= 90) 90 else distanceResponse.distance_cm
            distance_color.value = if (distanceResponse.distance_cm < 30 || distanceResponse.distance_cm > 50) Color(0xFFF64E4E) else Color(0xFF48861A)

            angle.value = pitchValue
            distance.value = distanceValue
            angle_f.value = pitchValue.toFloat() / 90f
            distance_f.value = distanceValue.toFloat() / 90f

            // ✅ 10초간 pitch, distance 조회
            val pitchlistResponse = RetrofitClient.apiService.get10Pitch()
            val distancelistResponse = RetrofitClient.apiService.get10Distance()

            pitch10List.value = pitchlistResponse.pitch_10angle
            distance10List.value = distancelistResponse.distance_10cm

            // ✅ GPT 리포트 자동 생성
            val saveResult = RetrofitClient.apiService.saveReport()
            if (saveResult.status == "success") {
                val latestReport = RetrofitClient.apiService.getLatestReport()
                reportItems.value = latestReport
            } else {
                showErrorModal.value = true
            }

        } catch (e: Exception) {
            Log.e("DEBUG", "❌ 자동 리포트 실패: ${e.message}")
            showErrorModal.value = true
        } finally {
            isLoading.value = false
        }
    }

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
                LineStatus(pitch10List.value, distance10List.value)
                StatusBox(angle.value, distance.value, angle_f.value, distance_f.value, angle_color.value, distance_color.value)

                reportItems.value.forEach {
                    reportList(title = it.title, content = it.content)
                }
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

    if (showErrorModal.value) {
        ErrorModal {
            showErrorModal.value = false
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
            text = "[10초 간 목각도, 거리 데이터 조회]",
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
            ProgressBar(value = "[현재 사용자-모니터 간 거리 : ${distance}cm] *범위 0~90cm",label = "\uD83D\uDCCD거리", progress = distance_f , color = distance_c)
            Spacer(modifier = Modifier.height(20.dp))
            ProgressBar(value = "[현재 목각도 : ${angle}도] *범위 0~90도", label = "\uD83D\uDCCD목각도", progress = angle_f, color = angle_c) // 연한 빨강
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