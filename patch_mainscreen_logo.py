import re

with open('app/src/main/java/com/example/ui/screens/MainScreen.kt', 'r') as f:
    content = f.read()

new_image = """                    coil.compose.AsyncImage(
                        model = "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/logo.jpg",
                        contentDescription = "App Logo",
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                    )"""

content = re.sub(r'                    Image\(\n.*?painter = painterResource\(id = R\.drawable\.new_logo_seahorses_1783519843079\),\n.*?contentDescription = "App Logo",\n.*?modifier = Modifier\n.*?\.size\(48\.dp\)\n.*?\.clip\(CircleShape\)\n                    \)', new_image, content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/screens/MainScreen.kt', 'w') as f:
    f.write(content)
