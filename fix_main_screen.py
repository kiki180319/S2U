import re

with open('app/src/main/java/com/example/ui/screens/MainScreen.kt', 'r') as f:
    content = f.read()

# Add imports for AnimatedContent
imports = """
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
"""
content = content.replace('import androidx.compose.material3.*', 'import androidx.compose.material3.*\n' + imports)

# We want to replace the `when (currentTab)` block with `AnimatedContent`
when_block = """                when (currentTab) {
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
                }"""

animated_content = """                AnimatedContent(
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
                }"""

content = content.replace(when_block, animated_content)

# Update TopAppBar to CenterAlignedTopAppBar
content = content.replace('TopAppBar(', 'CenterAlignedTopAppBar(')

with open('app/src/main/java/com/example/ui/screens/MainScreen.kt', 'w') as f:
    f.write(content)
