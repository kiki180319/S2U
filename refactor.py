import os
import re

main_file = "app/src/main/java/com/example/ui/screens/MainScreen.kt"

with open(main_file, "r") as f:
    lines = f.readlines()

# Extract imports
package_line = ""
imports = []
content_start = 0

for i, line in enumerate(lines):
    if line.startswith("package"):
        package_line = line
    elif line.startswith("import"):
        imports.append(line)
    elif line.startswith("@Composable"):
        content_start = i
        break

imports.append("import com.example.ui.components.*\n")

ranges = [
    (248, 1005, "FeedScreen.kt", "screens"),
    (1006, 1136, "SharedComponents.kt", "components"),
    (1137, 2040, "EventsScreen.kt", "screens"),
    (2041, 2755, "ForumScreen.kt", "screens"),
    (2756, 3102, "ProfileScreen.kt", "screens"),
    (3103, 3125, "SharedComponents.kt", "components"),
    (3126, 3535, "AiChatScreen.kt", "screens"),
    (3536, 3631, "LeaderboardScreen.kt", "screens"),
    (3632, 3718, "Top50ChatScreen.kt", "screens"),
    (3719, 3807, "StreamingPartyScreen.kt", "screens"),
    (3808, 3852, "VideoDashboardScreen.kt", "screens"),
    (3853, len(lines), "AdminPanelScreen.kt", "screens"),
]

# We need to collect the contents for each file
files_content = {}
for r in ranges:
    start_idx, end_idx, filename, folder = r
    # line numbers in grep are 1-based, list is 0-based
    chunk = lines[start_idx-1:end_idx]
    key = (folder, filename)
    if key not in files_content:
        files_content[key] = []
    files_content[key].extend(chunk)

# Now, we rewrite MainScreen.kt to only contain 0 to 247
main_new_content = lines[:247]

# Let's ensure directories exist
os.makedirs("app/src/main/java/com/example/ui/components", exist_ok=True)
os.makedirs("app/src/main/java/com/example/ui/screens", exist_ok=True)

for (folder, filename), content in files_content.items():
    filepath = f"app/src/main/java/com/example/ui/{folder}/{filename}"
    
    # generate header
    header = [f"package com.example.ui.{folder}\n\n"]
    header.extend(imports)
    header.append("\n")
    
    with open(filepath, "w") as f:
        f.writelines(header)
        f.writelines(content)

# We also need to add import for components to MainScreen
main_new_content_fixed = []
for line in main_new_content:
    if line.startswith("import com.example.ui.theme."):
        main_new_content_fixed.append(line)
        if "import com.example.ui.components.*" not in main_new_content_fixed:
            main_new_content_fixed.append("import com.example.ui.components.*\n")
    else:
        main_new_content_fixed.append(line)

with open(main_file, "w") as f:
    f.writelines(main_new_content_fixed)

print("Extraction complete")
