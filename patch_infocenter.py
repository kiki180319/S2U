import re

with open('app/src/main/java/com/example/ui/screens/InformationCenter.kt', 'r') as f:
    content = f.read()

# remove MembersScreen
members_screen_pattern = re.compile(r'@Composable\s*fun MembersScreen\(onBack: \(\) -> Unit\)\s*\{.*?\n\}\n', re.DOTALL)
content = members_screen_pattern.sub('', content)

with open('app/src/main/java/com/example/ui/screens/InformationCenter.kt', 'w') as f:
    f.write(content)
