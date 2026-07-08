import re

with open('app/src/main/java/com/example/ui/viewmodel/HeartsViewModel.kt', 'r') as f:
    content = f.read()

# Add imports
imports = """import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import android.util.Log\n"""

# We just insert them after package declaration
content = content.replace("package com.example.ui.viewmodel\n\n", "package com.example.ui.viewmodel\n\n" + imports)

# Add init block to fetch token
init_block = """    init {
        fetchAndSaveFcmToken()
    }

    private fun fetchAndSaveFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("HeartsViewModel", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            val db = FirebaseFirestore.getInstance()
            val data = hashMapOf(
                "token" to token,
                "timestamp" to System.currentTimeMillis()
            )
            db.collection("user_tokens").document(token)
                .set(data)
                .addOnSuccessListener { Log.d("HeartsViewModel", "Token saved to Firestore initially") }
        }
    }

    fun handleNotificationIntent(targetScreen: String, targetId: String?) {
        selectTab(targetScreen)
        if (targetId != null) {
            when (targetScreen) {
                "forum" -> {
                    val threadId = targetId.toIntOrNull()
                    if (threadId != null) {
                        viewModelScope.launch {
                            // Let's delay slightly to ensure threads are loaded if not already
                            kotlinx.coroutines.delay(500)
                            val thread = threads.value.find { it.id == threadId }
                            if (thread != null) {
                                selectThread(thread)
                            }
                        }
                    }
                }
                "events" -> {
                    val eventId = targetId.toIntOrNull()
                    if (eventId != null) {
                        viewModelScope.launch {
                            kotlinx.coroutines.delay(500)
                            val event = events.value.find { it.id == eventId }
                            if (event != null) {
                                selectEvent(event)
                            }
                        }
                    }
                }
            }
        }
    }
"""
# insert before `// Community Unified Updates Feed State`
content = content.replace("    // Community Unified Updates Feed State", init_block + "\n    // Community Unified Updates Feed State")

# Modify postComment to add Firestore logic
post_comment_orig = """    // 3. Post a comment to thread
    fun postComment(threadId: Int, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            val user = userProfile.value
            repository.addComment(
                CommentEntity(
                    threadId = threadId,
                    author = user?.name ?: "H2H_Spark",
                    authorTitle = user?.title ?: "Senior Fan Club Member",
                    content = content
                )
            )
        }
    }"""

post_comment_new = """    // 3. Post a comment to thread
    fun postComment(threadId: Int, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            val user = userProfile.value
            val authorName = user?.name ?: "H2H_Spark"
            
            repository.addComment(
                CommentEntity(
                    threadId = threadId,
                    author = authorName,
                    authorTitle = user?.title ?: "Senior Fan Club Member",
                    content = content
                )
            )
            
            // Trigger automatic notification by writing to Firestore
            // Finds the original thread owner
            val thread = threads.value.find { it.id == threadId }
            if (thread != null && thread.author != authorName) {
                val db = FirebaseFirestore.getInstance()
                val notificationData = hashMapOf(
                    "targetUser" to thread.author,
                    "senderName" to authorName,
                    "threadId" to threadId.toString(),
                    "title" to "Balasan Baru di Forum",
                    "body" to "$authorName mengomentari postingan Anda: '${thread.title}'",
                    "target_screen" to "forum",
                    "target_id" to threadId.toString(),
                    "timestamp" to System.currentTimeMillis()
                )
                db.collection("forum_notifications").add(notificationData)
                    .addOnSuccessListener { Log.d("HeartsViewModel", "Trigger notification written to Firestore") }
                    .addOnFailureListener { e -> Log.e("HeartsViewModel", "Error writing trigger", e) }
            }
        }
    }"""

content = content.replace(post_comment_orig, post_comment_new)

with open('app/src/main/java/com/example/ui/viewmodel/HeartsViewModel.kt', 'w') as f:
    f.write(content)
