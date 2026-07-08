with open('app/src/main/java/com/example/ui/screens/AiChatScreen.kt', 'r') as f:
    content = f.read()

old_scroll = """    LaunchedEffect(chatMessages.size, chatLoading) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size)
        }
    }"""

new_scroll = """    LaunchedEffect(chatMessages.size, chatLoading) {
        if (chatMessages.isNotEmpty()) {
            val targetIndex = if (chatLoading) chatMessages.size else chatMessages.size - 1
            if (targetIndex >= 0) {
                listState.animateScrollToItem(targetIndex)
            }
        }
    }"""

content = content.replace(old_scroll, new_scroll)

with open('app/src/main/java/com/example/ui/screens/AiChatScreen.kt', 'w') as f:
    f.write(content)
