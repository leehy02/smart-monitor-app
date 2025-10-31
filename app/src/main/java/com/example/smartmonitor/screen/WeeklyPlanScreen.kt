package com.example.smartmonitor.screen

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.smartmonitor.screen.ui.theme.SmartMonitorTheme
import com.example.smartmonitor.screen.user.CommonTopBar
import com.example.smartmonitor.screen.user.HamMenu
import kotlinx.coroutines.launch
import androidx.compose.ui.graphics.Path
import com.example.smartmonitor.viewmodel.CBTweeklyViewModel
import com.example.smartmonitor.viewmodel.PlansUiState
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@Composable
fun WeeklyPlanScreen(
    navController: NavController,
    vm: CBTweeklyViewModel = viewModel()
) {
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // 서버 상태 구독 + 최초 로드
    val plansState = vm.uiState.collectAsState().value
    LaunchedEffect(Unit) { vm.loadPlans() }

    val currentDate = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { HamMenu(navController, drawerState) }
    ){
        Scaffold(
            topBar = {
                CommonTopBar(
                    title = "주간 실천 계획",
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
                Text(
                    text = "\uD83D\uDCC5 $currentDate",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF131313),
                    modifier = Modifier.align(Alignment.Start)
                )

                Spacer(modifier = Modifier.padding(8.dp))

                when (plansState) {
                    is PlansUiState.Idle,
                    is PlansUiState.Loading -> {
                        Text("불러오는 중...")
                    }
                    is PlansUiState.Error -> {
                        Text("로드 실패: ${(plansState as PlansUiState.Error).message}", color = Color.Red)
                        Spacer(Modifier.height(8.dp))
                        Button(onClick = { vm.loadPlans() }) { Text("다시 시도") }
                    }
                    is PlansUiState.Success -> {
                        // ✅ 요일 탭/그룹핑 제거, 실천 항목만 표시
                        val plans = (plansState as PlansUiState.Success).plans

                        if (plans.isEmpty()) {
                            Text(
                                text = "제공된 실천 항목이 없습니다.",
                                fontSize = 16.sp,
                                color = Color.Gray,
                                modifier = Modifier
                                    .padding(vertical = 16.dp)
                                    .align(Alignment.Start)
                            )
                        } else {
                            plans.forEach { plan ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Checkbox(
                                        checked = plan.isDone, // isCompleted == 1
                                        onCheckedChange = { checked ->
                                            vm.togglePlan(plan, checked) // 옵티미스틱 + 실패 롤백
                                        }
                                    )
                                    Text(
                                        text = plan.planText,
                                        fontSize = 16.sp,
                                        color = Color(0xFF333333),
                                        modifier = Modifier
                                            .weight(1f)
                                            .padding(start = 4.dp)
                                    )
                                }
                            }
                        }

                        HorizontalDivider(
                            color = Color(0xFFDDDDDD),
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 12.dp)
                        )

                        Text(
                            text = "\uD83C\uDFC3\u200D♀\uFE0F 활동량 비교",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF131313),
                            modifier = Modifier.align(Alignment.Start)
                        )

                        Spacer(modifier = Modifier.padding(3.dp))

                        WeeklyLineChart(listOf(2, 2, 1, 0, 3, 2, 4))
                    }
                }
            }
        }
    }
}



@Composable
fun WeeklyLineChart(data: List<Int>) {
    val days = listOf("월", "화", "수", "목", "금", "토", "일")
    val maxY = 4

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .padding(16.dp)
    ) {
        val width = size.width
        val height = size.height
        val spacingX = width / (data.size - 1)
        val spacingY = height / maxY

        val path = Path()

        // ✅ 1. 회색 네모 상자 (그래프 테두리 박스)
        drawRect(
            color = Color.LightGray,
            style = Stroke(width = 3f)
        )

        // ✅ 2. 가로 격자선
        for (i in 0..maxY) {
            val y = height - (i * spacingY)
            drawLine(
                color = Color.LightGray,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1f
            )
        }

        // ✅ 3. 선 경로 및 점 찍기
        data.forEachIndexed { index, value ->
            val x = spacingX * index
            val y = height - (value * spacingY)

            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }

            drawCircle(
                color = Color.Blue,
                center = Offset(x, y),
                radius = 6f
            )
        }

        // ✅ 4. 파란 선 그리기
        drawPath(
            path = path,
            color = Color.Blue,
            style = Stroke(width = 4f)
        )

        // ✅ 5. X축 요일 텍스트
        days.forEachIndexed { index, day ->
            val x = spacingX * index
            drawContext.canvas.nativeCanvas.drawText(
                day,
                x,
                height + 40f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 30f
                    textAlign = android.graphics.Paint.Align.CENTER
                }
            )
        }

        // ✅ 6. Y축 숫자
        for (i in 0..maxY) {
            val y = height - (i * spacingY)
            drawContext.canvas.nativeCanvas.drawText(
                i.toString(),
                -20f,
                y + 10f,
                android.graphics.Paint().apply {
                    color = android.graphics.Color.GRAY
                    textSize = 28f
                    textAlign = android.graphics.Paint.Align.RIGHT
                }
            )
        }
    }
}



@Preview
@Composable
fun WeeklyPlanPreview() {
    SmartMonitorTheme {
        WeeklyPlanScreen(navController = rememberNavController())
    }
}
