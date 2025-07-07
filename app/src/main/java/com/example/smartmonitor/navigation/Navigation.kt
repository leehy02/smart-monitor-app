package com.example.smartmonitor.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.smartmonitor.screen.AImenuScreen
import com.example.smartmonitor.screen.AIreportScreen
import com.example.smartmonitor.screen.CBTmodeScreen
import com.example.smartmonitor.screen.GuideScreen
import com.example.smartmonitor.screen.MainScreen
import com.example.smartmonitor.screen.MenuScreen

@Composable
fun SetupNavGraph(navController: NavController) {
    NavHost(navController = navController as NavHostController, startDestination = "main_screen") {
        // "main_screen" 화면을 MainScreen으로 설정
        composable("main_screen") {
            MainScreen(navController)
        }
        // 다른 화면으로 넘어갈 때 사용할 destination 추가 가능
        composable("menu_screen") {
            MenuScreen(navController)
        }
        composable("aimenu_screen") {
            AImenuScreen(navController)
        }
        composable("guide_screen") {
            GuideScreen(navController)
        }
        composable("aireport_screen") {
            AIreportScreen(navController)
        }
        composable("cbtmode_screen") {
            CBTmodeScreen(navController)
        }
    }
}