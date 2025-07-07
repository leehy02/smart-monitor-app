package com.example.smartmonitor.screen.modal

import androidx.compose.foundation.Image
import com.example.smartmonitor.R
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.compose.rememberNavController
import com.example.smartmonitor.screen.AImenuScreen
import com.example.smartmonitor.screen.ui.theme.SmartMonitorTheme

@Composable
fun ResetModal(onDismiss: () -> Unit){ // Unit == void
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .width(300.dp)
                .height(400.dp)
                .background(Color.Transparent),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column (
                modifier = Modifier
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ){
                Spacer(Modifier.height(30.dp))
                Image(
                    painterResource(id=R.drawable.reset),
                    contentDescription = "reset",
                    modifier = Modifier.size(80.dp)
                )
                Spacer(Modifier.height(15.dp))
                Text(
                    text = "초기화 기능 실행",
                    fontWeight = FontWeight.Bold,
                    fontSize = 30.sp,
                    color = Color(0xFF474747)
                )
                Spacer(Modifier.height(15.dp))
                Text(
                    text = "모니터 암을 초기 상태로 \n" +
                            "되돌립니다. \n" +
                            "실행되고 있으니 \n" +
                            "잠시 기다려 주세요!",
                    fontWeight = FontWeight.Bold,
                    fontSize = 23.sp,
                    lineHeight = 30.sp,
                    color = Color(0xFF6A6A6A),
                    textAlign = TextAlign.Center,
                )
                Spacer(Modifier.height(15.dp))
                Button(
                    onClick = onDismiss,
                    modifier = Modifier
                        .width(250.dp)
                        .height(60.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(Color(0xFF4469FF))
                ) {
                    Text(
                        text = "확인",
                        fontWeight = FontWeight.Bold,
                        fontSize = 30.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ResetModalPreview() {
    SmartMonitorTheme {
        ResetModal(onDismiss = {})
    }
}