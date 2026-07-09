package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material.icons.filled.Article
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.AlternateEmail
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.CommunityUpdateEntity
import com.example.ui.theme.*
import com.example.ui.components.bounceClick
import androidx.compose.animation.*
import com.example.ui.viewmodel.HeartsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.ui.platform.LocalContext
import android.content.Intent
import android.net.Uri

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(viewModel: HeartsViewModel) {
    val updates by viewModel.updates.collectAsStateWithLifecycle()
    val isRssLoading by viewModel.isRssLoading.collectAsStateWithLifecycle()
    val rssError by viewModel.rssError.collectAsStateWithLifecycle()
    
    val scope = rememberCoroutineScope()

    PullToRefreshBox(
        isRefreshing = isRssLoading,
        onRefresh = {
            viewModel.fetchRssUpdates()
        },
        modifier = Modifier.fillMaxSize().background(PremiumBlack)
    ) {
        if (updates.isEmpty() && !isRssLoading) {
            EmptyState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(updates, key = { it.id }) { update ->
                    val context = LocalContext.current
                    UpdateCard(update = update, onClick = { 
                        update.sourceUrl?.let { url ->
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            try {
                                context.startActivity(intent)
                            } catch(e: Exception) {
                                // Ignore
                            }
                        }
                    })
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Article,
            contentDescription = "No updates",
            tint = PremiumMediumGray,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Belum ada update terbaru",
            color = PremiumWhite,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Tarik ke bawah untuk memuat ulang",
            color = PremiumLightGray,
            fontSize = 14.sp
        )
    }
}

@Composable
fun UpdateCard(update: CommunityUpdateEntity, onClick: () -> Unit) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(update.timestamp))
    
    val icon = when (update.type.lowercase()) {
        "video", "youtube" -> Icons.Default.PlayCircle
        "live" -> Icons.Default.WifiTethering
        "instagram" -> Icons.Default.CameraAlt
        "tiktok" -> Icons.Default.MusicNote
        "x" -> Icons.Default.AlternateEmail
        else -> Icons.Default.Campaign
    }
    
    val iconTint = when (update.type.lowercase()) {
        "video", "youtube" -> Color(0xFFFF0000) // YouTube Red
        "live" -> StatusGreen
        "instagram" -> Color(0xFFE1306C) // Instagram Pinkish
        "tiktok" -> Color(0xFF00F2FE) // TikTok Cyan
        "x" -> Color(0xFF1DA1F2) // Twitter/X Blue
        else -> HeartsGold
    }

    val typeLabel = when (update.type.lowercase()) {
        "video" -> "OFFICIAL VIDEO"
        "youtube" -> "YOUTUBE"
        "instagram" -> "INSTAGRAM"
        "tiktok" -> "TIKTOK"
        "x" -> "X (TWITTER)"
        "live" -> "LIVESTREAM"
        else -> update.type.uppercase()
    }

    Surface(
        color = PremiumDarkGray,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick().clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PremiumMediumGray),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = icon, contentDescription = null, tint = iconTint)
                }
                
                Spacer(modifier = Modifier.width(12.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (update.isPinned) {
                            Icon(
                                imageVector = Icons.Default.PushPin,
                                contentDescription = "Pinned",
                                tint = HeartsGold,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                        }
                        Text(
                            text = typeLabel,
                            color = iconTint,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = dateString,
                        color = PremiumLightGray,
                        fontSize = 12.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = update.title,
                color = PremiumWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(6.dp))
            
            Text(
                text = update.content,
                color = PremiumLightGray,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            if (!update.thumbnailUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PremiumMediumGray)
                ) {
                    coil.compose.AsyncImage(
                        model = update.thumbnailUrl,
                        contentDescription = update.title,
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
