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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.material3.OutlinedTextFieldDefaults.colors
import androidx.compose.ui.text.TextStyle
import com.keremsen.wordmaster.viewmodel.MusicPlayerViewModel

@Composable
fun ProfileScreen(navController: NavController, settingsViewModel: SettingsViewModel,authViewModel: AuthViewModel,musicPlayerViewModel: MusicPlayerViewModel) {
    val isSoundOn = settingsViewModel.isSoundOn
    val isMusicOn = musicPlayerViewModel.isMusicOn.value
    val isMusicPause = musicPlayerViewModel.isMusicPause.value

    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences("ProfilePrefs", Context.MODE_PRIVATE) }

    val coroutineScope = rememberCoroutineScope()
    val mediaPlayer = remember { MediaPlayer.create(context, R.raw.clikedsound) }

    val currentUser by authViewModel.currentUser.observeAsState()
    val authError by authViewModel.error.observeAsState()
    val signInIntent by authViewModel.signInIntent.observeAsState()

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

            } catch (e: Exception) {
                authViewModel.setError(e.message ?: "Google girişi başarısız")
            }
        }
    }
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer.release()
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
            mediaPlayer.release()
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
                            if (isSoundOn)
                                mediaPlayer.start()
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
                    .fillMaxHeight(0.3f),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .clickable {
                            if (isSoundOn)
                                mediaPlayer.start()
                            isEditingName = true }
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
                                if (isSoundOn)
                                    mediaPlayer.start()
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
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Hoş geldin, ${currentUser?.displayName ?: "Kullanıcı"}",
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = currentUser?.email ?: "",
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { authViewModel.signOut(context) },
                        modifier = Modifier.padding(8.dp)
                    ) {
                        Text("Çıkış Yap")
                    }
                }
            } else {
                // Giriş yapılmamış durumda
                Box(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                        .background(
                            color = Color.White.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(24.dp)
                        )
                        .clickable {
                            if (isSoundOn)
                                mediaPlayer.start()
                            if (isMusicOn)
                                musicPlayerViewModel.pauseMusic()
                            authViewModel.prepareGoogleSignIn(context)
                            signInIntent?.let {
                                signInLauncher.launch(it)
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        modifier = Modifier
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.googleicon),
                            contentDescription = "google",
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                        Text(
                            text = "Google ile oturum açınız",
                            fontSize = 20.sp,
                            color = Color.White
                        )
                    }
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