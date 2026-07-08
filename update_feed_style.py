with open('app/src/main/java/com/example/ui/screens/FeedScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('import com.example.ui.theme.*', 'import com.example.ui.theme.*\nimport com.example.ui.components.bounceClick\nimport androidx.compose.animation.*')
content = content.replace('.clickable { onClick() }', '.bounceClick().clickable { onClick() }')

with open('app/src/main/java/com/example/ui/screens/FeedScreen.kt', 'w') as f:
    f.write(content)
