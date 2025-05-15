package com.keremsen.wordmaster.navigation



import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.keremsen.wordmaster.view.MainScreen
import com.keremsen.wordmaster.view.ProfileScreen
import com.keremsen.wordmaster.view.SettingScreen
import com.keremsen.wordmaster.view.SplashScreen
import com.keremsen.wordmaster.viewmodel.MusicPlayerViewModel
import com.keremsen.wordmaster.viewmodel.SettingsViewModel


@Composable
fun AppNavigation(settingsViewModel: SettingsViewModel,musicPlayerViewModel: MusicPlayerViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "SplashScreen"
    ) {
        composable("SplashScreen") {
            SplashScreen(navController)
        }

        composable(
            route = "MainScreen",
        ) {
            MainScreen(navController = navController,settingsViewModel,musicPlayerViewModel)
        }

        composable(
            route = "SettingScreen",

        ) {
            SettingScreen(navController = navController,settingsViewModel,musicPlayerViewModel)
        }
        composable(route = "ProfileScreen"){
            ProfileScreen(navController = navController,settingsViewModel)
        }
    }
}
