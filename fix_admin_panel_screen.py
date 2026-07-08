import sys

with open('app/src/main/java/com/example/ui/screens/MainScreen.kt', 'r') as f:
    content = f.read()

replacement = """@Composable
fun AdminPanelScreen(viewModel: HeartsViewModel) {
    val isAdmin by viewModel.isAdmin.collectAsStateWithLifecycle()
    if (!isAdmin) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Lock, contentDescription = "Locked", tint = PremiumMediumGray, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Access Denied", color = PremiumWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("You do not have permission to view the Admin Panel.", color = PremiumLightGray, textAlign = TextAlign.Center)
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {"""

content = content.replace("""@Composable
fun AdminPanelScreen(viewModel: HeartsViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {""", replacement)

with open('app/src/main/java/com/example/ui/screens/MainScreen.kt', 'w') as f:
    f.write(content)
