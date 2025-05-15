package com.keremsen.wordmaster.view


import androidx.compose.foundation.background
import com.keremsen.wordmaster.R
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(navController: NavController) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.loadinganimation))
    val progress by animateLottieCompositionAsState(composition = composition,iterations = LottieConstants.IterateForever )
    LaunchedEffect(Unit) {
        delay(3000)
        navController.navigate("MainScreen") {
            popUpTo("SplashScreen") { inclusive = true }
        }
    }
    Column(modifier = Modifier.fillMaxSize()
        .background(color = colorResource(id=R.color.LoadingBackGroundColor))) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(200.dp)
            )
        }
    }



}