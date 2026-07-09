package com.example.data.repository

import com.example.data.database.*
import kotlinx.coroutines.flow.Flow

interface IHeartsRepository {
    val userProfile: Flow<UserEntity?>
    val allEvents: Flow<List<EventEntity>>
    fun getNearestEvent(currentTime: Long): Flow<EventEntity?>
    val allThreads: Flow<List<ForumEntity>>
    val allVideos: Flow<List<VideoEntity>>
    val allUpdates: Flow<List<CommunityUpdateEntity>>

    fun getCommentsForEvent(eventId: Int): Flow<List<EventCommentEntity>>
    fun getAttendeesForEvent(eventId: Int): Flow<List<EventAttendeeEntity>>
    fun getCommentsForThread(threadId: Int): Flow<List<CommentEntity>>
    fun getThreadById(threadId: Int): Flow<ForumEntity?>

    suspend fun saveUserProfile(user: UserEntity)
    suspend fun addEvent(event: EventEntity)
    suspend fun updateEvent(event: EventEntity)
    suspend fun addEventComment(comment: EventCommentEntity)
    suspend fun addEventAttendee(attendee: EventAttendeeEntity)
    suspend fun removeEventAttendee(eventId: Int, userName: String)
    suspend fun addThread(thread: ForumEntity)
    suspend fun updateThread(thread: ForumEntity)
    suspend fun addComment(comment: CommentEntity)
    suspend fun updateVideo(video: VideoEntity)
    suspend fun addUpdate(update: CommunityUpdateEntity)
    suspend fun addUpdates(updates: List<CommunityUpdateEntity>)
    suspend fun clearLocalData()
    suspend fun deleteUserProfile()
    suspend fun deleteThreadById(id: Int)
    suspend fun deleteEventById(id: Int)
    suspend fun prepopulateIfEmpty()

    // Pagination & Refresh for Firebase Optimization
    suspend fun loadMoreThreads()
    suspend fun refreshThreads()
    suspend fun loadMoreEvents()
    suspend fun refreshEvents()

    // Live Streaming & Integrated Live Chat Features
    val activeLiveSessions: Flow<List<LiveStreamSession>>
    fun getLiveSession(streamId: String): Flow<LiveStreamSession?>
    suspend fun startLiveSession(hostUserId: String, title: String, streamUrl: String): String
    suspend fun stopLiveSession(streamId: String)
    suspend fun incrementViewerCount(streamId: String)
    suspend fun decrementViewerCount(streamId: String)
    fun getLiveChatMessages(streamId: String): Flow<List<LiveChatMessage>>
    suspend fun sendLiveChatMessage(streamId: String, senderName: String, text: String, senderRole: String)
}
