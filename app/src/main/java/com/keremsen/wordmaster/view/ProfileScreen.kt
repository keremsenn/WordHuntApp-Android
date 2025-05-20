package com.keremsen.wordmaster.view


import android.media.MediaPlayer
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.GoogleAuthProvider
import com.keremsen.wordmaster.R
import com.keremsen.wordmaster.viewmodel.AuthViewModel
import com.keremsen.wordmaster.viewmodel.SettingsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.ui.text.input.ImeAction
import android.content.Context
import android.media.SoundPool
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextFieldDefaults.colors
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import com.keremsen.wordmaster.utils.CustomIconButton
import com.keremsen.wordmaster.viewmodel.MusicPlayerViewModel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import com.keremsen.wordmaster.viewmodel.LevelManagerViewModel

@Composable
fun ProfileScreen(navController: NavController, settingsViewModel: SettingsViewModel,authViewModel: AuthViewModel,musicPlayerViewModel: MusicPlayerViewModel) {
    val isSoundOn = settingsViewModel.isSoundOn
    val isMusicOn = musicPlayerViewModel.isMusicOn.value
    val isMusicPause = musicPlayerViewModel.isMusicPause.value

    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE) }
    val levelManagerSharedPref = context.getSharedPreferences("UserPreferences", Context.MODE_PRIVATE)
    val levelManager = remember { LevelManagerViewModel(levelManagerSharedPref)  }

    val coroutineScope = rememberCoroutineScope()
    val soundPool = remember {
        SoundPool.Builder()
            .setMaxStreams(1)
            .build()
    }
    val soundId = remember {
        soundPool.load(context, R.raw.clikedsound, 1)
    }

    val currentUser by authViewModel.currentUser.observeAsState()
    val authError by authViewModel.error.observeAsState()
    val signInIntent by authViewModel.signInIntent.observeAsState()

    // Oturum sonlandırma dialog state'i
    var showSignOutDialog by remember { mutableStateOf(false) }

    // Google Sign-In launcher
    val signInLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        coroutineScope.launch {
            try {
                if (isMusicPause){
                    musicPlayerViewModel.startMusic()
                }
                val account = GoogleSignIn.getSignedInAccountFromIntent(result.data).await()
                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                authViewModel.signInWithCredential(credential)
                navController.navigate("SplashScreen") {
                    popUpTo(0) { inclusive = true }
                }
            } catch (e: Exception) {
                authViewModel.setError(e.message ?: "Google girişi başarısız")
            }
        }
    }
    // Animasyon durumu
    var visible by remember { mutableStateOf(false) }
    val offsetY = animateFloatAsState(
        targetValue = if (visible) 0f else 1000f,
        animationSpec = tween(durationMillis = 300, easing = FastOutSlowInEasing)
    ).value

    val alpha = animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 300)
    ).value

    var isEditingName by remember { mutableStateOf(false) }
    var profileName by remember { 
        mutableStateOf(sharedPreferences.getString("userName", "Misafir4856451") ?: "Misafir4856451") 
    }



    fun saveProfileName(name: String) {
        sharedPreferences.edit().apply {
            putString("userName", name)
            apply()
        }
    }

    fun handleBack() {
        coroutineScope.launch {
            visible = false
            delay(200)
            navController.popBackStack()
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
                    .height(65.dp)
                    .graphicsLayer {
                        translationY = offsetY
                        this.alpha = alpha
                    },
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            if (isSoundOn) {
                                soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
                            }
                            visible = false
                            delay(200)
                            navController.navigate("MainScreen") {
                                popUpTo("ProfileScreen") { inclusive = true }
                            }
                        }
                    },
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
            //profil kısmı
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
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            if (isSoundOn) {
                                soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
                            }
                            isEditingName = true
                        }
                ) {
                    Image(
                        painter = painterResource(R.drawable.accounticon),
                        contentDescription = "accountprofile",
                    )
                }
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
                            onValueChange = { profileName = it },
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 8.dp),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = androidx.compose.foundation.text.KeyboardActions(
                                onDone = {
                                    saveProfileName(profileName)
                                    isEditingName = false
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
                        Button(
                            onClick = {
                                if (isSoundOn) {
                                    soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
                                }
                                saveProfileName(profileName)
                                isEditingName = false 
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

            // Kullanıcı durumuna göre arayüz
            if (currentUser != null) {
                // Giriş yapmış kullanıcı için
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .graphicsLayer {
                            translationY = offsetY
                            this.alpha = alpha
                        },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Oturum sonlandırma butonu
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Button(
                            onClick = {
                                if (isSoundOn) {
                                    soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
                                }
                                showSignOutDialog = true
                            },
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(16.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFE57373)
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = "Çıkış",
                                    tint = Color.White
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Oturumu Sonlandır",
                                    color = Color.White,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }
                    // Oturum sonlandırma onay dialogu
                    if (showSignOutDialog) {
                        AlertDialog(
                            onDismissRequest = { showSignOutDialog = false },
                            title = {
                                Text(
                                    text = "Oturumu Sonlandır",
                                    style = TextStyle(
                                        fontSize = 20.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.Black
                                    )
                                )
                            },
                            text = {
                                Column(
                                    modifier = Modifier.padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = "E-posta: ${currentUser?.email ?: ""}",
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            color = Color.Gray
                                        ),
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )
                                    Text(
                                        text = "Oturumu kapatmak istediğinizden emin misiniz?",
                                        style = TextStyle(
                                            fontSize = 16.sp,
                                            color = Color.Black
                                        )
                                    )
                                }
                            },
                            confirmButton = {
                                TextButton(
                                    onClick = {
                                        showSignOutDialog = false
                                        // Level bilgisini sıfırla
                                        levelManager.resetLevel()
                                        authViewModel.signOut(context)
                                        saveProfileName("Misafir4856451")
                                        navController.navigate("SplashScreen") {
                                            popUpTo(0) { inclusive = true }
                                        }
                                    }
                                ) {
                                    Text(
                                        text = "Evet, Çıkış Yap",
                                        color = Color(0xFFE57373)
                                    )
                                }
                            },
                            dismissButton = {
                                TextButton(
                                    onClick = { showSignOutDialog = false }
                                ) {
                                    Text(
                                        text = "İptal",
                                        color = Color.Gray
                                    )
                                }
                            },
                            containerColor = Color.White,
                            shape = RoundedCornerShape(16.dp)
                        )
                    }
                }
            } else {
                Box(  modifier = Modifier.fillMaxWidth().graphicsLayer {
                    translationY = offsetY
                    this.alpha = alpha
                },
                    contentAlignment = Alignment.Center) {
                    // Giriş yapılmamış durumda
                    CustomIconButton(
                        text = "Google ile oturum açınız",
                        icon = R.drawable.googleicon,
                        onClick = {
                            if (isSoundOn) {
                                soundPool.play(soundId, 1f, 1f, 1, 0, 1f)
                            }
                            if (isMusicOn)
                                musicPlayerViewModel.pauseMusic()
                            authViewModel.prepareGoogleSignIn(context)
                            signInIntent?.let {
                                signInLauncher.launch(it)
                            }
                        }
                    )
                }

            }
            // Hata mesajı gösterimi
            authError?.let { error ->
                Text(
                    text = error,
                    color = Color.Red,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}