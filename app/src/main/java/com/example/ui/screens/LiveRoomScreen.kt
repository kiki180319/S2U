package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.LiveChatMessage
import com.example.data.database.LiveStreamSession
import com.example.ui.components.UserAvatarBadge
import com.example.ui.components.YouTubePlayer
import com.example.ui.theme.*
import com.example.ui.viewmodel.HeartsViewModel
import kotlinx.coroutines.launch
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LiveRoomScreen(
    streamId: String,
    viewModel: HeartsViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()
    val session by viewModel.currentLiveSession.collectAsStateWithLifecycle()
    val chatMessages by viewModel.currentLiveChatMessages.collectAsStateWithLifecycle()
    val currentUserProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    
    // Lifecycle-aware Presence Counter to prevent phantom viewers
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, streamId) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START, Lifecycle.Event.ON_RESUME -> {
                    viewModel.enterLiveRoom(streamId)
                }
                Lifecycle.Event.ON_STOP, Lifecycle.Event.ON_PAUSE -> {
                    viewModel.leaveLiveRoom(streamId)
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            viewModel.leaveLiveRoom(streamId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = session?.title ?: "Live Streaming Room",
                            color = PremiumWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = if (session?.isActive == true) "LIVE NOW" else "SIARAN TIDAK AKTIF",
                            color = if (session?.isActive == true) StatusGreen else PremiumLightGray,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = PremiumWhite
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PremiumBlack
                )
            )
        },
        containerColor = PremiumBlack,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            // 1. TOP PORTION: Streaming Player
            val videoUrl = session?.streamUrl ?: ""
            val videoId = remember(videoUrl) { extractYoutubeVideoId(videoUrl) }
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                if (videoUrl.isNotBlank()) {
                    HeartsStreamPlayer(
                        videoUrl = videoUrl,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Fallback visual banner when URL is empty
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Surface(
                            color = Color.Red,
                            shape = RoundedCornerShape(4.dp),
                            modifier = Modifier.padding(bottom = 8.dp)
                        ) {
                            Text(
                                "PREMIUM STREAM",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Text(
                            text = session?.title ?: "Hearts2Hearts Special Live Party",
                            color = PremiumWhite,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Stream URL: Kosong",
                            color = PremiumLightGray,
                            fontSize = 12.sp
                        )
                    }
                }

                // Presence Stats Overlaid on Video Player Corner
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Viewers",
                        tint = HeartsPink,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${session?.viewerCount ?: 0}",
                        color = PremiumWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "Chat Participants",
                        tint = HeartsPink,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${session?.chatParticipantCount ?: 0}",
                        color = PremiumWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 2. BOTTOM PORTION: Real-Time Live Chat Screen
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(PremiumDarkGray)
            ) {
                Surface(
                    color = PremiumBlack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "LIVE CHAT ROOM",
                        color = HeartsPink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }

                // Chat Message List
                val listState = rememberLazyListState()
                LaunchedEffect(chatMessages.size) {
                    if (chatMessages.isNotEmpty()) {
                        listState.animateScrollToItem(chatMessages.size - 1)
                    }
                }

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(chatMessages, key = { it.id }) { message ->
                        LiveChatMessageRow(message = message)
                    }
                }

                // Chat Input Bar
                LiveRoomChatInputBar(
                    onSendMessage = { text ->
                        viewModel.sendLiveChat(text)
                    }
                )
            }
        }
    }
}

@Composable
fun LiveChatMessageRow(message: LiveChatMessage) {
    val isHost = message.senderRole == "admin" || message.senderName == "Carmen"
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        UserAvatarBadge(
            userName = message.senderName,
            size = 32.dp,
            fontSize = 14.sp
        )
        
        Spacer(modifier = Modifier.width(8.dp))
        
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = message.senderName,
                    color = if (isHost) Color.Red else HeartsPink,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                if (isHost) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Surface(
                        color = Color.Red,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "HOST",
                            color = Color.White,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(2.dp))
            Surface(
                color = PremiumMediumGray.copy(alpha = 0.5f),
                shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp, topEnd = 8.dp)
            ) {
                Text(
                    text = message.text,
                    color = PremiumWhite,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}

@Composable
fun LiveRoomChatInputBar(
    onSendMessage: (String) -> Unit
) {
    var text by remember { mutableStateOf("") }
    val keyboardController = LocalSoftwareKeyboardController.current

    Surface(
        color = PremiumBlack,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                placeholder = { Text("Kirim pesan obrolan...", color = PremiumLightGray, fontSize = 14.sp) },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = PremiumWhite,
                    unfocusedTextColor = PremiumWhite,
                    focusedBorderColor = HeartsPink,
                    unfocusedBorderColor = PremiumMediumGray,
                    focusedContainerColor = PremiumDarkGray,
                    unfocusedContainerColor = PremiumDarkGray
                ),
                shape = RoundedCornerShape(24.dp),
                maxLines = 3,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (text.isNotBlank()) {
                        onSendMessage(text)
                        text = ""
                    }
                })
            )

            IconButton(
                onClick = {
                    if (text.isNotBlank()) {
                        onSendMessage(text)
                        text = ""
                        keyboardController?.hide()
                    }
                },
                modifier = Modifier
                    .size(44.dp)
                    .background(HeartsPink, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Send,
                    contentDescription = "Send Message",
                    tint = PremiumWhite,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

fun extractYoutubeVideoId(url: String): String {
    return when {
        url.contains("v=") -> url.substringAfter("v=").substringBefore("&")
        url.contains("youtu.be/") -> url.substringAfter("youtu.be/").substringBefore("?")
        else -> url
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun HeartsStreamPlayer(
    videoUrl: String,
    modifier: Modifier = Modifier
) {
    val isYoutube = videoUrl.contains("youtube.com", ignoreCase = true) || videoUrl.contains("youtu.be", ignoreCase = true)
    val isHls = videoUrl.contains(".m3u8", ignoreCase = true) || videoUrl.contains("/hls/", ignoreCase = true)
    
    if (isYoutube) {
        val videoId = extractYoutubeVideoId(videoUrl)
        YouTubePlayer(
            youtubeVideoId = videoId,
            modifier = modifier
        )
    } else if (isHls) {
        val context = LocalContext.current
        val exoPlayer = remember(videoUrl) {
            ExoPlayer.Builder(context).build().apply {
                val mediaItem = MediaItem.fromUri(videoUrl)
                setMediaItem(mediaItem)
                prepare()
                playWhenReady = true
            }
        }
        
        DisposableEffect(exoPlayer) {
            onDispose {
                exoPlayer.release()
            }
        }
        
        AndroidView(
            factory = { ctx ->
                PlayerView(ctx).apply {
                    player = exoPlayer
                    useController = true
                }
            },
            modifier = modifier
        )
    } else {
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    settings.mediaPlaybackRequiresUserGesture = false
                    settings.domStorageEnabled = true
                    settings.useWideViewPort = true
                    settings.loadWithOverviewMode = true
                    
                    val displayUrl = if (videoUrl.contains("youtube.com/watch?v=")) {
                        val videoId = videoUrl.substringAfter("v=").substringBefore("&")
                        "https://www.youtube.com/embed/$videoId?autoplay=1"
                    } else if (videoUrl.contains("youtu.be/")) {
                        val videoId = videoUrl.substringAfter("youtu.be/").substringBefore("?")
                        "https://www.youtube.com/embed/$videoId?autoplay=1"
                    } else {
                        videoUrl
                    }
                    
                    loadUrl(displayUrl)
                }
            },
            modifier = modifier
        )
    }
}
