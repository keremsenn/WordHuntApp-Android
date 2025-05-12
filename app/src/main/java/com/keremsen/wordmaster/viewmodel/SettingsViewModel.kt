package com.keremsen.wordmaster.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel


class SettingsViewModel() : ViewModel() {

    
    var isSoundOn by mutableStateOf( true)
        private set
        


    fun toggleSound() {
        isSoundOn = !isSoundOn
    }


}