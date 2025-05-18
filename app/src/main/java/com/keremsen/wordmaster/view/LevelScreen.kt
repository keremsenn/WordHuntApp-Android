package com.keremsen.wordmaster.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.keremsen.wordmaster.R
import com.keremsen.wordmaster.model.User
import com.keremsen.wordmaster.viewmodel.AuthViewModel
import com.keremsen.wordmaster.viewmodel.LevelManagerViewModel
import com.keremsen.wordmaster.viewmodel.WordViewModel
import kotlinx.coroutines.delay
import androidx.activity.compose.BackHandler
import androidx.compose.material3.TextButton

@Composable
fun LevelScreen(navController: NavController, wordViewModel: WordViewModel, authViewModel: AuthViewModel, level: Int) {
    val context = LocalContext.current
    val sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
    val levelManager = remember { LevelManagerViewModel(sharedPreferences) }
    val currentLevel = levelManager.getLevel().toString()

    var showExitDialog by remember { mutableStateOf(false) }


    // Geri tuşu için handler
    BackHandler {
        showExitDialog = true
    }

    // Çıkış dialogu
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Çıkış Onayı") },
            text = { Text("Çıkmak istediğinizden emin misiniz?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showExitDialog = false
                        navController.navigate("MainScreen") {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) {
                    Text("Evet")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showExitDialog = false }
                ) {
                    Text("Hayır")
                }
            }
        )
    }

    var currentUser by remember { mutableStateOf(User()) }
    authViewModel.userData.observeAsState().value?.let { newUserData ->
        currentUser = newUserData
    }

    // Verileri ve yükleme durumunu alma
    val words by wordViewModel.kelimeler.collectAsState(initial = emptyList())
    val isLoading by wordViewModel.isLoading.collectAsState()

    // Eğer veriler boşsa ve yüklenmiyorsa, veriyi yeniden yüklemeyi dene
    LaunchedEffect(key1 = Unit) {
        if (words.isEmpty() && !isLoading) {
            wordViewModel.loadKelimeler()
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Arkaplan resmi
            Image(
                painter = painterResource(R.drawable.levelbackground),
                contentDescription = "background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Yükleme durumu veya içerik göster
            if (isLoading) {
                // Yükleme göstergesi
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(50.dp),
                        color = Color.Blue
                    )
                    Text(
                        text = "Seviye yükleniyor...",
                        modifier = Modifier.padding(top = 16.dp),
                        fontSize = 18.sp,
                        color = Color.Black
                    )
                }
            } else {
                // Veriler yüklendiyse içeriği göster
                val userLevel = if (currentUser.level == -1) currentLevel.toInt() else currentUser.level
                val currentWord = words.find { it.id == userLevel }

                if (currentWord == null) {
                    // Kelime bulunamadıysa hata mesajı göster
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Seviye ${userLevel} bulunamadı. Lütfen daha sonra tekrar deneyin.",
                            color = Color.Black,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(16.dp)
                        )

                        Button(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Geri Dön")
                        }
                    }
                } else {
                    // Kelime bulundu, oyun ekranını göster
                    val letterCount = currentWord.word.length
                    var inputLetters by remember { mutableStateOf(List(letterCount) { "" }) }
                    var currentInput by remember { mutableStateOf("") }
                    var isWrongAnswer by remember { mutableStateOf(false) }
                    var isCorrectAnswer by remember { mutableStateOf(false) }
                    var boxScale by remember { mutableStateOf(1f) }

                    // Yanlış cevap animasyonu için
                    LaunchedEffect(isWrongAnswer) {
                        if (isWrongAnswer) {
                            boxScale = 1.2f
                            delay(1000) // 1 saniye bekle
                            boxScale = 1f
                            isWrongAnswer = false
                        }
                    }

                    // Doğru cevap animasyonu için
                    LaunchedEffect(isCorrectAnswer) {
                        if (isCorrectAnswer) {
                            boxScale = 1.2f
                            delay(1000) // 1 saniye bekle
                            boxScale = 1f
                            isCorrectAnswer = false
                            
                            // Sadece ResultScreen'e yönlendir
                            navController.navigate("ResultScreen/$userLevel") {
                                popUpTo("LevelScreen/$userLevel") { inclusive = true }
                            }
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top
                    ) {
                        // Header Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Level bilgisi
                            Spacer(modifier = Modifier.size(55.dp))
                            Text(
                                text = "Level ${userLevel}",
                                color = Color.Black,
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Ana sayfa ikonu
                            IconButton(
                                onClick = { showExitDialog = true },
                                modifier = Modifier.size(55.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.homepageicon),
                                    contentDescription = "Ana Sayfa",
                                    modifier = Modifier.size(55.dp)
                                )
                            }
                            // Sağ tarafta boşluk için

                        }

                        // Body Row with letter boxes
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp, Alignment.CenterHorizontally),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                maxItemsInEachRow = Int.MAX_VALUE
                            ) {
                                repeat(letterCount) { index ->
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .scale(boxScale)
                                            .background(
                                                color = when {
                                                    isWrongAnswer -> Color(0xFFB71C1C)
                                                    isCorrectAnswer -> Color(0xFF1B5E20)
                                                    else -> Color.Blue
                                                },
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .border(
                                                width = 2.dp,
                                                color = Color.White,
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = inputLetters.getOrElse(index) { "" },
                                            style = TextStyle(
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                textAlign = TextAlign.Center
                                            )
                                        )
                                    }
                                }
                            }
                        }

                        // İpucu / Kelime anlamı göster
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.7f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "İpucu: ${currentWord.meaning}",
                                fontSize = 18.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center
                            )
                        }

                        // Tek giriş kutusu
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            BasicTextField(
                                value = currentInput,
                                onValueChange = { newValue ->
                                    // Sadece küçük harflere izin ver ve harf dışındaki karakterleri filtrele
                                    val filteredInput = newValue.lowercase().filter { it.isLetter() }
                                    
                                    if (filteredInput.length <= letterCount) {
                                        currentInput = filteredInput
                                        // Girilen harfleri kutulara dağıt
                                        val newList = List(letterCount) { index ->
                                            if (index < filteredInput.length) filteredInput[index].toString() else ""
                                        }
                                        inputLetters = newList
                                    }
                                },
                                textStyle = TextStyle(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    textAlign = TextAlign.Center
                                ),
                                decorationBox = { innerTextField ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(0.8f)
                                            .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                            .padding(16.dp)
                                    ) {
                                        if (currentInput.isEmpty()) {
                                            Text(
                                                text = "Cevabınızı buraya yazın...",
                                                style = TextStyle(
                                                    fontSize = 24.sp,
                                                    fontWeight = FontWeight.Normal,
                                                    color = Color.Black.copy(alpha = 0.5f),
                                                    textAlign = TextAlign.Center
                                                ),
                                                modifier = Modifier.fillMaxWidth()
                                            )
                                        }
                                        innerTextField()
                                    }
                                }
                            )
                        }

                        // Cevap kontrol butonu
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button(
                                onClick = {
                                    // Kullanıcının girdiği kelimeyi kontrol et
                                    val userInput = currentInput
                                    if (userInput == currentWord.word.lowercase()) {
                                        // Doğru cevap - animasyonu başlat
                                        isCorrectAnswer = true
                                    } else {
                                        // Yanlış cevap - animasyonu başlat
                                        isWrongAnswer = true
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(Color.Green),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.padding(top = 16.dp)
                            ) {
                                Text(
                                    text = "Kontrol Et",
                                    color = Color.White,
                                    fontSize = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}