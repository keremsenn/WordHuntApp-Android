package com.keremsen.wordmaster.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel

class LevelManagerViewModel(private val sharedPreferences: SharedPreferences): ViewModel() {
    fun saveLevel(level: Int) {
        sharedPreferences.edit().putInt("user_level", level).apply()
    }

    fun getLevel(): Int {
        return sharedPreferences.getInt("user_level", 1)
    }

    fun resetLevel() {
        sharedPreferences.edit().putInt("user_level", 1).apply()
    }
}