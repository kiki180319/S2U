package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.example.data.database.VideoEntity
import com.example.ui.components.bounceClick
import com.example.ui.theme.*
import com.example.ui.viewmodel.HeartsViewModel

@OptIn(ExperimentalAnimationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VideoDashboardScreen(viewModel: HeartsViewModel) {
    val videos by viewModel.videos.collectAsStateWithLifecycle()
    val selectedVideo by viewModel.selectedVideo.collectAsStateWithLifecycle()
    val isVideosLoading by viewModel.isVideosLoading.collectAsStateWithLifecycle()
    val videosError by viewModel.videosError.collectAsStateWithLifecycle()
    
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Semua", "Favorit")

    val displayedVideos = remember(videos, selectedTab) {
        if (selectedTab == 0) videos else videos.filter { it.isFavorite }
    }

    Box(modifier = Modifier.fillMaxSize().background(PremiumBlack)) {
        AnimatedContent(
            targetState = selectedVideo,
            transitionSpec = {
                if (targetState != null) {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                } else {
                    slideInHorizontally { width -> -width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> width } + fadeOut()
                }
            },
            label = "VideoNavigation"
        ) { video ->
            if (video == null) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Custom TabRow
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                            .clip(RoundedCornerShape(24.dp))
                            .background(PremiumMediumGray)
                            .padding(4.dp)
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(if (selectedTab == index) PremiumDarkGray else Color.Transparent)
                                    .clickable { selectedTab = index }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = title,
                                    color = if (selectedTab == index) PremiumWhite else PremiumLightGray,
                                    fontSize = 14.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    if (isVideosLoading) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = HeartsPink)
                        }
                    } else if (videosError != null && videos.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideoLibrary,
                                contentDescription = "Error loading videos",
                                tint = PremiumMediumGray,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = videosError!!,
                                color = PremiumWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                                modifier = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    } else if (displayedVideos.isEmpty()) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideoLibrary,
                                contentDescription = "No videos",
                                tint = PremiumMediumGray,
                                modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (selectedTab == 1) "Belum ada video favorit." else "Belum ada video, nantikan konten terbaru!",
                                color = PremiumWhite,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(displayedVideos, key = { it.videoId }) { item ->
                                VideoCard(
                                    video = item,
                                    onClick = { viewModel.selectVideo(item) },
                                    onFavorite = { viewModel.toggleFavoriteVideo(item) }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(80.dp)) }
                        }
                    }
                }
            } else {
                VideoDetailScreen(
                    video = video,
                    onBack = { viewModel.selectVideo(null) },
                    onFavorite = { viewModel.toggleFavoriteVideo(video) }
                )
            }
        }
    }
}

@Composable
fun VideoCard(
    video: VideoEntity,
    onClick: () -> Unit,
    onFavorite: () -> Unit
) {
    Surface(
        color = PremiumDarkGray,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick()
            .clickable { onClick() }
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
                    .background(PremiumBlack)
            ) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = video.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                
                // Duration Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .background(PremiumBlack.copy(alpha = 0.8f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = video.duration,
                        color = PremiumWhite,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = video.title,
                        color = PremiumWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = video.channelName,
                        color = PremiumLightGray,
                        fontSize = 14.sp
                    )
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                IconButton(
                    onClick = onFavorite,
                    modifier = Modifier.size(24.dp).bounceClick()
                ) {
                    Icon(
                        imageVector = if (video.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (video.isFavorite) HeartsPink else PremiumLightGray
                    )
                }
            }
        }
    }
}

@Composable
fun VideoDetailScreen(
    video: VideoEntity,
    onBack: () -> Unit,
    onFavorite: () -> Unit
) {
    BackHandler { onBack() }

    Column(modifier = Modifier.fillMaxSize().background(PremiumBlack)) {
        // Pseudo Player (Placeholder for actual video player)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f)
                .background(PremiumBlack),
            contentAlignment = Alignment.Center
        ) {
            AsyncImage(
                model = video.thumbnailUrl,
                contentDescription = video.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                alpha = 0.5f // Dim the thumbnail a bit to make play button visible
            )
            
            IconButton(
                onClick = { /* TODO: Play video */ },
                modifier = Modifier.size(64.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayCircleFilled,
                    contentDescription = "Play",
                    tint = PremiumWhite,
                    modifier = Modifier.fillMaxSize()
                )
            }
            
            IconButton(
                onClick = onBack,
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .background(PremiumBlack.copy(alpha = 0.5f), CircleShape)
            ) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PremiumWhite)
            }
        }
        
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = video.title,
                        color = PremiumWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                    
                    Spacer(modifier = Modifier.width(16.dp))
                    
                    IconButton(
                        onClick = onFavorite,
                        modifier = Modifier.bounceClick()
                    ) {
                        Icon(
                            imageVector = if (video.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (video.isFavorite) HeartsPink else PremiumWhite
                        )
                    }
                }
            }
            
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(PremiumDarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = video.channelName.take(1).uppercase(),
                            color = HeartsPink,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = video.channelName,
                        color = PremiumWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            item {
                HorizontalDivider(color = PremiumMediumGray, thickness = 0.5.dp)
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Description",
                    color = PremiumWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = video.description,
                    color = PremiumLightGray,
                    fontSize = 14.sp,
                    lineHeight = 22.sp
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}
