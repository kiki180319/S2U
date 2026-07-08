import re

with open('app/src/main/java/com/example/ui/screens/VideoDashboardScreen.kt', 'r') as f:
    content = f.read()

# Add states fetching
state_fetching = """    val selectedVideo by viewModel.selectedVideo.collectAsStateWithLifecycle()
    val isVideosLoading by viewModel.isVideosLoading.collectAsStateWithLifecycle()
    val videosError by viewModel.videosError.collectAsStateWithLifecycle()"""

content = content.replace("    val selectedVideo by viewModel.selectedVideo.collectAsStateWithLifecycle()", state_fetching)

old_logic = """                    if (videos.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            CircularProgressIndicator(color = HeartsPink)
                        }
                    } else if (displayedVideos.isEmpty()) {"""

new_logic = """                    if (isVideosLoading) {
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
                    } else if (displayedVideos.isEmpty()) {"""

content = content.replace(old_logic, new_logic)

with open('app/src/main/java/com/example/ui/screens/VideoDashboardScreen.kt', 'w') as f:
    f.write(content)
