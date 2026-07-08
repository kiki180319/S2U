package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.border
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.ui.viewmodel.ChatMessage
import com.example.ui.viewmodel.HeartsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AiChatScreen(viewModel: HeartsViewModel) {
    val chatMessages by viewModel.chatMessages.collectAsStateWithLifecycle()
    val chatLoading by viewModel.chatLoading.collectAsStateWithLifecycle()
    
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    
    // Auto-scroll to bottom when messages change
    LaunchedEffect(chatMessages.size, chatLoading) {
        if (chatMessages.isNotEmpty()) {
            val targetIndex = if (chatLoading) chatMessages.size else chatMessages.size - 1
            if (targetIndex >= 0) {
                listState.animateScrollToItem(targetIndex)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PremiumBlack)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 24.dp)
        ) {
            itemsIndexed(chatMessages) { index, message ->
                val isUser = message.sender == "user"
                val isLastInGroup = if (index < chatMessages.size - 1) {
                    chatMessages[index + 1].sender != message.sender
                } else {
                    true
                }
                val isFirstInGroup = if (index > 0) {
                    chatMessages[index - 1].sender != message.sender
                } else {
                    true
                }
                
                ChatBubble(
                    message = message,
                    isLastInGroup = isLastInGroup,
                    isFirstInGroup = isFirstInGroup
                )
            }
            
            if (chatLoading) {
                item {
                    TypingIndicatorBubble()
                }
            }
        }
        
        ChatInputBar(
            onSend = { text ->
                viewModel.sendMessageToChatbot(text)
            }
        )
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    isLastInGroup: Boolean,
    isFirstInGroup: Boolean
) {
    val isUser = message.sender == "user"
    var showTimestamp by remember { mutableStateOf(false) }
    val timeString = remember { SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = if (isFirstInGroup) 16.dp else 2.dp),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        AnimatedVisibility(
            visible = showTimestamp,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Text(
                text = timeString,
                color = PremiumLightGray,
                fontSize = 11.sp,
                modifier = Modifier.padding(bottom = 4.dp, top = 8.dp)
            )
        }
        
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (!isUser) {
                if (isLastInGroup) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(HeartsPink),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("AI", color = PremiumWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Spacer(modifier = Modifier.width(28.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
            }
            
            // Corner radius logic for iMessage feel
            val shape = if (isUser) {
                RoundedCornerShape(
                    topStart = 20.dp,
                    topEnd = if (isFirstInGroup) 20.dp else 4.dp,
                    bottomStart = 20.dp,
                    bottomEnd = if (isLastInGroup) 4.dp else 4.dp
                )
            } else {
                RoundedCornerShape(
                    topStart = if (isFirstInGroup) 20.dp else 4.dp,
                    topEnd = 20.dp,
                    bottomStart = if (isLastInGroup) 4.dp else 4.dp,
                    bottomEnd = 20.dp
                )
            }
            
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(shape)
                    .background(if (isUser) HeartsPink else PremiumMediumGray)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { showTimestamp = !showTimestamp }
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message.text,
                    color = PremiumWhite,
                    fontSize = 16.sp,
                    lineHeight = 22.sp
                )
            }
        }
    }
}

@Composable
fun TypingIndicatorBubble() {
    Row(
        verticalAlignment = Alignment.Bottom,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(HeartsPink),
            contentAlignment = Alignment.Center
        ) {
            Text("AI", color = PremiumWhite, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.width(8.dp))
        
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 20.dp,
                        topEnd = 20.dp,
                        bottomStart = 4.dp,
                        bottomEnd = 20.dp
                    )
                )
                .background(PremiumMediumGray)
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            TypingIndicator()
        }
    }
}

@Composable
fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    
    val dot1 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )
    
    val dot2 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing, delayMillis = 150),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )
    
    val dot3 by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(400, easing = LinearEasing, delayMillis = 300),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Dot(dot1)
        Dot(dot2)
        Dot(dot3)
    }
}

@Composable
fun Dot(alpha: Float) {
    Box(
        modifier = Modifier
            .size(6.dp)
            .clip(CircleShape)
            .background(PremiumWhite.copy(alpha = alpha))
    )
}

@Composable
fun ChatInputBar(onSend: (String) -> Unit) {
    var text by remember { mutableStateOf("") }

    Surface(
        color = PremiumBlack,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(24.dp))
                    .background(PremiumMediumGray)
                    .border(1.dp, PremiumDarkGray, RoundedCornerShape(24.dp))
                    .padding(horizontal = 16.dp, vertical = 10.dp)
            ) {
                if (text.isEmpty()) {
                    Text("Message AI Companion...", color = PremiumLightGray, fontSize = 16.sp)
                }
                BasicTextField(
                    value = text,
                    onValueChange = { text = it },
                    textStyle = TextStyle(color = PremiumWhite, fontSize = 16.sp),
                    cursorBrush = SolidColor(HeartsPink),
                    modifier = Modifier.fillMaxWidth().heightIn(min = 24.dp, max = 120.dp)
                )
            }
            
            AnimatedVisibility(
                visible = text.isNotBlank(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .padding(start = 12.dp, bottom = 4.dp)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(HeartsPink)
                        .clickable {
                            if (text.isNotBlank()) {
                                onSend(text)
                                text = ""
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Send",
                        tint = PremiumWhite,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
