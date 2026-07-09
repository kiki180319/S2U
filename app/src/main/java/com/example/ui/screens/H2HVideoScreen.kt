package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.data.api.H2HVideo
import com.example.ui.components.YouTubePlayer
import com.example.ui.theme.PremiumBlack
import com.example.ui.theme.PremiumDarkGray
import com.example.ui.theme.PremiumLightGray
import com.example.ui.theme.PremiumWhite
import com.example.ui.theme.HeartsPink
import com.example.ui.viewmodel.H2HVideoViewModel
import com.example.ui.viewmodel.VideoStatsState

@Composable
fun H2HVideoScreen(onBack: () -> Unit, videoViewModel: H2HVideoViewModel = viewModel()) {
    val videos by videoViewModel.videos.collectAsState()
    val isLoading by videoViewModel.isLoading.collectAsState()
    val error by videoViewModel.error.collectAsState()
    val context = LocalContext.current
    
    var selectedVideo by remember { mutableStateOf<H2HVideo?>(null) }
    var playerMode by remember { mutableStateOf("player") } // "player" or "dashboard"

    // Intercept back button if a video is playing
    BackHandler(enabled = selectedVideo != null) {
        selectedVideo = null
    }

    Column(modifier = Modifier.fillMaxSize().background(PremiumBlack)) {
        if (selectedVideo == null) {
            // Header for video list
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PremiumWhite)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Hearts2Hearts Videos",
                    color = PremiumWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = HeartsPink)
                }
            } else if (error != null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = error ?: "Terjadi kesalahan", color = Color.Red, modifier = Modifier.padding(16.dp))
                        Button(
                            onClick = { onBack() },
                            colors = ButtonDefaults.buttonColors(containerColor = HeartsPink)
                        ) {
                            Text("Kembali")
                        }
                    }
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(videos) { video ->
                        H2HVideoItem(video = video) {
                            selectedVideo = video
                            playerMode = "player" // reset mode to player on selecting new video
                        }
                    }
                }
            }
        } else {
            // Video Player screen
            val currentVideo = selectedVideo!!
            
            // Trigger stats fetch when video is selected
            LaunchedEffect(currentVideo.youtubeId) {
                videoViewModel.fetchVideoViews(currentVideo.youtubeId)
            }
            
            val videoStatsMap by videoViewModel.videoStats.collectAsState()
            val statsState = videoStatsMap[currentVideo.youtubeId] ?: VideoStatsState()

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { selectedVideo = null }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back to List", tint = PremiumWhite)
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Sekarang Memutar",
                    color = PremiumLightGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            // Embedded player container
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(Color.Black)
            ) {
                if (playerMode == "player") {
                    YouTubePlayer(
                        youtubeVideoId = currentVideo.youtubeId,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    // Dashboard Mode
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color(0xFF1C1B1F), Color(0xFF0F0E11))
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (statsState.isLoading) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                CircularProgressIndicator(color = Color(0xFFFF0000), modifier = Modifier.size(36.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Memuat statistik streaming...", color = PremiumLightGray, fontSize = 12.sp)
                            }
                        } else if (statsState.error != null) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(Icons.Default.Warning, contentDescription = "Error", tint = Color.Yellow, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(statsState.error ?: "", color = PremiumWhite, fontSize = 12.sp, textAlign = TextAlign.Center)
                            }
                        } else {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxSize().padding(16.dp),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "YOUTUBE LIVE STREAM STATS",
                                    color = Color(0xFFFF0000),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                )
                                
                                Spacer(modifier = Modifier.height(6.dp))
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = "Views",
                                        tint = PremiumWhite,
                                        modifier = Modifier.size(24.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "${statsState.viewCount} x ditonton",
                                        color = PremiumWhite,
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                                
                                Spacer(modifier = Modifier.height(10.dp))
                                
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Favorite,
                                            contentDescription = "Likes",
                                            tint = HeartsPink,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${statsState.likeCount} suka",
                                            color = PremiumLightGray,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                    
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Comment,
                                            contentDescription = "Comments",
                                            tint = Color(0xFF00F2FE),
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = "${statsState.commentCount} komentar",
                                            color = PremiumLightGray,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Segmented mode switcher below the player
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Button(
                    onClick = { playerMode = "player" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (playerMode == "player") HeartsPink else PremiumDarkGray,
                        contentColor = PremiumWhite
                    ),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Text("In-App Player", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                
                Button(
                    onClick = { playerMode = "dashboard" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (playerMode == "dashboard") HeartsPink else PremiumDarkGray,
                        contentColor = PremiumWhite
                    ),
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(vertical = 10.dp)
                ) {
                    Text("Streaming Stats", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Video info and controls below player
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp)
            ) {
                item {
                    // Demo mode banner helper
                    if (statsState.isDemo) {
                        Surface(
                            color = Color(0xFFFFB300).copy(alpha = 0.1f),
                            shape = RoundedCornerShape(8.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB300).copy(alpha = 0.2f)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Demo Mode",
                                    tint = Color(0xFFFFB300),
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Mode Demo: Atur YOUTUBE_API_KEY di AI Studio Secrets untuk data real-time.",
                                    color = PremiumLightGray,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }

                    // External video warning block when inside Dashboard mode
                    if (playerMode == "dashboard") {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = Color(0xFFFF0000).copy(alpha = 0.1f)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFF0000).copy(alpha = 0.3f)),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(14.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Info",
                                    tint = Color(0xFFFF0000),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "Video mungkin dibatasi pemutarannya di dalam aplikasi oleh kebijakan agensi.",
                                    color = PremiumWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentVideo.videoUrl))
                                        context.startActivity(intent)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.OpenInNew, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Tonton Langsung di YouTube", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Streaming di YouTube resmi membantu mendukung rilisan Hearts2Hearts!",
                                    color = PremiumLightGray,
                                    fontSize = 11.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentVideo.type.uppercase(),
                            color = HeartsPink,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .background(HeartsPink.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                        
                        IconButton(onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(currentVideo.videoUrl))
                            context.startActivity(intent)
                        }) {
                            Icon(
                                imageVector = Icons.Default.OpenInNew,
                                contentDescription = "Buka di YouTube",
                                tint = PremiumLightGray
                            )
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(10.dp))
                    
                    Text(
                        text = currentVideo.title,
                        color = PremiumWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    Divider(color = PremiumDarkGray, thickness = 1.dp)
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Rekomendasi Video Lainnya",
                        color = PremiumWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Show list of other videos
                val recommendations = videos.filter { it.id != currentVideo.id }
                items(recommendations) { video ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedVideo = video }
                            .padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val videoThumbnail = if (video.thumbnailUrl == "https://youtube.com" || video.thumbnailUrl.isBlank()) {
                            "https://img.youtube.com/vi/${video.youtubeId}/hqdefault.jpg"
                        } else {
                            video.thumbnailUrl
                        }

                        Box(
                            modifier = Modifier
                                .size(width = 120.dp, height = 68.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(PremiumDarkGray),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = videoThumbnail,
                                contentDescription = video.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Icon(
                                imageVector = Icons.Default.PlayCircleFilled,
                                contentDescription = "Play",
                                tint = PremiumWhite.copy(alpha = 0.8f),
                                modifier = Modifier.size(28.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = video.title,
                                color = PremiumWhite,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = video.type,
                                color = PremiumLightGray,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun H2HVideoItem(video: H2HVideo, onClick: () -> Unit) {
    val finalThumbnailUrl = if (video.thumbnailUrl == "https://youtube.com" || video.thumbnailUrl.isBlank()) {
        "https://img.youtube.com/vi/${video.youtubeId}/hqdefault.jpg"
    } else {
        video.thumbnailUrl
    }

    Surface(
        color = PremiumDarkGray,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(PremiumBlack),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = finalThumbnailUrl,
                    contentDescription = video.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                Icon(
                    imageVector = Icons.Default.PlayCircleFilled,
                    contentDescription = "Play",
                    tint = PremiumWhite.copy(alpha = 0.8f),
                    modifier = Modifier.size(48.dp)
                )
                
                // Type Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .background(HeartsPink, RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = video.type,
                        color = PremiumWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = video.title,
                    color = PremiumWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}
