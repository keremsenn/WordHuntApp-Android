package com.keremsen.wordmaster.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.keremsen.wordmaster.view.MainScreen
import com.keremsen.wordmaster.view.ProfileScreen
import com.keremsen.wordmaster.view.SettingScreen


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "MainScreen"
    ) {
        composable(
            route = "MainScreen",
        ) {
            MainScreen(navController = navController)
        }

        composable(
            route = "SettingScreen",

        ) {
            SettingScreen(navController = navController)
        }
        composable(route = "ProfileScreen"){
            ProfileScreen(navController = navController)
        }
    }
}
