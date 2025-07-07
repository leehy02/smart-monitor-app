package com.example.smartmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.smartmonitor.screen.AImenuScreen
import com.example.smartmonitor.screen.AIreportScreen
import com.example.smartmonitor.screen.CBTmodeScreen
import com.example.smartmonitor.screen.GuideScreen
import com.example.smartmonitor.screen.MainScreen
import com.example.smartmonitor.screen.MenuScreen
import com.example.smartmonitor.ui.theme.SmartMonitorTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SmartMonitorTheme {
                // NavController 설정
                val navController = rememberNavController()

                // NavHost 설정 (main_screen을 시작 화면으로 설정)
                NavHost(navController = navController, startDestination = "main_screen") {
                    composable("main_screen") { MainScreen(navController) }
                    composable("menu_screen") { MenuScreen(navController) }
                    composable("aimenu_screen") { AImenuScreen(navController) }
                    composable("guide_screen") { GuideScreen(navController) }
                    composable("aireport_screen") { AIreportScreen(navController) }
                    composable("cbtmode_screen") { CBTmodeScreen(navController) }
                }
            }
        }
    }
}