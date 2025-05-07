package com.keremsen.wordmaster.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHost
import androidx.navigation.compose.rememberNavController
import com.keremsen.wordmaster.view.MainScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    NavHost(
        navController  = navController,
        startDestination  = "MainScreen"
    ){

    }
}