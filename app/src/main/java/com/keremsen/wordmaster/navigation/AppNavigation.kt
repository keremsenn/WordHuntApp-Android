package com.keremsen.wordmaster.navigation



import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.keremsen.wordmaster.view.LevelScreen
import com.keremsen.wordmaster.view.MainScreen
import com.keremsen.wordmaster.view.ProfileScreen
import com.keremsen.wordmaster.view.ResultScreen
import com.keremsen.wordmaster.view.SettingScreen
import com.keremsen.wordmaster.view.SplashScreen
import com.keremsen.wordmaster.viewmodel.AuthViewModel
import com.keremsen.wordmaster.viewmodel.LevelManagerViewModel
import com.keremsen.wordmaster.viewmodel.MusicPlayerViewModel
import com.keremsen.wordmaster.viewmodel.SettingsViewModel
import com.keremsen.wordmaster.viewmodel.WordViewModel


@Composable
fun AppNavigation(settingsViewModel: SettingsViewModel,musicPlayerViewModel: MusicPlayerViewModel,authViewModel:AuthViewModel,wordViewModel: WordViewModel) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "SplashScreen"
    ) {
        composable("SplashScreen") {
            SplashScreen(navController)
        }
        composable(
            route = "ResultScreen/{level}",
            arguments = listOf(
                navArgument("level") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getInt("level")
            if (level != null) {
                ResultScreen(navController,settingsViewModel, level,authViewModel)
            }
        }
        composable(
            route = "LevelScreen/{level}",
            arguments = listOf(
                navArgument("level") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val level = backStackEntry.arguments?.getInt("level")
            if (level != null) {
                LevelScreen(navController, wordViewModel,authViewModel,settingsViewModel, level)
            }
        }

        composable(
            route = "MainScreen",
        ) {
            MainScreen(navController = navController,settingsViewModel,authViewModel)
        }

        composable(
            route = "SettingScreen",

        ) {
            SettingScreen(navController = navController,settingsViewModel,musicPlayerViewModel)
        }
        composable(route = "ProfileScreen"){
            ProfileScreen(navController = navController,settingsViewModel,authViewModel,musicPlayerViewModel)
        }
    }
}
