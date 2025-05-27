package com.keremsen.wordmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.keremsen.wordmaster.navigation.AppNavigation
import com.keremsen.wordmaster.viewmodel.MusicPlayerViewModel
import com.keremsen.wordmaster.viewmodel.SettingsViewModel
import com.keremsen.wordmaster.viewmodel.WordViewModel


class MainActivity : ComponentActivity() {
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val musicPlayerViewModel: MusicPlayerViewModel by viewModels()
    private val wordViewModel: WordViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // AdMob'u başlat
        MobileAds.initialize(this)
        val configuration = RequestConfiguration.Builder()
            .setTestDeviceIds(listOf(AdRequest.DEVICE_ID_EMULATOR)) // Emülatör için test cihaz ID'si
            .build()
        MobileAds.setRequestConfiguration(configuration)

        lifecycle.addObserver(musicPlayerViewModel)

        setContent {
                    AppNavigation(settingsViewModel, musicPlayerViewModel, wordViewModel)
        }
    }
}


