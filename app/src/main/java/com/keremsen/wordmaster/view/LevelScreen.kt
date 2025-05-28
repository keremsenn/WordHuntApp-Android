package com.keremsen.wordmaster.view

import android.content.Context
import android.media.MediaPlayer
import android.media.SoundPool
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
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.keremsen.wordmaster.viewmodel.UserManagerViewModel
import com.keremsen.wordmaster.viewmodel.WordViewModel
import kotlinx.coroutines.delay
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.material3.TextButton
import com.keremsen.wordmaster.viewmodel.SettingsViewModel
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.colorResource
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.keremsen.wordmaster.utils.BonusAdPopup
import kotlinx.coroutines.launch

@Composable
fun LevelScreen(navController: NavController, wordViewModel: WordViewModel,settingsViewModel: SettingsViewModel) {
    val context = LocalContext.current
    val focusRequester = remember { FocusRequester() }
    val isSoundOn = settingsViewModel.isSoundOn

    val isLoadingState = remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    var showAdPopup by remember { mutableStateOf(false) }


    val sharedPreferences = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
    val userManager = remember { UserManagerViewModel(sharedPreferences) }
    val hintCount = remember { mutableIntStateOf(userManager.getHintBonus()) }


    val soundPool = remember {
        SoundPool.Builder()
            .setMaxStreams(1)
            .build()
    }
    val soundId = remember {
        soundPool.load(context, R.raw.clikedsound, 1)
    }

    val bonusSound = remember {
        SoundPool.Builder()
            .setMaxStreams(1)
            .build()
    }
    val bonusSoundId = remember {
        bonusSound.load(context, R.raw.bonussoundeffect, 1)
    }

    val currentLevel = userManager.getLevel()

    val wrongSound = remember { MediaPlayer.create(context, R.raw.wronganswersound) }
    val victorySound = remember { MediaPlayer.create(context, R.raw.victorysound) }

    var showExitDialog by remember { mutableStateOf(false) }

    val composition by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.victoryanimation)
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    var startAnimation by remember { mutableStateOf(false) }
    val scale = animateFloatAsState(
        targetValue = if (startAnimation) 1f else 0f,
        animationSpec = tween(durationMillis = 700), label = ""
    )

    // Geri tuşu için handler
    BackHandler {
        showExitDialog = true
    }

    // Çıkış dialogu
    if (showExitDialog) {
        AlertDialog(
            onDismissRequest = { showExitDialog = false },
            title = { Text("Çıkış Onayı") },
            text = { Text("Ana sayfaya dönmek istediğinizden emin misiniz?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (!isLoadingState.value) {
                            if (isSoundOn) {
                                soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
                            }
                            showExitDialog = false
                            navController.navigate("MainScreen") {
                                popUpTo(0) { inclusive = true }
                            }
                        }
                    }, enabled = !isLoadingState.value
                ) {
                    Text("Evet")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        if (isSoundOn) {
                            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
                        }
                            showExitDialog = false
                    }
                ) {
                    Text("Hayır")
                }
            }
        )
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
    if (showAdPopup) {
        BonusAdPopup(
            userManager,
            onWatchAd = {
                // Reklam izlendiyse yapılacaklar
                showAdPopup = false
            },
            onDismiss = {
                showAdPopup = false
            },"Harf Hakkın Tükendi"
            , "3 Adet harf hakkı kazanmak için reklam izlemek ister misin?",
            false,isSoundOn,soundPool,soundId,
            onRewardEarned = {
                // Ödül alındığında hintCount'u güncelle
                coroutineScope.launch {
                    hintCount.intValue = userManager.getHintBonus()
                    delay(500)
                    hintCount.intValue = userManager.getHintBonus()
                }

            },
            isLoading = isLoadingState.value,
            setIsLoading = { isLoadingState.value = it }
        )
    }


        Surface(modifier = Modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Arkaplan resmi
            Image(
                painter = painterResource(R.drawable.levelbackground2),
                contentDescription = "background",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .alpha(0.9f)
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
                val userLevel = currentLevel
                val currentWord = words.find { it.id == userLevel }

                if (currentWord == null) {
                    // Kelime bulunamadıysa hata mesajı göster
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Seviye $userLevel bulunamadı. Lütfen daha sonra tekrar deneyin.",
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

                    // Harf ipucu sistemi için state'ler
                    var lockedPositions by remember { mutableStateOf(setOf<Int>()) }
                    var availablePositions by remember { mutableStateOf((0 until letterCount).toList()) }

                    var isWrongAnswer by remember { mutableStateOf(false) }
                    var isCorrectAnswer by remember { mutableStateOf(false) }

                    var boxScale by remember { mutableFloatStateOf(1f) }

                    var rotationAngle by remember { mutableFloatStateOf(0f) }
                    val animatedRotation by animateFloatAsState(
                        targetValue = rotationAngle,
                        animationSpec = tween(durationMillis = 300, easing = LinearEasing),
                        label = ""
                    )

                    // Klavye girişini dinle ve kutulara yerleştir
                    LaunchedEffect(currentInput) {
                        if (currentInput.length <= letterCount) {
                            val newList = inputLetters.toMutableList()

                            // Önce kilitli olmayan pozisyonları temizle
                            for (i in newList.indices) {
                                if (!lockedPositions.contains(i)) {
                                    newList[i] = ""
                                }
                            }

                            // Yeni girilen harfleri kilitli olmayan pozisyonlara yerleştir
                            var inputIndex = 0
                            for (i in newList.indices) {
                                if (!lockedPositions.contains(i) && inputIndex < currentInput.length) {
                                    newList[i] = currentInput[inputIndex].toString()
                                    inputIndex++
                                }
                            }

                            inputLetters = newList
                        }
                    }

                    // Yanlış cevap animasyonu için
                    LaunchedEffect(isWrongAnswer) {
                        if (isWrongAnswer) {
                            boxScale = 1.05f
                            rotationAngle = 45f
                            delay(150)
                            rotationAngle = -45f
                            delay(150)
                            rotationAngle = 0f

                            delay(400) // 400ms bekle
                            boxScale = 1f
                            isWrongAnswer = false
                        }
                    }

                    // Doğru cevap animasyonu için
                    LaunchedEffect(isCorrectAnswer) {
                        if (isCorrectAnswer) {
                            boxScale = 1.05f
                            rotationAngle = 360f
                            delay(700)

                            delay(1000)
                            boxScale = 1f
                            delay(500)

                            // Sadece ResultScreen'e yönlendir
                            navController.navigate("ResultScreen/$userLevel") {
                                popUpTo("LevelScreen/$userLevel") { inclusive = true }
                            }
                            isCorrectAnswer = false
                        }
                    }

                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.CenterHorizontally
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
                                text = "Level $userLevel",
                                color = colorResource(R.color.meaningBoxText),
                                fontSize = 38.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // Ana sayfa ikonu
                            IconButton(
                                onClick = {
                                    if (isSoundOn) {
                                        soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
                                    }
                                    showExitDialog = true
                                },
                                enabled = !isCorrectAnswer,
                                modifier = Modifier.size(55.dp)
                            ) {
                                Image(
                                    painter = painterResource(id = R.drawable.homepageicon2),
                                    contentDescription = "Ana Sayfa",
                                    modifier = Modifier.size(55.dp)
                                )
                            }
                        }

                        // Body Row with letter boxes
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            // Kutular
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(
                                    10.dp,
                                    Alignment.CenterHorizontally
                                ),
                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                maxItemsInEachRow = Int.MAX_VALUE
                            ) {
                                repeat(letterCount) { index ->
                                    Box(
                                        modifier = Modifier
                                            .size(50.dp)
                                            .scale(boxScale)
                                            .rotate(animatedRotation)
                                            .background(
                                                color = if (lockedPositions.contains(index))
                                                    colorResource(R.color.LevelLockedBox) // Kilitli kutular için farklı renk
                                                else
                                                    colorResource(R.color.answerbox),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .border(
                                                width = 3.dp,
                                                color = if (lockedPositions.contains(index))
                                                    colorResource(R.color.LevelLockedBoxBorder)// Kilitli kutular için yeşil border
                                                else
                                                    colorResource(R.color.answerboxBorder),
                                                shape = RoundedCornerShape(8.dp)
                                            )
                                            .padding(8.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = inputLetters.getOrElse(index) { "" }.lowercase(),
                                            style = TextStyle(
                                                fontSize = 28.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                textAlign = TextAlign.Center
                                            )
                                        )
                                    }
                                }
                            }

                            // Metin giriş kutusu - kutuların üzerinde
                            BasicTextField(
                                value = currentInput,
                                onValueChange = { newValue ->
                                    // Sadece harflere izin ver ve harf dışındaki karakterleri filtrele
                                    val filteredInput = newValue.filter { it.isLetter() }

                                    // Kilitli olmayan pozisyon sayısını hesapla
                                    val availableSlots = letterCount - lockedPositions.size

                                    if (filteredInput.length <= availableSlots) {
                                        // Yeni girişi küçük harfe çevir
                                        val lowercaseInput = filteredInput.lowercase()
                                        currentInput = lowercaseInput
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .fillMaxHeight(0.12f)
                                    .focusRequester(focusRequester)
                                    .focusTarget(),
                                cursorBrush = SolidColor(Color.Transparent),
                                textStyle = TextStyle(
                                    fontSize = 1.sp,
                                    color = Color.Transparent
                                )
                            )
                        }

                        // İpucu / Kelime anlamı göster
                        Row(
                            modifier = Modifier
                                .fillMaxWidth(0.96f)
                                .background(
                                    colorResource(R.color.meaningBox),
                                    RoundedCornerShape(12.dp)
                                )
                                .border(
                                    4.dp,
                                    colorResource(R.color.meaningBoxBorder),
                                    RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                if (!isCorrectAnswer) {
                                    Text(
                                        text = "İPUCU",
                                        fontSize = 26.sp,
                                        color = colorResource(R.color.meaningBoxText),
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(bottom = 4.dp)
                                    )
                                    Text(
                                        text = currentWord.meaning,
                                        fontSize = 24.sp,
                                        color = colorResource(R.color.meaningBoxText),
                                        fontWeight = FontWeight.SemiBold,
                                        textAlign = TextAlign.Center
                                    )
                                } else {
                                    LottieAnimation(
                                        composition = composition,
                                        progress = { progress },
                                        modifier = Modifier
                                            .scale(scale.value)
                                            .fillMaxWidth()
                                            .fillMaxWidth(0.5f)
                                    )
                                }
                            }
                        }

                        // Butonlar
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center
                        ) {

                            Box( modifier = Modifier
                                .fillMaxWidth(), contentAlignment = Alignment.Center){
                                // Onayla butonu
                                Button(
                                    onClick = {
                                        // Tüm kutulardaki harfleri birleştir
                                        val userInput = inputLetters.joinToString("")

                                        if (userInput.lowercase() == currentWord.word.lowercase()) {
                                            // Doğru cevap - animasyonu başlat
                                            if (isSoundOn)
                                                victorySound.start()
                                            startAnimation = true
                                            isCorrectAnswer = true
                                        } else {
                                            // Yanlış cevap - animasyonu başlat
                                            if (isSoundOn)
                                                wrongSound.start()
                                            isWrongAnswer = true
                                        }
                                    },
                                    enabled = !isCorrectAnswer,
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = colorResource(R.color.LevelButton)
                                    ),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .width(120.dp)
                                        .height(60.dp)
                                        .padding(top = 16.dp)
                                ) {
                                    Text(
                                        text = "Onayla",
                                        color = Color.White,
                                        fontSize = 22.sp
                                    )
                                }
                                Box(modifier = Modifier
                                    .size(90.dp)
                                    .align(Alignment.TopEnd)
                                    .clickable(
                                        indication = null,
                                        interactionSource = remember { MutableInteractionSource() },
                                        enabled = availablePositions.isNotEmpty()
                                    ) {

                                        if (hintCount.intValue != 0) {
                                            if(isSoundOn)
                                               bonusSound.play(bonusSoundId, 1f, 1f, 1, 0, 1f)
                                            // Eğer daha harf verilecek pozisyon varsa
                                            userManager.useHint()
                                            if (availablePositions.isNotEmpty()) {
                                                // Rastgele bir pozisyon seç
                                                val randomIndex = availablePositions.random()

                                                // O pozisyondaki harfi yerleştir
                                                val newInputLetters = inputLetters.toMutableList()
                                                newInputLetters[randomIndex] =
                                                    currentWord.word[randomIndex]
                                                        .toString()
                                                        .lowercase()
                                                inputLetters = newInputLetters

                                                // Bu pozisyonu kilitle
                                                lockedPositions = lockedPositions + randomIndex

                                                // Bu pozisyonu mevcut pozisyonlardan kaldır
                                                availablePositions =
                                                    availablePositions.filter { it != randomIndex }

                                                // Kullanıcının mevcut girişini güncelle (kilitli harfleri hariç tut)
                                                val newCurrentInput = StringBuilder()
                                                for (i in inputLetters.indices) {
                                                    if (!lockedPositions.contains(i) && inputLetters[i].isNotEmpty()) {
                                                        newCurrentInput.append(inputLetters[i])
                                                    }
                                                }
                                                currentInput = newCurrentInput.toString()
                                            }

                                            hintCount.intValue = userManager.getHintBonus()
                                        }else{
                                            if (isSoundOn)
                                                soundPool.play(soundId,1f,1f,1,0,1f)
                                            //reklam ver

                                            showAdPopup = true
                                        }
                                    }
                                ) {
                                    Column(
                                        modifier = Modifier.fillMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally, // içeriği ortala
                                        verticalArrangement = Arrangement.Top // yukarıdan aşağı yerleştir
                                    ) {
                                        Image(
                                            painter = painterResource(
                                                if (hintCount.intValue == 0) R.drawable.lightblubad else R.drawable.lightblub
                                            ),
                                            contentDescription = "hint",
                                            modifier = Modifier
                                                .size(60.dp) // Gerekirse boyutla
                                        )
                                        Text(
                                            text = hintCount.intValue.toString(),
                                            fontSize = 24.sp // 30.sp çok büyük olabilir bu alanda, istersen değiştirebilirsin
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}