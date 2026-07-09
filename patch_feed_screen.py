import re

with open('app/src/main/java/com/example/ui/screens/FeedScreen.kt', 'r') as f:
    content = f.read()

content = content.replace(
    'import androidx.compose.material.icons.filled.Article',
    'import androidx.compose.material.icons.filled.Article\nimport androidx.compose.material.icons.filled.Share'
)

old_icon = """        val icon = when (update.type.lowercase()) {
            "video" -> Icons.Default.PlayCircle
            "live" -> Icons.Default.WifiTethering
            else -> Icons.Default.Campaign
        }"""
        
new_icon = """        val icon = when (update.type.lowercase()) {
            "video" -> Icons.Default.PlayCircle
            "live" -> Icons.Default.WifiTethering
            "social_media" -> Icons.Default.Share
            else -> Icons.Default.Campaign
        }"""

old_icon_tint = """        val iconTint = when (update.type.lowercase()) {
            "video" -> HeartsPink
            "live" -> StatusGreen
            else -> HeartsGold
        }"""
        
new_icon_tint = """        val iconTint = when (update.type.lowercase()) {
            "video" -> HeartsPink
            "live" -> StatusGreen
            "social_media" -> PremiumWhite
            else -> HeartsGold
        }"""

content = content.replace(old_icon, new_icon)
content = content.replace(old_icon_tint, new_icon_tint)

# Replace the onClick to use an intent to open the url if it's social media or a web link.
# First, add the required imports.
content = content.replace(
    'import java.util.Locale',
    'import java.util.Locale\nimport androidx.compose.ui.platform.LocalContext\nimport android.content.Intent\nimport android.net.Uri'
)

# Then update FeedScreen lazycolumn
old_feed_lazy = """                items(updates, key = { it.id }) { update ->
                    UpdateCard(update = update, onClick = { /* TODO: Open detail/URL */ })
                }"""

new_feed_lazy = """                items(updates, key = { it.id }) { update ->
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
                }"""

content = content.replace(old_feed_lazy, new_feed_lazy)

with open('app/src/main/java/com/example/ui/screens/FeedScreen.kt', 'w') as f:
    f.write(content)
