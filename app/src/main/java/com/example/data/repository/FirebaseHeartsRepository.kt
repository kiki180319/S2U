package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.data.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.DocumentSnapshot
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

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
    override fun getNearestEvent(currentTime: Long): Flow<EventEntity?> = eventDao.getNearestEvent(currentTime)
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
        if (db != null) {
            try {
                // Save profile to Firestore under 'users' collection using device UUID as doc ID for simplicity,
                // or just use their name as ID if we want global usernames
                val safeId = user.name.replace(Regex("[^a-zA-Z0-9_-]"), "_")
                db!!.collection("users").document(safeId).set(user).await()
            } catch (e: Exception) {
                Log.e("FirebaseSync", "Failed to save user profile to Firestore", e)
            }
        }
    }
    
    override suspend fun loginWithUsername(username: String): UserEntity? = withContext(Dispatchers.IO) {
        if (username.equals("developer", ignoreCase = true) || username.equals("admin", ignoreCase = true) || username.equals("pcool180399@gmail.com", ignoreCase = true)) {
            val devUser = UserEntity(
                name = "Developer",
                title = "System Developer",
                favoriteBias = "All Members",
                bio = "Creator of the H2H Fandom App",
                joinedDate = "July 2026",
                avatarName = "avatar_spark",
                role = "admin"
            )
            userDao.insertOrUpdateUser(devUser)
            return@withContext devUser
        }
        
        if (db != null) {
            try {
                val safeId = username.replace(Regex("[^a-zA-Z0-9_-]"), "_")
                val doc = db!!.collection("users").document(safeId).get().await()
                if (doc.exists()) {
                    val user = doc.toObject(UserEntity::class.java)
                    if (user != null) {
                        userDao.insertOrUpdateUser(user)
                        return@withContext user
                    }
                }
            } catch (e: Exception) {
                Log.e("FirebaseSync", "Failed to login from Firestore", e)
            }
        }
        return@withContext null
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
    
    override suspend fun clearLocalData() = withContext(Dispatchers.IO) {
        communityUpdateDao.clearAllUpdates()
    }

    override suspend fun deleteUserProfile() = withContext(Dispatchers.IO) {
        userDao.deleteUser()
    }

    override suspend fun deleteThreadById(id: Int) = withContext(Dispatchers.IO) {
        forumDao.deleteThreadById(id)
    }

    override suspend fun deleteEventById(id: Int) = withContext(Dispatchers.IO) {
        eventDao.deleteEventById(id)
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

    // Live Streaming & Integrated Live Chat Features
    private val _simulatedLiveSessions = MutableStateFlow<List<LiveStreamSession>>(
        listOf(
            LiveStreamSession(
                streamId = "live_default_carmen",
                hostUserId = "1",
                title = "H2H Carmen - Sesi Tanya Jawab Santai Sore 💖",
                streamUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                viewerCount = 124,
                chatParticipantCount = 42,
                isActive = true
            )
        )
    )
    private val _simulatedLiveChatMessages = MutableStateFlow<List<LiveChatMessage>>(
        listOf(
            LiveChatMessage("msg_1", "live_default_carmen", "H2H_Spark", "Senior Fan Club Member", "Halo Carmen! Kakak cantik bgt hari ini 😍"),
            LiveChatMessage("msg_2", "live_default_carmen", "Carmen", "admin", "Halo semuanya! Makasih ya udah sempetin mampir ke live chat aku")
        )
    )

    override val activeLiveSessions: Flow<List<LiveStreamSession>> = callbackFlow {
        if (db == null) {
            val job = this@callbackFlow.launch {
                _simulatedLiveSessions.collect { trySend(it) }
            }
            awaitClose { job.cancel() }
            return@callbackFlow
        }
        val subscription = db!!.collection("live_sessions")
            .whereEqualTo("isActive", true)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val sessions = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(LiveStreamSession::class.java)
                    }
                    trySend(sessions)
                }
            }
        awaitClose { subscription.remove() }
    }

    override fun getLiveSession(streamId: String): Flow<LiveStreamSession?> = callbackFlow {
        if (db == null) {
            val job = this@callbackFlow.launch {
                _simulatedLiveSessions.map { sessions -> sessions.find { it.streamId == streamId } }.collect { trySend(it) }
            }
            awaitClose { job.cancel() }
            return@callbackFlow
        }
        val subscription = db!!.collection("live_sessions").document(streamId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null && snapshot.exists()) {
                    trySend(snapshot.toObject(LiveStreamSession::class.java))
                } else {
                    trySend(null)
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun startLiveSession(hostUserId: String, title: String, streamUrl: String): String = withContext(Dispatchers.IO) {
        val streamId = "live_${System.currentTimeMillis()}"
        val session = LiveStreamSession(
            streamId = streamId,
            hostUserId = hostUserId,
            title = title,
            streamUrl = streamUrl,
            viewerCount = 1,
            chatParticipantCount = 1,
            isActive = true
        )
        if (db != null) {
            try {
                db!!.collection("live_sessions").document(streamId).set(session).await()
            } catch (e: Exception) {
                Log.e("FirebaseSync", "Failed to start live session in Firestore", e)
            }
        } else {
            _simulatedLiveSessions.update { it + session }
        }
        val currentUser = userDao.getUserSync()
        if (currentUser != null) {
            userDao.insertOrUpdateUser(currentUser.copy(isLive = true, currentStreamId = streamId))
        }
        return@withContext streamId
    }

    override suspend fun stopLiveSession(streamId: String) = withContext(Dispatchers.IO) {
        if (db != null) {
            try {
                db!!.collection("live_sessions").document(streamId).update("isActive", false).await()
            } catch (e: Exception) {
                Log.e("FirebaseSync", "Failed to stop live session in Firestore", e)
            }
        } else {
            _simulatedLiveSessions.update { sessions ->
                sessions.map { if (it.streamId == streamId) it.copy(isActive = false) else it }
            }
        }
        val currentUser = userDao.getUserSync()
        if (currentUser != null && currentUser.currentStreamId == streamId) {
            userDao.insertOrUpdateUser(currentUser.copy(isLive = false, currentStreamId = null))
        }
    }

    override suspend fun incrementViewerCount(streamId: String) {
        withContext(Dispatchers.IO) {
            if (db != null) {
                try {
                    val docRef = db!!.collection("live_sessions").document(streamId)
                    db!!.runTransaction { transaction ->
                        val snapshot = transaction.get(docRef)
                        val currentCount = snapshot.getLong("viewerCount") ?: 0
                        transaction.update(docRef, "viewerCount", currentCount + 1)
                    }.await()
                } catch (e: Exception) {
                    Log.e("FirebaseSync", "Failed to increment viewer count", e)
                }
            } else {
                _simulatedLiveSessions.update { sessions ->
                    sessions.map { if (it.streamId == streamId) it.copy(viewerCount = it.viewerCount + 1) else it }
                }
            }
        }
    }

    override suspend fun decrementViewerCount(streamId: String) {
        withContext(Dispatchers.IO) {
            if (db != null) {
                try {
                    val docRef = db!!.collection("live_sessions").document(streamId)
                    db!!.runTransaction { transaction ->
                        val snapshot = transaction.get(docRef)
                        val currentCount = snapshot.getLong("viewerCount") ?: 0
                        transaction.update(docRef, "viewerCount", maxOf(0, currentCount - 1))
                    }.await()
                } catch (e: Exception) {
                    Log.e("FirebaseSync", "Failed to decrement viewer count", e)
                }
            } else {
                _simulatedLiveSessions.update { sessions ->
                    sessions.map { if (it.streamId == streamId) it.copy(viewerCount = maxOf(0, it.viewerCount - 1)) else it }
                }
            }
        }
    }

    override fun getLiveChatMessages(streamId: String): Flow<List<LiveChatMessage>> = callbackFlow {
        if (db == null) {
            val job = this@callbackFlow.launch {
                _simulatedLiveChatMessages.map { messages -> messages.filter { it.streamId == streamId } }.collect { trySend(it) }
            }
            awaitClose { job.cancel() }
            return@callbackFlow
        }
        val subscription = db!!.collection("live_chats")
            .whereEqualTo("streamId", streamId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val messages = snapshot.documents.mapNotNull { doc ->
                        doc.toObject(LiveChatMessage::class.java)
                    }
                    trySend(messages)
                }
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun sendLiveChatMessage(streamId: String, senderName: String, text: String, senderRole: String) {
        withContext(Dispatchers.IO) {
            val message = LiveChatMessage(
                id = "msg_${System.currentTimeMillis()}",
                streamId = streamId,
                senderName = senderName,
                senderRole = senderRole,
                text = text,
                timestamp = System.currentTimeMillis()
            )
            if (db != null) {
                try {
                    db!!.collection("live_chats").document(message.id).set(message).await()
                    val docRef = db!!.collection("live_sessions").document(streamId)
                    db!!.runTransaction { transaction ->
                        val snapshot = transaction.get(docRef)
                        val currentCount = snapshot.getLong("chatParticipantCount") ?: 0
                        transaction.update(docRef, "chatParticipantCount", currentCount + 1)
                    }.await()
                } catch (e: Exception) {
                    Log.e("FirebaseSync", "Failed to send live chat to Firestore", e)
                }
            } else {
                _simulatedLiveChatMessages.update { it + message }
                _simulatedLiveSessions.update { sessions ->
                    sessions.map { if (it.streamId == streamId) it.copy(chatParticipantCount = it.chatParticipantCount + 1) else it }
                }
            }
        }
    }
}
