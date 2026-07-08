with open('app/src/main/java/com/example/ui/screens/LeaderboardScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('private data class LeaderboardUser', 'data class LeaderboardUser')
content = content.replace('import androidx.compose.ui.draw.clip', 'import androidx.compose.ui.draw.clip\nimport androidx.compose.ui.graphics.Color')

with open('app/src/main/java/com/example/ui/screens/LeaderboardScreen.kt', 'w') as f:
    f.write(content)
