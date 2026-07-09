package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star

import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.alpha
import com.example.ui.theme.*
import com.example.ui.viewmodel.HeartsViewModel

@Composable
fun InfoCenterScreen(viewModel: HeartsViewModel) {
    var currentSubScreen by remember { mutableStateOf("dashboard") }
    
    BackHandler(enabled = currentSubScreen != "dashboard") {
        currentSubScreen = "dashboard"
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = com.example.R.drawable.img_info_bg),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Box(modifier = Modifier.matchParentSize().background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.4f)))

        Crossfade(targetState = currentSubScreen, label = "InfoCenterTransition") { screen ->
            when (screen) {
                "dashboard" -> InfoDashboard(onNavigate = { currentSubScreen = it })
                "group_profile" -> GroupProfileScreen(onBack = { currentSubScreen = "dashboard" })
                "members" -> MembersScreen(onBack = { currentSubScreen = "dashboard" })
                "discography" -> DiscographyScreen(onBack = { currentSubScreen = "dashboard" })
                "fanguide" -> FanGuideScreen(onBack = { currentSubScreen = "dashboard" })
                else -> InfoDashboard(onNavigate = { currentSubScreen = it })
            }
        }
    }
}

@Composable
fun InfoDashboard(onNavigate: (String) -> Unit) {
    var searchQuery by remember { mutableStateOf("") }
    val allSearchableItems = listOf(
        Pair("Carmen", "Member"),
        Pair("Jiwoo (Leader)", "Member"),
        Pair("Yuha", "Member"),
        Pair("Stella", "Member"),
        Pair("Juun", "Member"),
        Pair("A-na", "Member"),
        Pair("Ian", "Member"),
        Pair("Ye-on", "Member"),
        Pair("The Chase", "Song"),
        Pair("Butterflies", "Song"),
        Pair("Hearts2Hearts Debut Announcement", "News"),
        Pair("The Chase MV Release", "News")
    )
    
    val searchResults = if (searchQuery.isBlank()) {
        emptyList()
    } else {
        allSearchableItems.filter { it.first.contains(searchQuery, ignoreCase = true) }
    }

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)) {
        Spacer(modifier = Modifier.height(16.dp))
        Text("Information Center", color = PremiumWhite, fontSize = 28.sp, fontWeight = FontWeight.Bold)
        Text("The ultimate encyclopedia for Hearts2Hearts", color = PremiumLightGray, fontSize = 14.sp)
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search members, songs, news...", color = PremiumMediumGray) },
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search", tint = PremiumMediumGray) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = HeartsPink,
                unfocusedBorderColor = PremiumMediumGray,
                focusedTextColor = PremiumWhite,
                unfocusedTextColor = PremiumWhite,
                cursorColor = HeartsPink,
                focusedContainerColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.4f),
                unfocusedContainerColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.4f)
            ),
            shape = RoundedCornerShape(24.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        if (searchQuery.isNotBlank()) {
            if (searchResults.isEmpty()) {
                Text("No results found for \"$searchQuery\"", color = PremiumLightGray, modifier = Modifier.padding(16.dp))
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(searchResults) { result ->
                        Card(
                            modifier = Modifier.fillMaxWidth().clickable { 
                                when(result.second) {
                                    "Member" -> onNavigate("members")
                                    "Song" -> onNavigate("discography")
                                    else -> {} 
                                }
                            },
                            colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.4f)),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(result.first, color = PremiumWhite, fontWeight = FontWeight.Bold)
                                Text(result.second, color = HeartsPink, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // InfoSection definitions
                val sections = listOf(
                    Triple("Group Profile", "group_profile", Pair(androidx.compose.material.icons.Icons.Default.Info, androidx.compose.ui.graphics.Color.Red)),
                    Triple("Members", "members", Pair(androidx.compose.material.icons.Icons.Default.Person, androidx.compose.ui.graphics.Color.Blue)),
                    Triple("Discography", "discography", Pair(androidx.compose.material.icons.Icons.Default.PlayArrow, androidx.compose.ui.graphics.Color(0xFFFFA500))),
                    Triple("Fan Guide", "fanguide", Pair(androidx.compose.material.icons.Icons.Default.Star, androidx.compose.ui.graphics.Color.Green))
                )
                items(sections.size) { index ->
                    val section = sections[index]
                    Card(
                        modifier = Modifier.height(120.dp).clickable { onNavigate(section.second) },
                        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize().padding(16.dp),
                            verticalArrangement = Arrangement.Top,
                            horizontalAlignment = Alignment.Start
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(section.third.second, RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(section.third.first, contentDescription = section.first, tint = PremiumWhite, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text(section.first, color = PremiumWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GroupProfileScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PremiumWhite) }
            Text("Group Profile", color = PremiumWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Text("Hearts2Hearts (하츠투하츠 / H2H)", color = PremiumWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Text("Debut: February 24, 2025", color = PremiumLightGray)
                Text("Agency: SM Entertainment", color = PremiumLightGray)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Meaning of the Name", color = HeartsPink, fontWeight = FontWeight.Bold)
                Text("\"Hearts2Hearts\" melambangkan keinginan grup untuk menghubungkan hati para penggemar di seluruh dunia melalui musik yang penuh emosi dan pesan yang tulus.", color = PremiumWhite)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Fandom", color = HeartsPink, fontWeight = FontWeight.Bold)
                Text("Name: S2U (하츄)", color = PremiumWhite)
                Text("Color: Sky Blue", color = PremiumWhite)
                Text("Mascot: Falabella", color = PremiumWhite)
                
                Spacer(modifier = Modifier.height(24.dp))
                Text("Official Links", color = HeartsPink, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Spacer(modifier = Modifier.height(8.dp))
                
                val context = androidx.compose.ui.platform.LocalContext.current
                
                val links = listOf(
                    "Album Lemon Tang" to "https://hearts2hearts.lnk.to/LemonTang",
                    "Instagram" to "https://instagram.com/hearts2hearts",
                    "X (Twitter)" to "https://x.com/Hearts2Hearts",
                    "TikTok" to "https://tiktok.com/@hearts2hearts",
                    "Facebook" to "https://facebook.com/hearts2heartsH2H",
                    "YouTube" to "https://www.youtube.com/@hearts2hearts.official"
                )
                
                links.forEach { (title, url) ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable {
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                                try {
                                    context.startActivity(intent)
                                } catch (e: Exception) { }
                            },
                        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.4f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Search, contentDescription = null, tint = HeartsPink)
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(title, color = PremiumWhite, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun DiscographyScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PremiumWhite) }
            Text("Discography", color = PremiumWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Box(modifier = Modifier.fillMaxWidth().height(150.dp).background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.4f)), contentAlignment = Alignment.Center) {
                            Text("THE CHASE", color = PremiumWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        Text("1st Mini Album: The Chase", color = HeartsPink, fontWeight = FontWeight.Bold)
                        Text("Release: Feb 24, 2025", color = PremiumLightGray, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("1. The Chase (Title)\n2. Butterflies", color = PremiumWhite)
                    }
                }
            }
        }
    }
}

@Composable
fun FanGuideScreen(onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PremiumWhite) }
            Text("Fan Guide", color = PremiumWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                Text("Beginner Guide", color = HeartsPink, fontWeight = FontWeight.Bold)
                Text("Welcome to the S2U fandom! Here is everything you need to know about supporting Hearts2Hearts.", color = PremiumWhite)
                Spacer(modifier = Modifier.height(16.dp))
                Text("Streaming Rules", color = HeartsPink, fontWeight = FontWeight.Bold)
                Text("- Do not use bots.\n- Watch videos manually.\n- Share on social media.", color = PremiumWhite)
            }
        }
    }
}
