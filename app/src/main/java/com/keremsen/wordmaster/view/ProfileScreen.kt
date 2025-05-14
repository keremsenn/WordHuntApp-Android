package com.keremsen.wordmaster.view


import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.keremsen.wordmaster.R
import com.keremsen.wordmaster.viewmodel.MusicPlayerViewModel
import com.keremsen.wordmaster.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun ProfileScreen(navController: NavController,settingsViewModel: SettingsViewModel) {
    val isSoundOn = settingsViewModel.isSoundOn
    val context = LocalContext.current

    val coroutineScope = rememberCoroutineScope()
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.clikedsound) }

    // Animasyon durumu
    var visible by remember { mutableStateOf(false) }
    val offsetY = animateFloatAsState(
        targetValue = if (visible) 0f else 1000f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    ).value

    val alpha = animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 300)
    ).value

    fun handleBack() {
        coroutineScope.launch {
            visible = false
            delay(200)
            navController.popBackStack()
        }
    }

    BackHandler {
        handleBack()
    }

    DisposableEffect(Unit) {
        visible = true
        onDispose {
            mediaPlayer.release()
            visible = false
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Arka Plan Resmi",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(65.dp)
                    .graphicsLayer {
                        translationY = offsetY
                        this.alpha = alpha
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            if (isSoundOn)
                                mediaPlayer.start()
                            visible = false
                            delay(200)
                            navController.navigate("MainScreen") {
                                popUpTo("ProfileScreen") { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.size(55.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cancel),
                        contentDescription = "cancel",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(55.dp)
                    )
                }
            }



            }
        }
}