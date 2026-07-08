with open('app/src/main/java/com/example/ui/screens/LeaderboardScreen.kt', 'w') as f:
    f.write("""package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AvatarBadge
import com.example.ui.components.bounceClick
import com.example.ui.theme.*
import com.example.ui.viewmodel.HeartsViewModel

// Local data structure for the derived leaderboard
private data class LeaderboardUser(
    val rank: Int,
    val name: String,
    val score: Int,
    val isCurrentUser: Boolean
)

@Composable
fun LeaderboardScreen(viewModel: HeartsViewModel) {
    val threads by viewModel.threads.collectAsStateWithLifecycle()
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()

    val currentUserName = userProfile?.name ?: ""

    // Derive leaderboard from threads (no new ViewModel/DB entity)
    val leaderboard = remember(threads, currentUserName) {
        threads.groupBy { it.author }
            .map { (author, userThreads) ->
                val score = (userThreads.size * 50) + (userThreads.sumOf { it.upvotes } * 10)
                Pair(author, score)
            }
            .sortedByDescending { it.second }
            .mapIndexed { index, pair ->
                LeaderboardUser(
                    rank = index + 1,
                    name = pair.first,
                    score = pair.second,
                    isCurrentUser = (pair.first == currentUserName)
                )
            }
    }
    
    // Add current user if not in the list but has profile
    val finalLeaderboard = remember(leaderboard, currentUserName) {
        if (currentUserName.isNotBlank() && leaderboard.none { it.isCurrentUser }) {
            leaderboard + LeaderboardUser(
                rank = leaderboard.size + 1,
                name = currentUserName,
                score = 0,
                isCurrentUser = true
            )
        } else {
            leaderboard
        }
    }

    val currentUserEntry = finalLeaderboard.find { it.isCurrentUser }

    Box(modifier = Modifier.fillMaxSize().background(PremiumBlack)) {
        if (finalLeaderboard.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(
                    imageVector = Icons.Default.EmojiEvents,
                    contentDescription = "No Ranking",
                    tint = PremiumMediumGray,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Belum ada peringkat, mulai aktif untuk naik peringkat!",
                    color = PremiumWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
            }
        } else {
            Column(modifier = Modifier.fillMaxSize()) {
                Text(
                    text = "Top Fans Leaderboard",
                    color = PremiumWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
                
                LazyColumn(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    itemsIndexed(finalLeaderboard, key = { _, item -> item.name }) { index, user ->
                        LeaderboardRow(user = user)
                        if (index < finalLeaderboard.size - 1) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = PremiumMediumGray,
                                thickness = 0.5.dp
                            )
                        }
                    }
                }
            }
            
            // Sticky current user position at bottom
            if (currentUserEntry != null) {
                Surface(
                    color = PremiumDarkGray.copy(alpha = 0.95f),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(bottom = 80.dp), // Avoid bottom nav bar overlap
                    shadowElevation = 8.dp
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        HorizontalDivider(color = HeartsPink, thickness = 2.dp)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Posisimu: #${currentUserEntry.rank}",
                                color = PremiumWhite,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${currentUserEntry.score} pts",
                                color = HeartsPink,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LeaderboardRow(user: LeaderboardUser) {
    val isTop3 = user.rank <= 3
    val rankColor = when (user.rank) {
        1 -> HeartsGold
        2 -> Color(0xFFC0C0C0) // Silver
        3 -> Color(0xFFCD7F32) // Bronze
        else -> PremiumLightGray
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (user.isCurrentUser) PremiumDarkGray else Color.Transparent)
            .bounceClick()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank Number or Badge
        Box(
            modifier = Modifier.size(32.dp),
            contentAlignment = Alignment.Center
        ) {
            if (isTop3) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .background(rankColor.copy(alpha = 0.2f))
                        .border(1.dp, rankColor, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${user.rank}",
                        color = rankColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            } else {
                Text(
                    text = "${user.rank}",
                    color = PremiumLightGray,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp
                )
            }
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        // Avatar
        Box(
            modifier = Modifier.size(40.dp)
        ) {
            AvatarBadge(avatarName = user.name, size = 40.dp, fontSize = 16.sp)
            if (user.isCurrentUser) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .clip(CircleShape)
                        .border(2.dp, HeartsPink, CircleShape)
                )
            }
        }
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // Name
        Text(
            text = user.name + (if (user.isCurrentUser) " (You)" else ""),
            color = if (user.isCurrentUser) HeartsPink else PremiumWhite,
            fontSize = 16.sp,
            fontWeight = if (user.isCurrentUser || isTop3) FontWeight.Bold else FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )
        
        // Score
        Text(
            text = "${user.score} pts",
            color = if (isTop3) rankColor else PremiumLightGray,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
""")
