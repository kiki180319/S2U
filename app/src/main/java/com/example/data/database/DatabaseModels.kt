package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_profile")
data class UserEntity(
    @PrimaryKey val id: Int = 1,
    val deviceUuid: String = "",
    val name: String,
    val title: String,
    val favoriteBias: String,
    val bio: String,
    val joinedDate: String,
    val avatarName: String = "avatar_spark",
    val twitterUrl: String? = null,
    val instagramUrl: String? = null,
    val youtubeUrl: String? = null,
    val tiktokUrl: String? = null,
    val role: String = "user"
)

@Entity(tableName = "event_comments")
data class EventCommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventId: Int,
    val author: String,
    val authorTitle: String,
    val authorAvatar: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "event_attendees")
data class EventAttendeeEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val eventId: Int,
    val userName: String,
    val userTitle: String,
    val userAvatar: String
)

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String,
    val category: String, // "Livestream", "Gathering", "Project"
    val date: String,
    val location: String,
    val organizer: String,
    val joinedCount: Int = 0,
    val isJoined: Boolean = false,
    val customFormUrl: String? = null
)

@Entity(tableName = "forum_threads")
data class ForumEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val content: String,
    val author: String,
    val authorTitle: String,
    val category: String, // "Fan Art", "Streaming", "General", "Gathering"
    val upvotes: Int = 0,
    val isUpvoted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val threadId: Int,
    val author: String,
    val authorTitle: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey val videoId: String,
    val title: String,
    val description: String,
    val channelName: String,
    val thumbnailUrl: String,
    val duration: String,
    val isFavorite: Boolean = false
)

@Entity(tableName = "community_updates")
data class CommunityUpdateEntity(
    @PrimaryKey val id: String,
    val type: String, // "video", "announcement", "live"
    val title: String,
    val content: String,
    val timestamp: Long,
    val sourceUrl: String? = null,
    val thumbnailUrl: String? = null,
    val isPinned: Boolean = false
)
