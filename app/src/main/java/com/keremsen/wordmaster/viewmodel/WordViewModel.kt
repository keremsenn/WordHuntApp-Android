package com.keremsen.wordmaster.viewmodel


import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.keremsen.wordmaster.R
import com.keremsen.wordmaster.model.Word
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

class WordViewModel(application: Application) : AndroidViewModel(application) {
    private val _kelimeler = MutableStateFlow<List<Word>>(emptyList())
    val kelimeler: StateFlow<List<Word>> = _kelimeler

    // Yükleme durumunu izleyen bir state ekleyelim
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadKelimeler()
    }

    fun loadKelimeler() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val context = getApplication<Application>().applicationContext
                val inputStream = context.resources.openRawResource(R.raw.wordandmeaning)
                val reader = InputStreamReader(inputStream, Charsets.UTF_8)

                // Okuma işlemini biraz gecikmeyi simüle etmek için withContext ile yapıyoruz
                // Bu gerçek uygulamada gerekli değil, sadece göstermek için
                val kelimeList = withContext(Dispatchers.IO) {
                    val kelimeListType = object : TypeToken<List<Word>>() {}.type
                    Gson().fromJson<List<Word>>(reader, kelimeListType)
                }

                _kelimeler.value = kelimeList
                reader.close()
                _isLoading.value = false
            } catch (e: Exception) {
                Log.e("WordViewModel", "Kelimeler yüklenirken hata oluştu", e)
                e.printStackTrace()
                _isLoading.value = false
            }
        }
    }
}