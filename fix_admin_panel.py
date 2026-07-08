import sys

with open('app/src/main/java/com/example/ui/screens/MainScreen.kt', 'r') as f:
    lines = f.readlines()

new_lines = []
i = 0
while i < len(lines):
    line = lines[i]
    if 'NavigationDrawerItem(' in line and i + 1 < len(lines) and 'label = { Text("Admin Panel") }' in lines[i+1]:
        new_lines.append('                if (isAdmin) {\n')
        new_lines.append('                    ' + line.lstrip())
        new_lines.append('                    ' + lines[i+1].lstrip())
        new_lines.append('                    ' + lines[i+2].lstrip())
        new_lines.append('                    ' + lines[i+3].lstrip())
        new_lines.append('                    ' + lines[i+4].lstrip())
        new_lines.append('                }\n')
        i += 5
    else:
        new_lines.append(line)
        i += 1

with open('app/src/main/java/com/example/ui/screens/MainScreen.kt', 'w') as f:
    f.writelines(new_lines)

