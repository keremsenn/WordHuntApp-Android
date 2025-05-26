package com.keremsen.wordmaster.viewmodel

import android.content.SharedPreferences
import android.os.Build
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class UserManagerViewModel(private val sharedPreferences: SharedPreferences): ViewModel() {

    private val dateFormatApi26 = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
    } else null

    private val dateFormatLegacy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private fun getTodayDateString(): String {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            java.time.LocalDate.now().format(dateFormatApi26)
        } else {
            dateFormatLegacy.format(Date())
        }
    }

    fun getHintBonus(): Int {
        checkAndResetDailyBonus()
        return sharedPreferences.getInt("hint_bonus", 5)
    }

    fun useHint(): Int {
        checkAndResetDailyBonus()
        val current = getHintBonus()
        if (current > 0) {
            sharedPreferences.edit().putInt("hint_bonus", current - 1).apply()
            return current - 1
        }
        return 0
    }

    private fun checkAndResetDailyBonus() {
        val today = getTodayDateString()
        val lastDate = sharedPreferences.getString("last_bonus_date", "")

        if (today != lastDate) {
            sharedPreferences.edit()
                .putInt("hint_bonus", 5)
                .putString("last_bonus_date", today)
                .apply()
        }
    }

    fun saveLevel(level: Int) {
        sharedPreferences.edit().putInt("user_level", level).apply()
    }

    fun getLevel(): Int {
        return sharedPreferences.getInt("user_level", 1)
    }

    fun resetLevel() {
        sharedPreferences.edit().putInt("user_level", 1).apply()
    }

    fun saveName(name: String) {
        sharedPreferences.edit().putString("user_name", name).apply()
    }

    fun getName(): String {
        return sharedPreferences.getString("user_name", "Misafir583834") ?: "Misafir583834"
    }

    fun resetName() {
        sharedPreferences.edit().putString("user_name", "Misafir583834").apply()
    }

    fun deleteAcount(){
        sharedPreferences.edit()
            .putInt("user_level", 1)
            .putString("user_name", "Misafir583834")
            .putInt("hint_bonus", 5)
            .putString("last_bonus_date", getTodayDateString())
            .apply()
    }


}