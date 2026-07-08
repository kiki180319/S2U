with open('app/src/main/java/com/example/ui/screens/ProfileScreen.kt', 'r') as f:
    content = f.read()

# Replace the loading block
old_loading_block = """    if (userProfile == null) {
        Box(modifier = Modifier.fillMaxSize().background(PremiumBlack), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = HeartsPink)
        }
        return
    }"""

new_loading_block = """    val isLoading by viewModel.isProfileLoading.collectAsStateWithLifecycle()
    val profileError by viewModel.profileError.collectAsStateWithLifecycle()

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(PremiumBlack), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = HeartsPink)
        }
        return
    }

    if (userProfile == null || profileError != null) {
        Box(modifier = Modifier.fillMaxSize().background(PremiumBlack), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Not Logged In",
                    tint = PremiumMediumGray,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = profileError ?: "Silakan login untuk melihat profil Anda.",
                    color = PremiumWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = { /* TODO: Implement Login Action */ },
                    colors = ButtonDefaults.buttonColors(containerColor = HeartsPink)
                ) {
                    Text("Login / Buat Profil", color = PremiumWhite)
                }
            }
        }
        return
    }"""

content = content.replace(old_loading_block, new_loading_block)

with open('app/src/main/java/com/example/ui/screens/ProfileScreen.kt', 'w') as f:
    f.write(content)
