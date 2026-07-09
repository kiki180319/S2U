import re

with open('app/src/main/java/com/example/ui/screens/InformationCenter.kt', 'r') as f:
    content = f.read()

# Fix Image and Box backgrounds in InfoCenterScreen
content = re.sub(
    r'Box\(modifier = Modifier\.fillMaxSize\(\)\.background\(PremiumBlack\)\) \{.*?Crossfade',
    r'''Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = com.example.R.drawable.img_info_bg),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.matchParentSize()
        )
        Box(modifier = Modifier.matchParentSize().background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.4f)))

        Crossfade''',
    content,
    flags=re.DOTALL
)

# Replace the lazy grid sections to include icon and color
grid_replacement = """
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                val sections = listOf(
                    Triple("Group Profile", "group_profile", Triple(androidx.compose.material.icons.Icons.Default.Info, androidx.compose.ui.graphics.Color.Red, "Group Profile")),
                    Triple("Members", "members", Triple(androidx.compose.material.icons.Icons.Default.Person, androidx.compose.ui.graphics.Color.Blue, "Members")),
                    Triple("Discography", "discography", Triple(androidx.compose.material.icons.Icons.Default.PlayArrow, androidx.compose.ui.graphics.Color(0xFFFFA500), "Discography")),
                    Triple("Fan Guide", "fanguide", Triple(androidx.compose.material.icons.Icons.Default.Star, androidx.compose.ui.graphics.Color.Green, "Fan Guide"))
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
                                Icon(section.third.first, contentDescription = section.third.third, tint = PremiumWhite, modifier = Modifier.size(24.dp))
                            }
                            Spacer(modifier = Modifier.weight(1f))
                            Text(section.first, color = PremiumWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
            }
"""

content = re.sub(
    r'LazyVerticalGrid\(.*?columns = GridCells.Fixed\(2\),.*?\}\n\s*\}\n\s*\}',
    grid_replacement.strip(),
    content,
    flags=re.DOTALL
)

# Update other Card colors to transparent black
content = content.replace('PremiumDarkGray', 'androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.4f)')

with open('app/src/main/java/com/example/ui/screens/InformationCenter.kt', 'w') as f:
    f.write(content)

