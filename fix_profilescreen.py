import re

file_path = "app/src/main/java/com/example/ui/screens/ProfileScreen.kt"

with open(file_path, "r") as f:
    content = f.read()

# Add showLoginDialog state
content = content.replace("var showCreateProfile by remember { mutableStateOf(false) }",
                          "var showCreateProfile by remember { mutableStateOf(false) }\n    var showLoginDialog by remember { mutableStateOf(false) }")

# Change buttons in the not-logged-in section
old_buttons = """                Button(
                    onClick = { showCreateProfile = true },
                    colors = ButtonDefaults.buttonColors(containerColor = HeartsPink)
                ) {
                    Text("Login / Buat Profil", color = PremiumWhite)
                }"""

new_buttons = """                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { showLoginDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PremiumDarkGray)
                    ) {
                        Text("Login", color = PremiumWhite)
                    }
                    Button(
                        onClick = { showCreateProfile = true },
                        colors = ButtonDefaults.buttonColors(containerColor = HeartsPink)
                    ) {
                        Text("Buat Profil Baru", color = PremiumWhite)
                    }
                }"""
content = content.replace(old_buttons, new_buttons)

# Add LoginDialog composable usage
login_dialog_usage = """        if (showLoginDialog) {
            LoginDialog(
                onDismiss = { showLoginDialog = false },
                onLogin = { username ->
                    viewModel.login(username)
                    showLoginDialog = false
                }
            )
        }
        
        if (showCreateProfile) {"""
content = content.replace("        if (showCreateProfile) {", login_dialog_usage)

# Add LoginDialog composable at the bottom
login_dialog_comp = """
@Composable
fun LoginDialog(
    onDismiss: () -> Unit,
    onLogin: (String) -> Unit
) {
    var username by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PremiumDarkGray,
        title = { Text("Login", color = PremiumWhite, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Masukkan username Anda:", color = PremiumLightGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username", color = PremiumLightGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HeartsPink,
                        unfocusedBorderColor = PremiumMediumGray,
                        focusedTextColor = PremiumWhite,
                        unfocusedTextColor = PremiumWhite
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Developer? Gunakan username 'developer' atau 'pcool180399@gmail.com'.", color = PremiumMediumGray, fontSize = 11.sp)
            }
        },
        confirmButton = {
            Button(
                onClick = { if (username.isNotBlank()) onLogin(username.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = HeartsPink)
            ) {
                Text("Masuk", color = PremiumWhite)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = PremiumLightGray)
            }
        }
    )
}
"""

content = content + "\n" + login_dialog_comp

with open(file_path, "w") as f:
    f.write(content)
    print("Updated ProfileScreen")
