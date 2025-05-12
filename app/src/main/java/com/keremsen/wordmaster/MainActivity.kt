package com.keremsen.wordmaster

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.keremsen.wordmaster.navigation.AppNavigation
import com.keremsen.wordmaster.viewmodel.MusicPlayerViewModel
import com.keremsen.wordmaster.viewmodel.SettingsViewModel


class MainActivity : ComponentActivity() {
    private val settingsViewModel: SettingsViewModel by viewModels()
    private val musicPlayerViewModel: MusicPlayerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycle.addObserver(musicPlayerViewModel)
        setContent {
            AppNavigation(settingsViewModel ,musicPlayerViewModel)
        }
        musicPlayerViewModel.startMusic()
    }
}

