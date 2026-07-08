package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class FirebaseHeartsRepository(
    private val context: Context,
    private val userDao: UserDao,
    private val eventDao: EventDao,
    private val forumDao: ForumDao,
    private val commentDao: CommentDao,
    private val videoDao: VideoDao,
    private val eventCommentDao: EventCommentDao,
    private val eventAttendeeDao: EventAttendeeDao,
    private val communityUpdateDao: CommunityUpdateDao
) : IHeartsRepository {

    private val db: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e("FirebaseSync", "Firebase not initialized. Add google-services.json", e)
            null
        }
    }
    
    private val prefs: SharedPreferences = context.getSharedPreferences("hearts_sync_prefs", Context.MODE_PRIVATE)
    
    // Pagination cursors
    private var lastThreadSnapshot: DocumentSnapshot? = null
    private var lastEventSnapshot: DocumentSnapshot? = null
    
    // 1. Caching lokal (Room sebagai cache layer)
    // Data selalu dibaca dari Room untuk menghemat kuota Firestore
    override val userProfile: Flow<UserEntity?> = userDao.getUser()
    override val allEvents: Flow<List<EventEntity>> = eventDao.getEvents()
    override val allThreads: Flow<List<ForumEntity>> = forumDao.getThreads()
    override val allVideos: Flow<List<VideoEntity>> = videoDao.getVideos()
    override val allUpdates: Flow<List<CommunityUpdateEntity>> = communityUpdateDao.getUpdates()

    override fun getCommentsForEvent(eventId: Int): Flow<List<EventCommentEntity>> = eventCommentDao.getCommentsForEvent(eventId)
    override fun getAttendeesForEvent(eventId: Int): Flow<List<EventAttendeeEntity>> = eventAttendeeDao.getAttendeesForEvent(eventId)
    
    // 3. Batasi real-time listener (snapshotListener)
    // One-time fetch (get) via Flow from Room. Jika butuh real-time (misal Streaming Party chat),
    // UI bisa men-trigger listener khusus yang di-detach saat keluar.
    override fun getCommentsForThread(threadId: Int): Flow<List<CommentEntity>> = commentDao.getCommentsForThread(threadId)
    
    override fun getThreadById(threadId: Int): Flow<ForumEntity?> = forumDao.getThreadById(threadId)
    
    // 2. Simpan timestamp "last synced" per collection
    private fun isCacheExpired(collectionKey: String, expireHours: Int = 24): Boolean {
        val lastSync = prefs.getLong(collectionKey, 0)
        return (System.currentTimeMillis() - lastSync) > (expireHours * 60 * 60 * 1000)
    }
    
    private fun updateCacheTimestamp(collectionKey: String) {
        prefs.edit().putLong(collectionKey, System.currentTimeMillis()).apply()
    }

    override suspend fun refreshThreads() = withContext(Dispatchers.IO) {
        if (db == null) return@withContext
        Log.d("FirebaseSync", "[Monitoring] Firestore Read: Fetching latest threads (limit 20)")
        try {
            val snapshot = db!!.collection("threads")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .limit(20)
                .get().await()
                
            if (!snapshot.isEmpty) {
                lastThreadSnapshot = snapshot.documents.last()
                val threads = snapshot.documents.mapNotNull { doc -> 
                    try { doc.toObject(ForumEntity::class.java)?.copy(id = doc.id.hashCode()) } catch(e: Exception) { null }
                }
                threads.forEach { forumDao.insertThread(it) }
                updateCacheTimestamp("threads_sync")
            }
        } catch (e: Exception) {
            Log.e("FirebaseSync", "refreshThreads failed", e)
        }
    }

    override suspend fun loadMoreThreads() = withContext(Dispatchers.IO) {
        if (db == null || lastThreadSnapshot == null) return@withContext
        Log.d("FirebaseSync", "[Monitoring] Firestore Read: Fetching NEXT 20 threads (Pagination with startAfter)")
        try {
            val snapshot = db!!.collection("threads")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .startAfter(lastThreadSnapshot!!)
                .limit(20)
                .get().await()
                
            if (!snapshot.isEmpty) {
                lastThreadSnapshot = snapshot.documents.last()
                val threads = snapshot.documents.mapNotNull { doc -> 
                    try { doc.toObject(ForumEntity::class.java)?.copy(id = doc.id.hashCode()) } catch(e: Exception) { null }
                }
                threads.forEach { forumDao.insertThread(it) }
            }
        } catch (e: Exception) {
            Log.e("FirebaseSync", "loadMoreThreads failed", e)
        }
    }

    override suspend fun refreshEvents() = withContext(Dispatchers.IO) {
        if (db == null) return@withContext
        Log.d("FirebaseSync", "[Monitoring] Firestore Read: Fetching latest events (limit 15)")
        try {
            val snapshot = db!!.collection("events")
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(15)
                .get().await()
                
            if (!snapshot.isEmpty) {
                lastEventSnapshot = snapshot.documents.last()
                val events = snapshot.documents.mapNotNull { doc -> 
                    try { doc.toObject(EventEntity::class.java)?.copy(id = doc.id.hashCode()) } catch(e: Exception) { null }
                }
                events.forEach { eventDao.insertEvent(it) }
                updateCacheTimestamp("events_sync")
            }
        } catch (e: Exception) {
            Log.e("FirebaseSync", "refreshEvents failed", e)
        }
    }

    override suspend fun loadMoreEvents() = withContext(Dispatchers.IO) {
        if (db == null || lastEventSnapshot == null) return@withContext
        Log.d("FirebaseSync", "[Monitoring] Firestore Read: Fetching NEXT 15 events (Pagination with startAfter)")
        try {
            val snapshot = db!!.collection("events")
                .orderBy("date", Query.Direction.DESCENDING)
                .startAfter(lastEventSnapshot!!)
                .limit(15)
                .get().await()
                
            if (!snapshot.isEmpty) {
                lastEventSnapshot = snapshot.documents.last()
                val events = snapshot.documents.mapNotNull { doc -> 
                    try { doc.toObject(EventEntity::class.java)?.copy(id = doc.id.hashCode()) } catch(e: Exception) { null }
                }
                events.forEach { eventDao.insertEvent(it) }
            }
        } catch (e: Exception) {
            Log.e("FirebaseSync", "loadMoreEvents failed", e)
        }
    }

    override suspend fun addThread(thread: ForumEntity) = withContext(Dispatchers.IO) {
        forumDao.insertThread(thread) // Update local cache instantly
        
        if (db != null) {
            Log.d("FirebaseSync", "[Monitoring] Firestore Write: Adding new thread")
            try {
                db!!.collection("threads").document(thread.id.toString()).set(thread).await()
            } catch (e: Exception) {
                Log.e("FirebaseSync", "addThread to Firestore failed", e)
            }
        }
    }
    
    // Default implementation for other required interface methods
    override suspend fun saveUserProfile(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.insertOrUpdateUser(user)
    }
    
    override suspend fun addEvent(event: EventEntity) = withContext(Dispatchers.IO) {
        eventDao.insertEvent(event)
    }
    
    override suspend fun updateEvent(event: EventEntity) = withContext(Dispatchers.IO) {
        eventDao.updateEvent(event)
    }
    
    override suspend fun addEventComment(comment: EventCommentEntity) = withContext(Dispatchers.IO) {
        eventCommentDao.insertComment(comment)
    }
    
    override suspend fun addEventAttendee(attendee: EventAttendeeEntity) = withContext(Dispatchers.IO) {
        eventAttendeeDao.insertAttendee(attendee)
    }
    
    override suspend fun removeEventAttendee(eventId: Int, userName: String) = withContext(Dispatchers.IO) {
        eventAttendeeDao.removeAttendee(eventId, userName)
    }
    
    override suspend fun updateThread(thread: ForumEntity) = withContext(Dispatchers.IO) {
        forumDao.updateThread(thread)
    }
    
    override suspend fun addComment(comment: CommentEntity) = withContext(Dispatchers.IO) {
        commentDao.insertComment(comment)
    }
    
    override suspend fun updateVideo(video: VideoEntity) = withContext(Dispatchers.IO) {
        videoDao.updateVideo(video)
    }
    
    override suspend fun addUpdate(update: CommunityUpdateEntity) = withContext(Dispatchers.IO) {
        communityUpdateDao.insertUpdate(update)
    }
    
    override suspend fun addUpdates(updates: List<CommunityUpdateEntity>) = withContext(Dispatchers.IO) {
        updates.forEach { communityUpdateDao.insertUpdate(it) }
    }
    
    override suspend fun prepopulateIfEmpty() = withContext(Dispatchers.IO) {
        // Will check Firestore cache limit before deciding to prepopulate from local defaults
        if (isCacheExpired("threads_sync")) {
            refreshThreads()
        }
        if (isCacheExpired("events_sync")) {
            refreshEvents()
        }
    }
}
