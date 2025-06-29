package com.keremsen.wordmaster.view

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.keremsen.wordmaster.R
import com.keremsen.wordmaster.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.ImeAction
import android.content.Context
import android.media.SoundPool
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextFieldDefaults.colors
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.core.content.ContextCompat
import com.keremsen.wordmaster.utils.WordListDialog
import com.keremsen.wordmaster.viewmodel.UserManagerViewModel
import com.keremsen.wordmaster.viewmodel.WordViewModel

@Composable
fun ProfileScreen(navController: NavController, settingsViewModel: SettingsViewModel,wordViewModel: WordViewModel) {
    val isSoundOn = settingsViewModel.isSoundOn
    val words by wordViewModel.kelimeler.collectAsState(initial = emptyList())
    val context = LocalContext.current
    val userManagerSharedPref = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
    val userManager = remember { UserManagerViewModel(userManagerSharedPref)  }

    var showWordListDialog by remember { mutableStateOf(false) }

    val level = userManager.getLevel()

    val coroutineScope = rememberCoroutineScope()
    val soundPool = remember {
        SoundPool.Builder()
            .setMaxStreams(1)
            .build()
    }
    val soundId = remember {
        soundPool.load(context, R.raw.clikedsound, 1)
    }

    // Animasyon durumu
    var visible by remember { mutableStateOf(false) }

    val offsetY = animateFloatAsState(
        targetValue = if (visible) 0f else 1000f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing), label = ""
    ).value

    val alpha = animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 300), label = ""
    ).value

    var isEditingName by remember { mutableStateOf(false) }
    var profileName by remember {
        mutableStateOf(userManager.getName())
    }

    var isAnimating by remember { mutableStateOf(false) }
    var profileState by remember { mutableStateOf(false) }

    // Çoklu tıklama önleme için debounce state'i
    var isClickEnabled by remember { mutableStateOf(true) }

    fun handleBack() {
        if (!isClickEnabled || isAnimating || profileState) return

        isClickEnabled = false
        isAnimating = true
        coroutineScope.launch {
            visible = false
            delay(200)
            navController.popBackStack()
            launch {
                delay(300)
                isAnimating = false
                isClickEnabled = true
            }
        }
    }

    fun handleCancel() {
        if (!isClickEnabled || isAnimating || profileState) return

        isClickEnabled = false
        coroutineScope.launch {
            if (isSoundOn) {
                soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
            }
            isAnimating = true
            visible = false
            delay(200)
            navController.navigate("MainScreen") {
                launch {
                    delay(400)
                    isAnimating = false
                    isClickEnabled = true
                }
                popUpTo("ProfileScreen") { inclusive = true }
            }
        }
    }

    fun handleProfileClick() {
        if (!isClickEnabled || isAnimating || profileState) return

        isClickEnabled = false
        if (isSoundOn) {
            soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
        }
        profileState = true
        isEditingName = true

        // Kısa bir süre sonra tıklama kontrolünü tekrar aktifleştir
        coroutineScope.launch {
            delay(300)
            isClickEnabled = true
        }
    }

    BackHandler {
        handleBack()
    }

    DisposableEffect(Unit) {
        visible = true
        onDispose {
            visible = false
        }
    }

    Surface(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "Arka Plan Resmi",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 43.dp)
                    .height(65.dp)
                    .graphicsLayer {
                        translationY = offsetY
                        this.alpha = alpha
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //cancel iconbutton
                IconButton(
                    onClick = { handleCancel() },
                    enabled = isClickEnabled && !isAnimating && !profileState,
                    modifier = Modifier.size(55.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cancel),
                        contentDescription = "cancel",
                        contentScale = ContentScale.Fit,
                        modifier = Modifier.size(55.dp)
                    )
                }
            }
            Spacer(modifier = Modifier.height(5.dp))

            // Profil kısmı
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.3f)
                    .graphicsLayer {
                        translationY = offsetY
                        this.alpha = alpha
                    },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clickable(
                            enabled = isClickEnabled && !isAnimating && !profileState,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            handleProfileClick()
                        }
                ) {
                    Image(
                        painter = painterResource(R.drawable.accounticon),
                        contentDescription = "accountprofile",
                    )
                }
                //isim düzenleme
                if (isEditingName) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = profileName,
                            onValueChange = { if (it.length <= 20) {
                                profileName = it
                            }},
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                                onDone = {
                                    userManager.saveName(profileName)
                                    isEditingName = false
                                    profileState = false
                                    // Kaydetme işlemi sonrası tıklama kontrolünü aktifleştir
                                    coroutineScope.launch {
                                        delay(100)
                                        isClickEnabled = true
                                    }
                                }
                            ),
                            colors = colors(
                                focusedBorderColor = Color.White,
                                unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            textStyle = TextStyle(
                                fontSize = 22.sp,
                                fontWeight = Bold
                            )
                        )
                        //kaydet button
                        Button(
                            onClick = {
                                if (isSoundOn) {
                                    soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
                                }
                                userManager.saveName(profileName)
                                profileState = false
                                isEditingName = false
                                // Kaydetme işlemi sonrası tıklama kontrolünü aktifleştir
                                coroutineScope.launch {
                                    delay(100)
                                    isClickEnabled = true
                                }
                            },
                            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4CAF50)
                            )
                        ) {
                            Text(
                                text = "Kaydet",
                                color = Color.White,
                                fontSize = 16.sp
                            )
                        }
                    }
                } else {
                        Text(
                            text = profileName,
                            fontWeight = Bold,
                            fontSize = 22.sp,
                            color = Color.White,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                }
            }
            //level tablosu
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.7f)
                    .graphicsLayer {
                        translationY = offsetY
                        this.alpha = alpha
                    },
                horizontalArrangement = Arrangement.Center
            ){
                Box(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(16.dp)) // Köşeleri oval yapar
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(ContextCompat.getColor(context, R.color.profile2)),
                                    Color(ContextCompat.getColor(context, R.color.profile)),
                                    Color(ContextCompat.getColor(context, R.color.profile3))
                                ) // Mor-mavi gradient
                            )
                        )
                ) {
                    // İçerik buraya
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Sol taraf
                        Column(
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.fillMaxWidth(0.46f)
                        ) {
                            Text(
                                text = "Yolculuk",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 20.sp,
                                fontWeight = Bold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Level $level",
                                color = Color.White.copy(alpha = 0.9f),
                                fontSize = 18.sp
                            )
                        }

                        // Ortadaki ince beyaz çizgi
                        Box(
                            modifier = Modifier
                                .width(1.dp)
                                .fillMaxHeight(0.85f)
                                .background(Color.White.copy(alpha = 0.7f))
                        )

                        // Sağ taraf
                        Row(
                            modifier = Modifier
                                .fillMaxHeight(0.8f)
                                .clickable(
                                    enabled =isClickEnabled && !isAnimating && !profileState,
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() }
                                ) {
                                    showWordListDialog = true
                                },
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Kelime Ağacım",
                                fontSize = 20.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                modifier = Modifier.padding(8.dp)
                            )
                        }

                        WordListDialog(
                            isVisible = showWordListDialog,
                            onDismiss = { showWordListDialog = false },
                            wordList = words,
                            level = level
                        )
                    }
                }


            }

        }
    }
}