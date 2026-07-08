with open('app/src/main/java/com/example/ui/viewmodel/HeartsViewModel.kt', 'r') as f:
    content = f.read()

content = content.replace("import kotlinx.coroutines.flow.map\n", "")
content = content.replace("import java.text.SimpleDateFormat\nimport java.util.*\n", "")

with open('app/src/main/java/com/example/ui/viewmodel/HeartsViewModel.kt', 'w') as f:
    f.write(content)
