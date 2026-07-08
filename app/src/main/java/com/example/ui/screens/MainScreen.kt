package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.theme.*
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
                Text("Hearts2Hearts", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(16.dp), color = HeartsPink)
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
                    "profile" to "Profile"
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
            Box(modifier = Modifier.padding(paddingValues).fillMaxSize()) {
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
                        "videodashboard" -> VideoDashboardScreen(viewModel)
                        "adminpanel" -> AdminPanelScreen(viewModel)
                        else -> FeedScreen(viewModel)
                    }
                }
            }
        }
    }
}
