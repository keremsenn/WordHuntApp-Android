package com.keremsen.wordmaster.view


import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.keremsen.wordmaster.R
import com.keremsen.wordmaster.viewmodel.MusicPlayerViewModel
import com.keremsen.wordmaster.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun SettingScreen(navController: NavController, settingsViewModel: SettingsViewModel,musicPlayerViewModel: MusicPlayerViewModel) {
    val context = LocalContext.current

    val isSoundOn = settingsViewModel.isSoundOn
    val isMusicOn = musicPlayerViewModel.isMusicOn.value

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
                                popUpTo("SettingScreen") { inclusive = true }
                            }
                        }
                    },
                    modifier = Modifier.size(55.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cancel),
                        contentDescription = "Profile",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(55.dp)
                    )
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        translationY = offsetY
                        this.alpha = alpha
                    },
                contentAlignment = Alignment.TopCenter
            ) {

                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.82f)
                        .fillMaxHeight(0.90f)
                        .border(3.dp, Color.White.copy(0.8f), RoundedCornerShape(32.dp))
                        .padding(15.dp),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 20.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "AYARLAR",
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        //music
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(if (isMusicOn) colorResource(R.color.Green) else Color.Transparent)
                                .border(
                                    2.dp,
                                    if (isMusicOn) colorResource(R.color.DarkGreen) else Color.White,
                                    CircleShape
                                )
                                .clickable {
                                    if (isSoundOn) mediaPlayer.start()

                                    if(isMusicOn)
                                    musicPlayerViewModel.stopMusic() else musicPlayerViewModel.startMusic()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = if (isMusicOn) R.drawable.music else R.drawable.nomusic),
                                contentDescription = "audio",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(35.dp)
                            )
                        }
                        //sound
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(if (isSoundOn) colorResource(R.color.Green) else Color.Transparent)
                                .border(
                                    2.dp,
                                    if (isSoundOn) colorResource(R.color.DarkGreen) else Color.White,
                                    CircleShape
                                )
                                .clickable {
                                    if (!isSoundOn) mediaPlayer.start()
                                    settingsViewModel.toggleSound()
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = if (isSoundOn) R.drawable.audio else R.drawable.noaudio),
                                contentDescription = "audio",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.size(35.dp)
                            )
                        }
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Row (
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ){
                        Text(
                            text = "Icons by Icons8",
                            fontSize = 15.sp,
                            color = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    val intent =
                                        Intent(Intent.ACTION_VIEW, Uri.parse("https://icons8.com"))
                                    context.startActivity(intent)
                                }
                        )
                    }


                }


            }
        }
    }
}