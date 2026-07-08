with open('app/src/main/java/com/example/ui/viewmodel/HeartsViewModel.kt', 'r') as f:
    lines = f.readlines()

new_lines = []
imports_to_move = []

for line in lines:
    if line.startswith('import android.util.Log') or line.startswith('import com.google.firebase.firestore.FirebaseFirestore') or line.startswith('import com.google.firebase.messaging.FirebaseMessaging'):
        imports_to_move.append(line)
    else:
        new_lines.append(line)

package_index = 0
for i, line in enumerate(new_lines):
    if line.startswith('package '):
        package_index = i
        break

for imp in reversed(imports_to_move):
    new_lines.insert(package_index + 1, imp)

with open('app/src/main/java/com/example/ui/viewmodel/HeartsViewModel.kt', 'w') as f:
    f.writelines(new_lines)
