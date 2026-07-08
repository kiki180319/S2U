import re

with open('app/src/main/java/com/example/MainActivity.kt', 'r') as f:
    content = f.read()

# We need to hold a reference to viewModel in MainActivity or pass it.
# Actually, viewModel() inside Compose is tied to the Activity. We can get the same instance in the Activity.
# Alternatively, in onNewIntent we can just put extra to the Activity's intent, but Compose might not re-read it.
# So we can do:
replacement = """    private var sharedViewModel: HeartsViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val factory = HeartsViewModel.Factory
        val vm = androidx.lifecycle.ViewModelProvider(this, factory).get(HeartsViewModel::class.java)
        sharedViewModel = vm
        
        // Handle initial intent
        handleIntent(intent)

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        // Dynamic permission callback
                    }
                    LaunchedEffect(Unit) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    android.Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    }
                    
                    MainScreen(viewModel = vm)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val targetScreen = intent?.getStringExtra("target_screen")
        val targetId = intent?.getStringExtra("target_id")
        if (targetScreen != null) {
            sharedViewModel?.handleNotificationIntent(targetScreen, targetId)
        }
    }"""

# we need to replace the onCreate function.
# Let's just find `override fun onCreate(savedInstanceState: Bundle?) {` and replace until `checkNotificationPermission()`
start_idx = content.find('    override fun onCreate(savedInstanceState: Bundle?) {')
end_idx = content.find('    /**\n     * Memeriksa apakah Notification Listener Service')

if start_idx != -1 and end_idx != -1:
    new_content = content[:start_idx] + replacement + "\n\n" + content[end_idx:]
    with open('app/src/main/java/com/example/MainActivity.kt', 'w') as f:
        f.write(new_content)
else:
    print("Could not find boundaries")
