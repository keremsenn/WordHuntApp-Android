package com.keremsen.wordmaster.utils

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.LoadAdError
import android.util.Log

@Composable
fun ResultBannerAd(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    AndroidView(
        factory = { ctx ->
            AdView(ctx).apply {
                setAdSize(AdSize.FULL_BANNER)
                // Test reklam ID'si
                adUnitId = "ca-app-pub-3940256099942544/6300978111"

                // Reklam yükleme durumunu takip et
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        super.onAdLoaded()
                        Log.d("AdMob", "Banner reklam başarıyla yüklendi")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        super.onAdFailedToLoad(error)
                        Log.e("AdMob", "Banner reklam yüklenemedi: ${error.message}")
                        Log.e("AdMob", "Hata kodu: ${error.code}")
                    }

                    override fun onAdOpened() {
                        super.onAdOpened()
                        Log.d("AdMob", "Banner reklam açıldı")
                    }
                }

                // Reklam yükle
                loadAd(AdRequest.Builder().build())
            }
        },
        modifier
    )
}