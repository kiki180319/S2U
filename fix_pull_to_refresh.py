with open('app/src/main/java/com/example/ui/screens/FeedScreen.kt', 'r') as f:
    content = f.read()

# Replace imports
content = content.replace("import androidx.compose.material3.pulltorefresh.PullToRefreshContainer\nimport androidx.compose.material3.pulltorefresh.rememberPullToRefreshState", "import androidx.compose.material3.pulltorefresh.PullToRefreshBox")

# Rewrite FeedScreen function
new_func = """@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(viewModel: HeartsViewModel) {
    val updates by viewModel.updates.collectAsStateWithLifecycle()
    var isRefreshing by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            scope.launch {
                isRefreshing = true
                delay(1500)
                isRefreshing = false
            }
        },
        modifier = Modifier.fillMaxSize().background(PremiumBlack)
    ) {
        if (updates.isEmpty() && !isRefreshing) {
            EmptyState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(updates, key = { it.id }) { update ->
                    UpdateCard(update = update, onClick = { /* TODO: Open detail/URL */ })
                }
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}"""

import re
content = re.sub(r'@OptIn\(ExperimentalMaterial3Api::class\)\n@Composable\nfun FeedScreen\(viewModel: HeartsViewModel\).*?(?=@Composable\nfun EmptyState)', new_func + '\n\n', content, flags=re.DOTALL)

with open('app/src/main/java/com/example/ui/screens/FeedScreen.kt', 'w') as f:
    f.write(content)
