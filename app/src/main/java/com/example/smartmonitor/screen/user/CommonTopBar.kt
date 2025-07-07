package com.example.smartmonitor.screen.user

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.smartmonitor.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonTopBar(title: String, navController: NavController, onMenuClick: () -> Unit) {
    TopAppBar(
        modifier = Modifier.shadow(5.dp),
        title = {
            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )
        },
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(painter = painterResource(id = R.drawable.back), contentDescription = "Back", tint = Color.Black)
            }
        },
        actions = {
            IconButton(onClick = { onMenuClick() }) {
                Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu", tint = Color.Black)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White, // 배경색
            titleContentColor = Color.Black // 글자색
        )
    )
}
