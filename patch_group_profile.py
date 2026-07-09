import re

with open('app/src/main/java/com/example/ui/screens/InformationCenter.kt', 'r') as f:
    content = f.read()

# I need to add Official Links to GroupProfileScreen.
# It should include intent to open urls. I'll need to add LocalContext and Intent.

replacement = """                Spacer(modifier = Modifier.height(16.dp))
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
                        colors = CardDefaults.cardColors(containerColor = PremiumDarkGray),
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
}"""

content = content.replace("""                Spacer(modifier = Modifier.height(16.dp))
                Text("Fandom", color = HeartsPink, fontWeight = FontWeight.Bold)
                Text("Name: S2U (하츄)", color = PremiumWhite)
                Text("Color: Sky Blue", color = PremiumWhite)
                Text("Mascot: Falabella", color = PremiumWhite)
            }
        }
    }
}""", replacement)

with open('app/src/main/java/com/example/ui/screens/InformationCenter.kt', 'w') as f:
    f.write(content)
