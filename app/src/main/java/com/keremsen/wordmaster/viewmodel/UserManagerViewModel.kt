package com.keremsen.wordmaster.viewmodel

import android.content.SharedPreferences
import android.os.Build
import androidx.lifecycle.ViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.core.content.edit

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
            sharedPreferences.edit { putInt("hint_bonus", current - 1) }
            return current - 1
        }
        return 0
    }

    fun addHintBonusWithAdReward(amount: Int = 3, onComplete: (newTotal: Int) -> Unit) {
        checkAndResetDailyBonus()
        val current = getHintBonus()
        val updated = (current + amount).coerceAtMost(5) // En fazla 5 olabilir
        sharedPreferences.edit { putInt("hint_bonus", updated) }
        onComplete(updated)
    }

    private fun checkAndResetDailyBonus() {
        val today = getTodayDateString()
        val lastDate = sharedPreferences.getString("last_bonus_date", "")

        if (today != lastDate) {
            sharedPreferences.edit {
                putInt("hint_bonus", 5)
                    .putString("last_bonus_date", today)
            }
        }
    }

    fun saveLevel(level: Int) {
        sharedPreferences.edit { putInt("user_level", level) }
    }

    fun getLevel(): Int {
        return sharedPreferences.getInt("user_level", 1)
    }

    fun resetLevel() {
        sharedPreferences.edit { putInt("user_level", 1) }
    }

    fun saveName(name: String) {
        sharedPreferences.edit { putString("user_name", name) }
    }

    fun getName(): String {
        return sharedPreferences.getString("user_name", "Misafir583834") ?: "Misafir583834"
    }

    fun resetName() {
        sharedPreferences.edit { putString("user_name", "Misafir583834") }
    }

    fun deleteAcount(){
        sharedPreferences.edit {
            putInt("user_level", 1)
                .putString("user_name", "Misafir583834")
                .putInt("hint_bonus", 5)
                .putString("last_bonus_date", getTodayDateString())
        }
    }


}