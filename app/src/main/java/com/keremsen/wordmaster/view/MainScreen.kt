package com.keremsen.wordmaster.view

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.keremsen.wordmaster.R
import kotlinx.coroutines.launch

@Composable
fun MainScreen(navController: NavController) {
    // ANİMASYONLAR
    val scale = remember { Animatable(0.1f) }
    val alpha = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scale.animateTo(1f, animationSpec = tween(300))
        alpha.animateTo(1f, animationSpec = tween(300))
    }

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
                            scale.animateTo(0.1f, animationSpec = tween(300))
                            alpha.animateTo(0f, animationSpec = tween(300))
                            navController.navigate("SettingsScreen")
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

            // BAŞLA butonu animasyonlu
            Button(
                onClick = {  },
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
                    fontSize = 20.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )
            }
        }
    }
}
