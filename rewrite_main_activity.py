import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

replacement = """                    val viewModel: HeartsViewModel = viewModel(factory = HeartsViewModel.Factory)
                    
                    LaunchedEffect(Unit) {
                        val targetScreen = intent.getStringExtra("target_screen")
                        val targetId = intent.getStringExtra("target_id")
                        if (targetScreen != null) {
                            viewModel.handleNotificationIntent(targetScreen, targetId)
                        }
                    }

                    MainScreen(viewModel = viewModel)"""

content = content.replace("                    val viewModel: HeartsViewModel = viewModel(factory = HeartsViewModel.Factory)\n                    MainScreen(viewModel = viewModel)", replacement)

with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
    f.write(content)
