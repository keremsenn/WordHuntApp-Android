package com.keremsen.wordmaster.view

import android.content.Context
import android.media.SoundPool
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.keremsen.wordmaster.R
import com.keremsen.wordmaster.utils.BannerAdd
import com.keremsen.wordmaster.viewmodel.UserManagerViewModel
import com.keremsen.wordmaster.viewmodel.SettingsViewModel

@Composable
fun ResultScreen(navController: NavController, settingsViewModel: SettingsViewModel, level: Int) {
    val context = LocalContext.current

    val sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
    val levelManager = remember { UserManagerViewModel(sharedPreferences) }

    val isSoundOn = settingsViewModel.isSoundOn
    val soundPool = remember {
        SoundPool.Builder()
            .setMaxStreams(1)
            .build()
    }
    val soundId = remember {
        soundPool.load(context, R.raw.clikedsound, 1)
    }
    // Level artırma işlemi
    LaunchedEffect(Unit) {
        levelManager.saveLevel(level + 1)
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Arkaplan resmi
            Image(
                painter = painterResource(R.drawable.levelbackground2),
                contentDescription = "background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box (modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.7f)))
            // Transparant box
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .fillMaxHeight(0.7f)
                    .align(Alignment.Center)
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Üst kısım - Tebrikler yazısı
                    Text(
                        text = "Tebrikler",
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 32.dp)
                    )

                    // Orta kısım - Level bilgisi
                    Text(
                        text = "Level $level'ı Başarıyla Tamamladın",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    // Alt kısım - Butonlar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Ana Sayfa butonu
                        Button(
                            onClick = {
                                if (isSoundOn) {
                                    soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
                                }
                                navController.navigate("MainScreen") {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(Color(0xFF4CAF50)),
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp)
                        ) {
                            Text(
                                text = "Ana Sayfa",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Yeni Level butonu
                        Button(
                            onClick = {
                                if (isSoundOn) {
                                    soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
                                }
                                navController.navigate("LevelScreen/${level + 1}") {
                                    popUpTo(0) { inclusive = true }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(Color(0xFF2196F3)),
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        ) {
                            Text(
                                text = "Sonraki Level",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

            }
            BannerAdd(  modifier = Modifier
                .fillMaxWidth()
                .height(60.dp).align(Alignment.BottomCenter))
        }
    }
}

