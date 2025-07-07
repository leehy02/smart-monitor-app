package com.example.smartmonitor.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import com.example.smartmonitor.R
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.smartmonitor.screen.ui.theme.SmartMonitorTheme

@Composable
fun MainScreen(navController: NavController) {
    Column(
        modifier = Modifier
        .fillMaxSize()
        .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.height(30.dp))

        Text(
            text = "스마트 모니터",
            fontSize = 40.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(80.dp))

        Image(
            painter = painterResource(id = R.drawable.healthmonitor),
            contentDescription = "Main Logo Image",
            modifier = Modifier
                .size(180.dp)
        )

        Spacer(Modifier.height(80.dp))

        Button(
            onClick = {navController.navigate("menu_screen")},
            modifier = Modifier
                .width(300.dp)
                .height(70.dp),
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(Color(0xFF4469FF))
        ) {
            Text(text="시작하기",
                color = Color.White,
                fontSize = 35.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun MainPreview() {
    SmartMonitorTheme {
        MainScreen(navController = rememberNavController()) // 미리보기용 NavController 사용
    }
}