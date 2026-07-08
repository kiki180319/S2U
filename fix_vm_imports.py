with open('app/src/main/java/com/example/ui/viewmodel/HeartsViewModel.kt', 'r') as f:
    lines = f.readlines()

imports_to_move = []
new_lines = []
for line in lines:
    if line.startswith('import ') and 'class HeartsViewModel' not in line:
        imports_to_move.append(line)
    elif 'import ' in line and 'class ' not in line:
        imports_to_move.append(line)
    elif line.startswith('package '):
        new_lines.append(line)
    else:
        new_lines.append(line)

final_lines = [new_lines[0]] + imports_to_move + new_lines[1:]

# Let's just do it simpler: find all lines starting with import, move them to the top
cleaned_lines = []
imports = []
for line in lines:
    stripped = line.strip()
    if stripped.startswith('import '):
        imports.append(line)
    else:
        cleaned_lines.append(line)

# find where package is
pkg_idx = -1
for i, line in enumerate(cleaned_lines):
    if line.startswith('package '):
        pkg_idx = i
        break

if pkg_idx != -1:
    pkg = cleaned_lines.pop(pkg_idx)
    cleaned_lines = [pkg] + imports + cleaned_lines
else:
    cleaned_lines = imports + cleaned_lines

with open('app/src/main/java/com/example/ui/viewmodel/HeartsViewModel.kt', 'w') as f:
    f.writelines(cleaned_lines)

