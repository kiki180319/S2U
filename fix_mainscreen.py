with open('app/src/main/java/com/example/ui/screens/MainScreen.kt', 'r') as f:
    content = f.read()

imports = """import androidx.compose.ui.Alignment
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.CircleShape
import com.example.R
"""

content = content.replace("import androidx.compose.ui.Modifier", imports + "\nimport androidx.compose.ui.Modifier")

new_row = """                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                    Image(
                        painter = painterResource(id = R.drawable.new_logo_seahorses_1783519843079),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Hearts2Hearts", style = MaterialTheme.typography.headlineMedium, color = HeartsPink)
                }"""

import re
content = re.sub(r'                Row\(verticalAlignment = Alignment.CenterVertically, modifier = Modifier\.padding\(16\.dp\)\) \{.*?\n                \}', new_row, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/screens/MainScreen.kt', 'w') as f:
    f.write(content)
