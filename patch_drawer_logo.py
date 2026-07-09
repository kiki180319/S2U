with open('app/src/main/java/com/example/ui/screens/MainScreen.kt', 'r') as f:
    content = f.read()

import re

new_drawer = """                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(16.dp)) {
                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = com.example.R.drawable.new_logo_seahorses_1783519843079),
                        contentDescription = "App Logo",
                        modifier = Modifier
                            .size(48.dp)
                            .androidx.compose.ui.draw.clip(androidx.compose.foundation.shape.CircleShape)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Hearts2Hearts", style = MaterialTheme.typography.headlineMedium, color = HeartsPink)
                }
                HorizontalDivider(color = PremiumMediumGray)"""

content = content.replace("""                Spacer(Modifier.height(16.dp))
                Text("Hearts2Hearts", style = MaterialTheme.typography.headlineMedium, modifier = Modifier.padding(16.dp), color = HeartsPink)
                HorizontalDivider(color = PremiumMediumGray)""", new_drawer)

with open('app/src/main/java/com/example/ui/screens/MainScreen.kt', 'w') as f:
    f.write(content)
