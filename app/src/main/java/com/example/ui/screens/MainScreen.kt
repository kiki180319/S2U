package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.foundation.shape.CircleShape
import com.example.R

import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
import com.example.data.database.EventEntity
import com.example.ui.viewmodel.HeartsViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: HeartsViewModel) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val currentTab by viewModel.currentTab.collectAsStateWithLifecycle()
    val isAdmin by viewModel.isAdmin.collectAsStateWithLifecycle()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet(
                drawerContainerColor = PremiumDarkGray,
                drawerContentColor = PremiumWhite
            ) {
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.img_logo),
                        contentDescription = "App Logo",
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Hearts2Hearts", style = MaterialTheme.typography.headlineMedium, color = HeartsPink)
                }
                HorizontalDivider(color = PremiumMediumGray)
                
                val tabs = listOf(
                    "home" to "Home",
                    "events" to "Events",
                    "forum" to "Forum",
                    "aichat" to "AI Companion",
                    "infocenter" to "Info Center",
                    "leaderboard" to "Leaderboard",
                    "top50chat" to "Top 50 Chat",
                    "streamingparty" to "Streaming Party",
                    "videodashboard" to "Video Dashboard",
                    "profile" to "Profile",
                    "settings" to "Settings"
                )

                tabs.forEach { (route, label) ->
                    NavigationDrawerItem(
                        label = { Text(label, color = PremiumWhite) },
                        selected = currentTab == route,
                        onClick = { 
                            viewModel.selectTab(route)
                            scope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = HeartsPink.copy(alpha = 0.2f),
                            unselectedContainerColor = PremiumDarkGray
                        )
                    )
                }
                
                if (isAdmin) {
                    NavigationDrawerItem(
                        label = { Text("Admin Panel", color = HeartsPink) },
                        selected = currentTab == "adminpanel",
                        onClick = { 
                            viewModel.selectTab("adminpanel")
                            scope.launch { drawerState.close() }
                        },
                        colors = NavigationDrawerItemDefaults.colors(
                            selectedContainerColor = HeartsPink.copy(alpha = 0.2f),
                            unselectedContainerColor = PremiumDarkGray
                        )
                    )
                }
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Hearts2Hearts", color = PremiumWhite) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = PremiumWhite)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PremiumBlack
                    )
                )
            },
            containerColor = PremiumBlack
        ) { paddingValues ->
            val homeViewModel: com.example.ui.viewmodel.HomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(factory = com.example.ui.viewmodel.HomeViewModel.Factory)
            val nearestEvent by homeViewModel.nearestEvent.collectAsStateWithLifecycle()

            Box(modifier = Modifier.fillMaxSize()) {
                Image(
                    painter = painterResource(id = R.drawable.img_info_bg),
                    contentDescription = "App Background",
                    contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                    modifier = Modifier.fillMaxSize().alpha(0.3f)
                )
                Box(modifier = Modifier.fillMaxSize().background(PremiumBlack.copy(alpha = 0.6f)))
                
                Column(
                    modifier = Modifier
                        .padding(paddingValues)
                        .fillMaxSize()
                ) {
                    if (currentTab == "home") {
                        nearestEvent?.let { event ->
                            NearestEventBanner(
                                event = event,
                                onActionClick = { viewModel.selectTab("events") }
                            )
                        }
                    }

                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                    AnimatedContent(
                    targetState = currentTab,
                    transitionSpec = {
                        slideInHorizontally(
                            initialOffsetX = { fullWidth -> fullWidth },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        ) togetherWith slideOutHorizontally(
                            targetOffsetX = { fullWidth -> -fullWidth },
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMediumLow
                            )
                        )
                    },
                    label = "TabTransition"
                ) { targetTab ->
                    when (targetTab) {
                        "home" -> FeedScreen(viewModel)
                        "events" -> EventsScreen(viewModel)
                        "forum" -> ForumScreen(viewModel)
                        "aichat" -> AiChatScreen(viewModel)
                        "infocenter" -> InfoCenterScreen(viewModel)
                        "profile" -> ProfileScreen(viewModel)
                        "leaderboard" -> LeaderboardScreen(viewModel)
                        "top50chat" -> Top50ChatScreen(viewModel)
                        "streamingparty" -> StreamingPartyScreen(viewModel)
                        "live_room" -> {
                            val streamId = viewModel.currentLiveStreamId.collectAsStateWithLifecycle().value ?: "live_default_carmen"
                            LiveRoomScreen(
                                streamId = streamId,
                                viewModel = viewModel,
                                onBack = { viewModel.selectTab("streamingparty") }
                            )
                        }
                        "videodashboard" -> H2HVideoScreen(onBack = { viewModel.selectTab("home") })
                        "settings" -> SettingsScreen(viewModel)
                        "adminpanel" -> AdminPanelScreen(viewModel)
                        else -> FeedScreen(viewModel)
                    }
                }
            }
                }
        }
    }
}
}

@Composable
fun NearestEventBanner(
    event: com.example.data.database.EventEntity,
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.primary.copy(alpha = 0.4f)
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        androidx.compose.foundation.shape.CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Event Terdekat",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Event Terdekat: ${event.title}",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = event.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                    maxLines = 1,
                    overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "📍 ${event.location} • 📅 ${event.date}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Button(
                onClick = onActionClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                modifier = Modifier.height(32.dp)
            ) {
                Text(
                    text = "Lihat",
                    fontSize = 11.sp,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}
