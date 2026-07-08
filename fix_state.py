import sys

with open('app/src/main/java/com/example/ui/screens/MainScreen.kt', 'r') as f:
    content = f.read()

content = content.replace('var chatMessage by remember { mutableStateOf("") }', 'var chatMessage by rememberSaveable { mutableStateOf("") }')
content = content.replace('var selectedTab by remember { mutableStateOf("Global") }', 'var selectedTab by rememberSaveable { mutableStateOf("Global") }')
content = content.replace('var inputMessage by remember { mutableStateOf("") }', 'var inputMessage by rememberSaveable { mutableStateOf("") }')

with open('app/src/main/java/com/example/ui/screens/MainScreen.kt', 'w') as f:
    f.write(content)
