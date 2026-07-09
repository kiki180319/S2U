package com.example.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    fun getUser(): Flow<UserEntity?>
    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")
    suspend fun getUserSync(): UserEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateUser(user: UserEntity)

    @Query("DELETE FROM user_profile WHERE id = 1")
    suspend fun deleteUser()
}

@Dao
interface EventDao {
    @Query("SELECT * FROM events ORDER BY id DESC")
    fun getEvents(): Flow<List<EventEntity>>
    @Query("SELECT * FROM events ORDER BY id DESC")
    suspend fun getEventsSync(): List<EventEntity>

    @Query("SELECT * FROM events WHERE timestamp >= :currentTime ORDER BY timestamp ASC LIMIT 1")
    fun getNearestEvent(currentTime: Long): Flow<EventEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: EventEntity)

    @Update
    suspend fun updateEvent(event: EventEntity)

    @Query("DELETE FROM events WHERE id = :id")
    suspend fun deleteEventById(id: Int)
}

@Dao
interface ForumDao {
    @Query("SELECT * FROM forum_threads ORDER BY timestamp DESC")
    fun getThreads(): Flow<List<ForumEntity>>
    @Query("SELECT * FROM forum_threads ORDER BY timestamp DESC")
    suspend fun getThreadsSync(): List<ForumEntity>

    @Query("SELECT * FROM forum_threads WHERE id = :id LIMIT 1")
    fun getThreadById(id: Int): Flow<ForumEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThread(thread: ForumEntity)
    
    @Query("DELETE FROM forum_threads WHERE id = :id")
    suspend fun deleteThreadById(id: Int)

    @Update
    suspend fun updateThread(thread: ForumEntity)
}

@Dao
interface CommentDao {
    @Query("SELECT * FROM comments WHERE threadId = :threadId ORDER BY timestamp ASC")
    fun getCommentsForThread(threadId: Int): Flow<List<CommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: CommentEntity)
}

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos")
    fun getVideos(): Flow<List<VideoEntity>>
    @Query("SELECT * FROM videos")
    suspend fun getVideosSync(): List<VideoEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity)

    @Update
    suspend fun updateVideo(video: VideoEntity)
}

@Dao
interface EventCommentDao {
    @Query("SELECT * FROM event_comments WHERE eventId = :eventId ORDER BY timestamp ASC")
    fun getCommentsForEvent(eventId: Int): Flow<List<EventCommentEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertComment(comment: EventCommentEntity)
}

@Dao
interface EventAttendeeDao {
    @Query("SELECT * FROM event_attendees WHERE eventId = :eventId")
    fun getAttendeesForEvent(eventId: Int): Flow<List<EventAttendeeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttendee(attendee: EventAttendeeEntity)

    @Query("DELETE FROM event_attendees WHERE eventId = :eventId AND userName = :userName")
    suspend fun removeAttendee(eventId: Int, userName: String)
}

@Dao
interface CommunityUpdateDao {
    @Query("SELECT * FROM community_updates ORDER BY timestamp DESC")
    fun getUpdates(): Flow<List<CommunityUpdateEntity>>
    @Query("SELECT * FROM community_updates ORDER BY timestamp DESC")
    suspend fun getUpdatesSync(): List<CommunityUpdateEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdate(update: CommunityUpdateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdates(updates: List<CommunityUpdateEntity>)

    @Query("DELETE FROM community_updates WHERE id = :id")
    suspend fun deleteUpdateById(id: String)

    @Query("DELETE FROM community_updates")
    suspend fun clearAllUpdates()
}

