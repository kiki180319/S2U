import re

with open('app/src/main/java/com/example/ui/screens/InformationCenter.kt', 'r') as f:
    content = f.read()

# Fix the extra braces and grid syntax
# We'll just replace the whole InfoDashboard function to be safe.
dashboard_start = content.find('@Composable\nfun InfoDashboard')
dashboard_end = content.find('@Composable\nfun GroupProfileScreen')

if dashboard_start != -1 and dashboard_end != -1:
    new_dashboard = """@Composable
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
                Text("No results found for \\"$searchQuery\\"", color = PremiumLightGray, modifier = Modifier.padding(16.dp))
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
"""
    content = content[:dashboard_start] + new_dashboard + "\n" + content[dashboard_end:]

# Fix icons import issue by adding proper imports
imports = """
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Star
"""
content = content.replace('import androidx.compose.material.icons.filled.Search', imports + '\nimport androidx.compose.material.icons.filled.Search')

with open('app/src/main/java/com/example/ui/screens/InformationCenter.kt', 'w') as f:
    f.write(content)

