package com.example.data.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

/**
 * Skema Data Firestore untuk Postingan Forum Komunitas (Post).
 */
data class FirestorePost(
    @DocumentId val id: String = "",
    val title: String = "",
    val content: String = "",
    val author: String = "",
    val authorTitle: String = "",
    val category: String = "General",
    val upvotes: Int = 0,
    val stickerId: String? = null, // ID Stiker valid dari whitelist (opsional)
    val stickerUrl: String? = null, // URL Stiker dari Firebase Storage
    @ServerTimestamp val timestamp: Date? = null
)

/**
 * Skema Data Firestore untuk Komentar (Inline Comments).
 */
data class FirestoreComment(
    @DocumentId val id: String = "",
    val postId: String = "",
    val author: String = "",
    val authorTitle: String = "",
    val content: String = "",
    val stickerId: String? = null, // ID Stiker valid dari whitelist (opsional)
    val stickerUrl: String? = null, // URL Stiker dari Firebase Storage
    @ServerTimestamp val timestamp: Date? = null
)

/**
 * Skema Data Firestore untuk Chat (Live Chat).
 */
data class FirestoreChat(
    @DocumentId val id: String = "",
    val senderName: String = "",
    val senderAvatar: String = "",
    val senderTitle: String = "",
    val messageText: String = "",
    val stickerId: String? = null, // ID Stiker valid dari whitelist (opsional)
    val stickerUrl: String? = null, // URL Stiker dari Firebase Storage
    @ServerTimestamp val timestamp: Date? = null
)
