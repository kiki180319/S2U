package com.example.data.repository

import com.example.data.database.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class HeartsRepository(


    private val userDao: UserDao,
    private val eventDao: EventDao,
    private val forumDao: ForumDao,
    private val commentDao: CommentDao,
    private val videoDao: VideoDao,
    private val eventCommentDao: EventCommentDao,
    private val eventAttendeeDao: EventAttendeeDao,
    private val communityUpdateDao: CommunityUpdateDao
) : IHeartsRepository {
    override val userProfile: Flow<UserEntity?> = userDao.getUser()
    override val allEvents: Flow<List<EventEntity>> = eventDao.getEvents()
    override fun getNearestEvent(currentTime: Long): Flow<EventEntity?> = eventDao.getNearestEvent(currentTime)
    override val allThreads: Flow<List<ForumEntity>> = forumDao.getThreads()
    override val allVideos: Flow<List<VideoEntity>> = videoDao.getVideos()
    override val allUpdates: Flow<List<CommunityUpdateEntity>> = communityUpdateDao.getUpdates()

    override suspend fun addUpdate(update: CommunityUpdateEntity) = withContext(Dispatchers.IO) {
        communityUpdateDao.insertUpdate(update)
    }

    override suspend fun addUpdates(updates: List<CommunityUpdateEntity>) = withContext(Dispatchers.IO) {
        communityUpdateDao.insertUpdates(updates)
    }

    suspend fun deleteUpdateById(id: String) = withContext(Dispatchers.IO) {
        communityUpdateDao.deleteUpdateById(id)
    }

    suspend fun clearAllUpdates() = withContext(Dispatchers.IO) {
        communityUpdateDao.clearAllUpdates()
    }

    override fun getCommentsForEvent(eventId: Int): Flow<List<EventCommentEntity>> =
        eventCommentDao.getCommentsForEvent(eventId)

    override suspend fun addEventComment(comment: EventCommentEntity) = withContext(Dispatchers.IO) {
        eventCommentDao.insertComment(comment)
    }

    override fun getAttendeesForEvent(eventId: Int): Flow<List<EventAttendeeEntity>> =
        eventAttendeeDao.getAttendeesForEvent(eventId)

    override suspend fun addEventAttendee(attendee: EventAttendeeEntity) = withContext(Dispatchers.IO) {
        eventAttendeeDao.insertAttendee(attendee)
    }

    override suspend fun removeEventAttendee(eventId: Int, userName: String) = withContext(Dispatchers.IO) {
        eventAttendeeDao.removeAttendee(eventId, userName)
    }

    override fun getCommentsForThread(threadId: Int): Flow<List<CommentEntity>> =
        commentDao.getCommentsForThread(threadId)

    override fun getThreadById(threadId: Int): Flow<ForumEntity?> =
        forumDao.getThreadById(threadId)

    override suspend fun saveUserProfile(user: UserEntity) = withContext(Dispatchers.IO) {
        userDao.insertOrUpdateUser(user)
    }

    override suspend fun addEvent(event: EventEntity) = withContext(Dispatchers.IO) {
        eventDao.insertEvent(event)
    }

    override suspend fun updateEvent(event: EventEntity) = withContext(Dispatchers.IO) {
        eventDao.updateEvent(event)
    }

    override suspend fun deleteEventById(id: Int) = withContext(Dispatchers.IO) {
        eventDao.deleteEventById(id)
    }

    override suspend fun addThread(thread: ForumEntity) = withContext(Dispatchers.IO) {
        forumDao.insertThread(thread)
    }

    override suspend fun updateThread(thread: ForumEntity) = withContext(Dispatchers.IO) {
        forumDao.updateThread(thread)
    }

    override suspend fun addComment(comment: CommentEntity) = withContext(Dispatchers.IO) {
        commentDao.insertComment(comment)
    }

    suspend fun addVideo(video: VideoEntity) = withContext(Dispatchers.IO) {
        videoDao.insertVideo(video)
    }

    override suspend fun updateVideo(video: VideoEntity) = withContext(Dispatchers.IO) {
        videoDao.updateVideo(video)
    }

    // Prepopulate database with complete mock data if empty
    override suspend fun clearLocalData() = withContext(Dispatchers.IO) {
        communityUpdateDao.clearAllUpdates()
        // we can clear others if we want, but let's just clear updates for now
    }

    override suspend fun deleteUserProfile() = withContext(Dispatchers.IO) {
        userDao.deleteUser()
    }

    override suspend fun deleteThreadById(id: Int) = withContext(Dispatchers.IO) {
        forumDao.deleteThreadById(id)
    }

    override suspend fun prepopulateIfEmpty() = withContext(Dispatchers.IO) {
        // 1. User profile
        val user = userDao.getUserSync()
        if (user == null) {
            userDao.insertOrUpdateUser(
                UserEntity(
                    id = 1,
                    deviceUuid = java.util.UUID.randomUUID().toString(),
                    name = "H2H_Spark",
                    title = "Senior Fan Club Member",
                    favoriteBias = "Jiwoo (Leader)",
                    bio = "Pecinta musik Hearts2Hearts sejak rilis single pertama! Senang membantu koordinasi gathering regional.",
                    joinedDate = "July 2026"
                )
            )
        }

        // 2. Videos
        val videos = videoDao.getVideosSync()
        if (videos.isNullOrEmpty()) {
            val defaultVideos = listOf(
                VideoEntity(
                    videoId = "dQw4w9WgXcQ",
                    title = "[OFFICIAL MV] Hearts2Hearts - Connection (Official Music Video)",
                    description = "Single utama dari album perdana Hearts2Hearts. Lagu ini didekasikan khusus untuk seluruh fans yang setia menemani perjuangan dari awal.",
                    channelName = "Hearts2Hearts Official",
                    thumbnailUrl = "https://img.youtube.com/vi/dQw4w9WgXcQ/0.jpg",
                    duration = "03:45",
                    isFavorite = true
                ),
                VideoEntity(
                    videoId = "9bZkp7q19f0",
                    title = "[LIVE PERFORMANCE] Hearts2Hearts - Melody of You (Acoustic Ver.)",
                    description = "Versi akustik spesial yang dibawakan langsung dari studio utama. Alunan melodi lembut yang menghangatkan hati pendengar.",
                    channelName = "Hearts2Hearts Official",
                    thumbnailUrl = "https://img.youtube.com/vi/9bZkp7q19f0/0.jpg",
                    duration = "04:12",
                    isFavorite = false
                ),
                VideoEntity(
                    videoId = "g6fPzYm-eNo",
                    title = "[VLOG] H2H Behind The Scene MV Shooting Day 1!",
                    description = "Intip keseruan dan momen lucu para member di belakang layar saat pembuatan video klip Connection. Banyak hal tak terduga!",
                    channelName = "Hearts2Hearts Official",
                    thumbnailUrl = "https://img.youtube.com/vi/g6fPzYm-eNo/0.jpg",
                    duration = "12:30",
                    isFavorite = false
                ),
                VideoEntity(
                    videoId = "kJQP7kiw5Fk",
                    title = "[STREAM SPECIAL] 2nd Anniversary Celebration Live",
                    description = "Perayaan ulang tahun ke-2 Hearts2Hearts! Banyak pengumuman menarik, pembacaan surat fans, dan unboxing kado istimewa.",
                    channelName = "Hearts2Hearts Official",
                    thumbnailUrl = "https://img.youtube.com/vi/kJQP7kiw5Fk/0.jpg",
                    duration = "01:45:20",
                    isFavorite = false
                )
            )
            for (vid in defaultVideos) {
                videoDao.insertVideo(vid)
            }
        }

        // 3. Events
        val events = eventDao.getEventsSync()
        if (events.isNullOrEmpty()) {
            val defaultEvents = listOf(
                EventEntity(
                    id = 1,
                    title = "Gathering Regional H2H Jakarta 2026",
                    description = "Temu kangen fans H2H di Jakarta! Ngobrol seru, sharing merch, games fan-made, dan streaming bersama MV terbaru Connection.",
                    category = "Gathering",
                    date = "25 Juli 2026, 13:00 WIB",
                    location = "Taman Mini Indonesia Indah, Jakarta",
                    organizer = "H2H Region Jakarta Admin Team",
                    joinedCount = 42,
                    isJoined = false,
                    customFormUrl = "https://docs.google.com/forms/d/e/1FAIpQLSfZ6D4oN8-h2h-gather-jkt/viewform",
                    timestamp = System.currentTimeMillis() + (16L * 24 * 3600 * 1000), // 16 days in future
                    createdBy = "admin"
                ),
                EventEntity(
                    id = 2,
                    title = "Special Livestream Q&A: Midnight Chat",
                    description = "Sesi live streaming santai bersama kreator H2H di YouTube. Siapkan pertanyaan terbaik kalian!",
                    category = "Livestream",
                    date = "12 Juli 2026, 20:00 WIB",
                    location = "Official YouTube Channel",
                    organizer = "Hearts2Hearts Crew",
                    joinedCount = 156,
                    isJoined = true,
                    customFormUrl = null,
                    timestamp = System.currentTimeMillis() + (3L * 24 * 3600 * 1000), // 3 days in future
                    createdBy = "admin"
                ),
                EventEntity(
                    id = 3,
                    title = "MV Connection 1M Views Project",
                    description = "Ayo dukung pencapaian 1 Juta views untuk MV terbaru Hearts2Hearts! Koordinasi mass-streaming party bersama di playlist resmi.",
                    category = "Project",
                    date = "08 Juli - 15 Juli 2026",
                    location = "Online (YouTube Playlist H2H)",
                    organizer = "H2H Global Streaming Team",
                    joinedCount = 310,
                    isJoined = false,
                    customFormUrl = "https://docs.google.com/forms/d/e/1FAIpQLSfStreamingProjectForm/viewform",
                    timestamp = System.currentTimeMillis() + (6L * 24 * 3600 * 1000), // 6 days in future
                    createdBy = "admin"
                )
            )
            for (evt in defaultEvents) {
                eventDao.insertEvent(evt)
            }

            // Default attendees for events
            val defaultAttendees = listOf(
                EventAttendeeEntity(eventId = 1, userName = "Aline_Art", userTitle = "H2H Master Illustrator", userAvatar = "avatar_lumi"),
                EventAttendeeEntity(eventId = 1, userName = "Teoris_H2H", userTitle = "H2H Chief Theorist", userAvatar = "avatar_nova"),
                EventAttendeeEntity(eventId = 1, userName = "Melodi_H2H", userTitle = "Music Critic", userAvatar = "avatar_echo"),
                EventAttendeeEntity(eventId = 1, userName = "Eka_H2H", userTitle = "Event Coordinator", userAvatar = "avatar_sol"),
                
                EventAttendeeEntity(eventId = 2, userName = "RianH2H", userTitle = "Fan Club Member", userAvatar = "avatar_spark"),
                EventAttendeeEntity(eventId = 2, userName = "SparkLover", userTitle = "H2H Listener", userAvatar = "avatar_cosmo"),
                EventAttendeeEntity(eventId = 2, userName = "Melodi_H2H", userTitle = "Music Critic", userAvatar = "avatar_echo"),
                EventAttendeeEntity(eventId = 2, userName = "H2H_Spark", userTitle = "Senior Fan Club Member", userAvatar = "avatar_spark"),
                
                EventAttendeeEntity(eventId = 3, userName = "Aline_Art", userTitle = "H2H Master Illustrator", userAvatar = "avatar_lumi"),
                EventAttendeeEntity(eventId = 3, userName = "RianH2H", userTitle = "Fan Club Member", userAvatar = "avatar_spark"),
                EventAttendeeEntity(eventId = 3, userName = "Eka_H2H", userTitle = "Event Coordinator", userAvatar = "avatar_sol")
            )
            for (att in defaultAttendees) {
                eventAttendeeDao.insertAttendee(att)
            }

            // Default event comments
            val defaultEventComments = listOf(
                EventCommentEntity(
                    eventId = 1,
                    author = "Aline_Art",
                    authorTitle = "H2H Master Illustrator",
                    authorAvatar = "avatar_lumi",
                    content = "Wah seru banget nih gathering! Nanti aku bakal bawa merch fan-made stiker ya buat dibagi-bagikan gratis! 🎨💖",
                    timestamp = System.currentTimeMillis() - 72000000
                ),
                EventCommentEntity(
                    eventId = 1,
                    author = "Teoris_H2H",
                    authorTitle = "H2H Chief Theorist",
                    authorAvatar = "avatar_nova",
                    content = "Asik! Ga sabar pengen ketemu sesama fans dan bahas teori album baru. Aku pasti dateng membawa laptop untuk presentasi singkat hehe! 💻📊",
                    timestamp = System.currentTimeMillis() - 54000000
                ),
                EventCommentEntity(
                    eventId = 2,
                    author = "SparkLover",
                    authorTitle = "H2H Listener",
                    authorAvatar = "avatar_cosmo",
                    content = "Udah nulis pertanyaan Q&A di form! Semoga dibacain nanti pas live. Penasaran banget sama kelanjutan lore MV-nya! Chat seru midnight! 🤖✨",
                    timestamp = System.currentTimeMillis() - 36000000
                ),
                EventCommentEntity(
                    eventId = 3,
                    author = "Eka_H2H",
                    authorTitle = "Event Coordinator",
                    authorAvatar = "avatar_sol",
                    content = "Ayo semuanya streaming bareng-bareng! Target 1M views sebelum akhir pekan! Kita pasti bisa demi kesayangan kita Hearts2Hearts! 🔥🎶",
                    timestamp = System.currentTimeMillis() - 18000000
                )
            )
            for (cmt in defaultEventComments) {
                eventCommentDao.insertComment(cmt)
            }
        }

        // 4. Forum Threads and Comments
        val threads = forumDao.getThreadsSync()
        if (threads.isNullOrEmpty()) {
            val thread1 = ForumEntity(
                id = 1,
                title = "Pengumuman Ambassador Baru: Converse & Barenbliss! 💄👟",
                content = "Wah gila sih, Hearts2Hearts baru saja diumumkan sebagai brand ambassador untuk Converse dan Barenbliss! Makin sukses aja nih grup kita. Siapa yang udah siap ngeborong produknya demi PC eksklusif?",
                author = "H2H_Updates",
                authorTitle = "News Fanpage",
                category = "News",
                upvotes = 120,
                isUpvoted = true,
                timestamp = System.currentTimeMillis() - 86400000 // 1 day ago
            )
            val thread2 = ForumEntity(
                id = 2,
                title = "MV 'Lemon Tang' Rilis! Apa pendapat kalian? 🍋",
                content = "Title track dari 2nd Mini Album akhirnya rilis! Suka banget sama vibenya yang fresh dan energik. Bagian Jiwoo sama Ye-on jadi favoritku. Share pendapat kalian dong!",
                author = "Lemonade_Lover",
                authorTitle = "H2H Enthusiast",
                category = "General",
                upvotes = 95,
                isUpvoted = false,
                timestamp = System.currentTimeMillis() - 172800000 // 2 days ago
            )

            forumDao.insertThread(thread1)
            forumDao.insertThread(thread2)

            // Insert comments
            commentDao.insertComment(
                CommentEntity(
                    threadId = 1,
                    author = "RianH2H",
                    authorTitle = "Fan Club Member",
                    content = "Gila keren banget gambarmu Aline! Pose chibi-nya dapet banget, imut abis! Tolong buatin versi wallpaper dong hehe.",
                    timestamp = System.currentTimeMillis() - 80000000
                )
            )
            commentDao.insertComment(
                CommentEntity(
                    threadId = 1,
                    author = "Eka_H2H",
                    authorTitle = "Event Coordinator",
                    content = "Wah ini dia yang ditunggu-tunggu! Kreativitas fans H2H emang ga ada tandingannya. Makasih banyak ya udah berkarya, langsung kujadiin ava Discord!",
                    timestamp = System.currentTimeMillis() - 75000000
                )
            )

            commentDao.insertComment(
                CommentEntity(
                    threadId = 2,
                    author = "Melodi_H2H",
                    authorTitle = "Music Critic",
                    content = "Sangat setuju! Di lirik lagu bait kedua juga ada pesan tersembunyi yang mirip tentang jembatan dua warna ini. Teori kamu solid banget!",
                    timestamp = System.currentTimeMillis() - 150000000
                )
            )
            commentDao.insertComment(
                CommentEntity(
                    threadId = 2,
                    author = "SparkLover",
                    authorTitle = "H2H Listener",
                    content = "Wah aku ga kepikiran sampai sana pas nonton! Pas baca ini langsung buka YouTube lagi dan ternyata beneran dong detailnya dapet banget. H2H emang pinter bikin easter egg!",
                    timestamp = System.currentTimeMillis() - 140000000
                )
            )
        }

        // 5. Community Updates
                val updates = communityUpdateDao.getUpdatesSync()
        if (updates.isNullOrEmpty()) {
            val defaultUpdates = listOf(
                CommunityUpdateEntity(
                    id = "up_vid_01",
                    type = "video",
                    title = "[OFFICIAL MV] Hearts2Hearts - 'Lemon Tang'",
                    content = "2nd Mini Album 'Lemon Tang' telah rilis! Jangan lupa tonton MV-nya dan dukung Hearts2Hearts di chart Oricon dan Circle Chart!",
                    timestamp = System.currentTimeMillis() - 3600000,
                    sourceUrl = "https://www.youtube.com/watch?v=1VqxWNwgf5Q",
                    thumbnailUrl = "https://img.youtube.com/vi/1VqxWNwgf5Q/0.jpg"
                ),
                CommunityUpdateEntity(
                    id = "up_ann_01",
                    type = "announcement",
                    title = "Donasi 50 Juta Won dari Hearts2Hearts!",
                    content = "Dalam rangka merayakan anniversary pertama, Hearts2Hearts atas nama fanclub resmi menyumbangkan 50 juta won kepada G-Foundation untuk mendukung remaja perempuan yang membutuhkan. Bangga menjadi House!",
                    timestamp = System.currentTimeMillis() - 7200000,
                    sourceUrl = "https://hearts2hearts.lnk.to/LemonTang",
                    thumbnailUrl = null,
                    isPinned = true
                ),
                CommunityUpdateEntity(
                    id = "up_live_01",
                    type = "live",
                    title = "[UPCOMING] Hearts2House 2026 Fan Meetings",
                    content = "Hearts2House 2026 siap digelar di berbagai kota besar termasuk Seoul, New York, Los Angeles, dan Jakarta. Pastikan kalian mengamankan tiketnya!",
                    timestamp = System.currentTimeMillis() - 10800000,
                    sourceUrl = "https://instagram.com/hearts2hearts",
                    thumbnailUrl = null
                ),
                CommunityUpdateEntity(
                    id = "up_soc_01",
                    type = "social_media",
                    title = "New Post from Instagram @hearts2hearts",
                    content = "The NEW model that perfectly captures the soft mood of 2aN is none other than #HeartsToHearts! We are so excited for this collaboration. Please show a lot of love! 💖",
                    timestamp = System.currentTimeMillis() - 14400000,
                    sourceUrl = "https://instagram.com/hearts2hearts",
                    thumbnailUrl = null
                ),
                CommunityUpdateEntity(
                    id = "up_soc_02",
                    type = "social_media",
                    title = "New Post from X @Hearts2Hearts",
                    content = "Hearts2Hearts officially announced as the new muse for Sorule! Get smooth and shiny hair with Hearts2Hearts at Sorule. Check out comments from the members! ✨ #Hearts2Hearts #Sorule",
                    timestamp = System.currentTimeMillis() - 21600000,
                    sourceUrl = "https://x.com/Hearts2Hearts",
                    thumbnailUrl = null
                ),
                CommunityUpdateEntity(
                    id = "up_soc_03",
                    type = "social_media",
                    title = "New Post from TikTok @hearts2hearts",
                    content = "Lemon Tang Challenge with Carmen and Ye-on! 🍋 Let's dance together! #LemonTangChallenge #Hearts2Hearts",
                    timestamp = System.currentTimeMillis() - 28800000,
                    sourceUrl = "https://tiktok.com/@hearts2hearts",
                    thumbnailUrl = null
                ),
                CommunityUpdateEntity(
                    id = "up_soc_04",
                    type = "social_media",
                    title = "New Post from Facebook @hearts2heartsH2H",
                    content = "Thank you House for all the love and support for Lemon Tang! Let's continue to shine together! ✨",
                    timestamp = System.currentTimeMillis() - 36000000,
                    sourceUrl = "https://facebook.com/hearts2heartsH2H",
                    thumbnailUrl = null
                ),
                CommunityUpdateEntity(
                    id = "up_soc_05",
                    type = "social_media",
                    title = "New Video on YouTube @hearts2hearts.official",
                    content = "Behind The Scenes: Lemon Tang MV Shooting Day 1. Come see the members having fun on set! 🍋🎥",
                    timestamp = System.currentTimeMillis() - 42000000,
                    sourceUrl = "https://www.youtube.com/@hearts2hearts.official",
                    thumbnailUrl = null
                )
            )
            for (up in defaultUpdates) {
                communityUpdateDao.insertUpdate(up)
            }
        }
    }
    override suspend fun loadMoreThreads() {}
    override suspend fun refreshThreads() {}
    override suspend fun loadMoreEvents() {}
    override suspend fun refreshEvents() {}

    // Live Streaming & Integrated Live Chat Features
    private val _liveSessions = MutableStateFlow<List<LiveStreamSession>>(
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
    override val activeLiveSessions: Flow<List<LiveStreamSession>> = _liveSessions.asStateFlow()

    override fun getLiveSession(streamId: String): Flow<LiveStreamSession?> {
        return _liveSessions.map { sessions -> sessions.find { it.streamId == streamId } }
    }

    override suspend fun startLiveSession(hostUserId: String, title: String, streamUrl: String): String {
        val streamId = "live_${System.currentTimeMillis()}"
        val newSession = LiveStreamSession(
            streamId = streamId,
            hostUserId = hostUserId,
            title = title,
            streamUrl = streamUrl,
            viewerCount = 1,
            chatParticipantCount = 1,
            isActive = true
        )
        _liveSessions.update { it + newSession }
        val currentUser = userDao.getUserSync()
        if (currentUser != null) {
            userDao.insertOrUpdateUser(currentUser.copy(isLive = true, currentStreamId = streamId))
        }
        return streamId
    }

    override suspend fun stopLiveSession(streamId: String) {
        _liveSessions.update { sessions ->
            sessions.map { if (it.streamId == streamId) it.copy(isActive = false) else it }
        }
        val currentUser = userDao.getUserSync()
        if (currentUser != null && currentUser.currentStreamId == streamId) {
            userDao.insertOrUpdateUser(currentUser.copy(isLive = false, currentStreamId = null))
        }
    }

    override suspend fun incrementViewerCount(streamId: String) {
        _liveSessions.update { sessions ->
            sessions.map { if (it.streamId == streamId) it.copy(viewerCount = it.viewerCount + 1) else it }
        }
    }

    override suspend fun decrementViewerCount(streamId: String) {
        _liveSessions.update { sessions ->
            sessions.map { if (it.streamId == streamId) it.copy(viewerCount = maxOf(0, it.viewerCount - 1)) else it }
        }
    }

    private val _liveChatMessages = MutableStateFlow<List<LiveChatMessage>>(
        listOf(
            LiveChatMessage("msg_1", "live_default_carmen", "H2H_Spark", "Senior Fan Club Member", "Halo Carmen! Kakak cantik bgt hari ini 😍"),
            LiveChatMessage("msg_2", "live_default_carmen", "Carmen", "admin", "Halo semuanya! Makasih ya udah sempetin mampir ke live chat aku")
        )
    )

    override fun getLiveChatMessages(streamId: String): Flow<List<LiveChatMessage>> {
        return _liveChatMessages.map { messages -> messages.filter { it.streamId == streamId } }
    }

    override suspend fun sendLiveChatMessage(streamId: String, senderName: String, text: String, senderRole: String) {
        val message = LiveChatMessage(
            id = "msg_${System.currentTimeMillis()}",
            streamId = streamId,
            senderName = senderName,
            senderRole = senderRole,
            text = text,
            timestamp = System.currentTimeMillis()
        )
        _liveChatMessages.update { it + message }
    }
}
