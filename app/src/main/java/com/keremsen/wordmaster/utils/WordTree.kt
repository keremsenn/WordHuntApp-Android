package com.keremsen.wordmaster.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.keremsen.wordmaster.model.Word
import kotlinx.coroutines.delay

// Debounce click handler
@Composable
fun rememberDebounceClickable(
    delayMillis: Long = 500L,
    onClick: () -> Unit
): () -> Unit {
    var isClickable by remember { mutableStateOf(true) }

    val clickHandler = remember {
        {
            if (isClickable) {
                isClickable = false
                onClick()
            }
        }
    }

    // Reset clickable state after delay
    LaunchedEffect(isClickable) {
        if (!isClickable) {
            delay(delayMillis)
            isClickable = true
        }
    }

    return clickHandler
}

@Composable
fun WordListDialog(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    wordList: List<Word>,
    level: Int
) {
    var showHint by remember { mutableStateOf(false) }
    var selectedWord by remember { mutableStateOf<Word?>(null) }
    var isProcessing by remember { mutableStateOf(false) }

    // Level'e göre kelimeleri filtrele
    val filteredWords = wordList.filter { it.id < level }

    // Debounced dismiss function
    val debouncedOnDismiss = rememberDebounceClickable(300L) {
        if (!isProcessing) {
            onDismiss()
        }
    }

    if (isVisible) {
        Dialog(
            onDismissRequest = debouncedOnDismiss,
            properties = DialogProperties(
                dismissOnBackPress = true,
                dismissOnClickOutside = true,
                usePlatformDefaultWidth = false
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.8f),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF2C4459)
                    ),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Header
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = debouncedOnDismiss,
                                enabled = !isProcessing
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ArrowBack,
                                    contentDescription = "Geri",
                                    tint = if (isProcessing) Color.Gray else Color.White
                                )
                            }

                            Text(
                                text = "Kelime Ağacım",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.Center
                            )
                        }

                        // Kelime listesi
                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(filteredWords) { word ->
                                WordItem(
                                    word = word,
                                    isEnabled = !isProcessing,
                                    onWordClick = {
                                        if (!isProcessing) {
                                            isProcessing = true
                                            selectedWord = word
                                            showHint = true
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // İpucu Dialog'u
    if (showHint && selectedWord != null) {
        HintDialog(
            word = selectedWord!!,
            onDismiss = {
                showHint = false
                selectedWord = null
                isProcessing = false
            }
        )
    }
}

@Composable
fun WordItem(
    word: Word,
    isEnabled: Boolean = true,
    onWordClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    var isClicked by remember { mutableStateOf(false) }

    // Debounced click handler
    val debouncedClick = rememberDebounceClickable(600L) {
        if (!isClicked && isEnabled) {
            isClicked = true
            onWordClick()
        }
    }

    // Reset click state after delay
    LaunchedEffect(isClicked) {
        if (isClicked) {
            delay(1000L)
            isClicked = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = isEnabled && !isClicked,
                onClick = debouncedClick
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isEnabled && !isClicked)
                Color(0xFF497480)
            else
                Color(0xFF497480).copy(alpha = 0.6f)
        ),
        elevation = CardDefaults.cardElevation(if (isEnabled && !isClicked) 4.dp else 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = word.word,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = if (isEnabled && !isClicked) Color.White else Color.White.copy(alpha = 0.6f)
                )

                Text(
                    text = "level: ${word.id}",
                    fontSize = 12.sp,
                    color = if (isEnabled && !isClicked)
                        Color(0xFFFDFDFD)
                    else
                        Color(0xFFFDFDFD).copy(alpha = 0.6f)
                )
            }

            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = "İpucu",
                tint = if (isEnabled && !isClicked)
                    Color(0xFFA3BDD3)
                else
                    Color(0xFFA3BDD3).copy(alpha = 0.6f),
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun HintDialog(
    word: Word,
    onDismiss: () -> Unit
) {
    var isDismissing by remember { mutableStateOf(false) }

    // Debounced dismiss function
    val debouncedDismiss = rememberDebounceClickable(300L) {
        if (!isDismissing) {
            isDismissing = true
            onDismiss()
        }
    }

    Dialog(
        onDismissRequest = debouncedDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color(0xFF2C4459)
            )
        ) {
            Column(
                modifier = Modifier.padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = debouncedDismiss,
                        enabled = !isDismissing
                    ) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Kapat",
                            tint = if (isDismissing) Color.Gray else Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = word.word,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = word.meaning,
                    fontSize = 16.sp,
                    color = Color.White.copy(alpha = 0.9f),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = debouncedDismiss,
                    enabled = !isDismissing,
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDismissing)
                            Color(0xFFA5BED5).copy(alpha = 0.6f)
                        else
                            Color(0xFFA5BED5),
                        disabledContainerColor = Color(0xFFA5BED5).copy(alpha = 0.6f)
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (isDismissing) "Kapatılıyor..." else "Anladım",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}