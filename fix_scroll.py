with open('app/src/main/java/com/example/ui/screens/MembersScreen.kt', 'r') as f:
    content = f.read()

# Add imports for scrolling
imports_to_add = """import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
"""

content = content.replace("import androidx.compose.foundation.shape.RoundedCornerShape", imports_to_add + "import androidx.compose.foundation.shape.RoundedCornerShape")

content = content.replace("Modifier.fillMaxSize().padding(16.dp).verticalScroll(androidx.compose.foundation.rememberScrollState())", "Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())")

with open('app/src/main/java/com/example/ui/screens/MembersScreen.kt', 'w') as f:
    f.write(content)
