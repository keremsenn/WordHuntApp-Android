package com.keremsen.wordmaster.view

import android.content.Context
import android.media.SoundPool
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.keremsen.wordmaster.R
import com.keremsen.wordmaster.viewmodel.LevelManagerViewModel
import com.keremsen.wordmaster.viewmodel.SettingsViewModel
import kotlinx.coroutines.launch


@Composable
fun MainScreen(navController: NavController,settingsViewModel: SettingsViewModel) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
    val levelManager = remember { LevelManagerViewModel(sharedPreferences) }

    val currentLevel = levelManager.getLevel().toString()
    val isSoundOn = settingsViewModel.isSoundOn

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    // ANİMASYONLAR
    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.birdanimation)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    val scale = remember { Animatable(1f) }
    val alpha = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()

    val soundPool = remember {
        SoundPool.Builder()
            .setMaxStreams(1)
            .build()
    }
    val soundId = remember {
        soundPool.load(context, R.raw.clikedsound, 1)
    }


    val animatedRadius by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 30f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = EaseInOut),
            repeatMode = RepeatMode.Restart
        ),
        label = "radius"
    )

    val animatedAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = EaseInOut),
            repeatMode = RepeatMode.Restart
        ),
        label = "alpha"
    )


    Surface(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            // Arka plan
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = "Background Image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Üst bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .alpha(alpha.value)
                    .scale(scale.value),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profil butonu
                IconButton(
                    onClick = {
                        coroutineScope.launch {

                            if (isSoundOn) {
                                soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
                            }

                            scale.animateTo(0.1f, animationSpec = tween(300))
                            alpha.animateTo(0f, animationSpec = tween(300))
                            navController.navigate("ProfileScreen")
                        }
                    },
                    modifier = Modifier.size(60.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile2),
                        contentDescription = "Profile",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(60.dp)
                    )
                }

                // Ayarlar butonu
                IconButton(
                    onClick = {
                        coroutineScope.launch {

                            if (isSoundOn) {
                                soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
                            }

                            scale.animateTo(0.1f, animationSpec = tween(300))
                            alpha.animateTo(0f, animationSpec = tween(300))
                            navController.navigate("SettingScreen")
                        }
                    },
                    modifier = Modifier.size(60.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.settings2),
                        contentDescription = "Settings",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(60.dp)
                    )
                }
            }

            Box(modifier = Modifier.fillMaxSize()) {
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier.fillMaxSize()
                )
            }
            Box(
                modifier = Modifier
                    .size(250.dp)
                    .align(Alignment.Center)
                    .drawBehind {
                        // Glow efekti sabit opaklıkla
                        drawCircle(
                            color = Color.White.copy(alpha = animatedAlpha),
                            radius = size.minDimension / 2 + animatedRadius,
                            center = center,
                        )
                    }
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Mevcut Bölüm",
                        color = Color.White,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = currentLevel,
                        color = Color.White,
                        fontSize = 38.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }


            // BAŞLA butonu
            Button(
                onClick = {

                    if (isSoundOn) {
                        soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
                    }
                    navController.navigate("LevelScreen/${currentLevel}")
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp)
                    .width(200.dp)
                    .height(56.dp)
                    .scale(scale.value)
                    .alpha(alpha.value),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4CAF50)
                )
            ) {
                Text(
                    text = "BAŞLA",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

}

