package com.keremsen.wordmaster.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.keremsen.wordmaster.R


@Composable
fun MainScreen() {
    @Composable
    fun YourScreen() {
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            // Background image
            Image(
                painter = painterResource(id = R.drawable.background),
                contentDescription = null,
                contentScale = ContentScale.Crop, // Görsel ekranı doldursun
                modifier = Modifier.fillMaxSize()
            )


        }
    }


}