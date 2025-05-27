package com.keremsen.wordmaster.utils

import android.app.Activity
import android.media.SoundPool
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.keremsen.wordmaster.viewmodel.UserManagerViewModel
import loadAndShowRewardedInterstitialAd


@Composable
fun BonusAdPopup(
    userManagerViewModel: UserManagerViewModel,
    onWatchAd: () -> Unit,
    onDismiss: () -> Unit,
    title:String,
    content:String,
    isInformation : Boolean,
    isSoundOn:Boolean,
    soundPool: SoundPool,
    soundId: Int,
    onRewardEarned: () -> Unit,
    isLoading: Boolean,
    setIsLoading: (Boolean) -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable(
                onClick = {
                    if (!isLoading) {
                        onDismiss()
                    }
                },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .zIndex(10f)
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            elevation = CardDefaults.cardElevation(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 32.dp)
                .wrapContentHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "Bonus",
                    tint = Color(0xFFFFC107),
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = title,
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = content,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if(!isInformation)
                        Button(
                            onClick = {
                                if(isSoundOn)
                                    soundPool.play(soundId,1f, 1f, 1, 0, 1f)

                                setIsLoading(true)

                                activity?.let {
                                    loadAndShowRewardedInterstitialAd(
                                        context = context,
                                        activity = activity,
                                        onRewardEarned = { amount, type ->
                                            if (amount > 0) {
                                                userManagerViewModel.addHintBonusWithAdReward(amount = 3) { newTotal ->
                                                    Toast.makeText(
                                                        context,
                                                        "3 ipucu kazandınız! Toplam: $newTotal",
                                                        Toast.LENGTH_SHORT
                                                    ).show()
                                                    onRewardEarned()
                                                    onDismiss()
                                                }
                                            }else { setIsLoading(false)}
                                        },
                                        onAdDismissed = {
                                            // Reklam kapatıldığında yapılacaklar
                                            setIsLoading(false)
                                        }
                                    )
                                }
                                      },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50)),
                            enabled = !isLoading
                        ) {
                            Text("Reklam İzle", fontSize = 16.sp, color = Color.White)
                        }

                    OutlinedButton(
                        onClick = {
                            if(isSoundOn)
                                soundPool.play(soundId,1f, 1f, 1, 0, 1f)
                            onDismiss()
                                  },
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, Color.Gray),
                        enabled = !isLoading
                    ) {


                        Text("Kapat", fontSize = 16.sp)
                    }
                }
            }

        }
    }
    if (isLoading) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .zIndex(20f),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = Color.Blue)
        }
    }
}

