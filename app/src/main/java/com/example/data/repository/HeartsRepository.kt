package com.example.data.repository

import com.example.data.database.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
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

    suspend fun deleteEventById(id: Int) = withContext(Dispatchers.IO) {
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
    override suspend fun prepopulateIfEmpty() = withContext(Dispatchers.IO) {
        // 1. User profile
        val user = userDao.getUser().firstOrNull()
        if (user == null) {
            userDao.insertOrUpdateUser(
                UserEntity(
                    id = 1,
                    deviceUuid = java.util.UUID.randomUUID().toString(),
                    name = "H2H_Spark",
                    title = "Senior Fan Club Member",
                    favoriteBias = "All Members (OT5)",
                    bio = "Pecinta musik Hearts2Hearts sejak rilis single pertama! Senang membantu koordinasi gathering regional.",
                    joinedDate = "July 2026"
                )
            )
        }

        // 2. Videos
        val videos = videoDao.getVideos().firstOrNull()
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
        val events = eventDao.getEvents().firstOrNull()
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
                    customFormUrl = "https://docs.google.com/forms/d/e/1FAIpQLSfZ6D4oN8-h2h-gather-jkt/viewform"
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
                    customFormUrl = null
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
                    customFormUrl = "https://docs.google.com/forms/d/e/1FAIpQLSfStreamingProjectForm/viewform"
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
        val threads = forumDao.getThreads().firstOrNull()
        if (threads.isNullOrEmpty()) {
            val thread1 = ForumEntity(
                id = 1,
                title = "Rekomendasi Fanart Hearts2Hearts Minggu Ini! 🎨",
                content = "Halo kawan-kawan H2H! Aku baru saja selesai menggambar ilustrasi chibi seluruh member untuk merayakan comeback Connection kemarin. Aku penasaran sama karya teman-teman yang lain, yuk bagikan link atau upload post fanart kalian di bawah agar kita bisa saling dukung!",
                author = "Aline_Art",
                authorTitle = "H2H Master Illustrator",
                category = "Fan Art",
                upvotes = 38,
                isUpvoted = false,
                timestamp = System.currentTimeMillis() - 86400000 // 1 day ago
            )
            val thread2 = ForumEntity(
                id = 2,
                title = "Teori di Balik Simbol Dua Hati di MV Connection 💖",
                content = "Teman-teman sadar gak, di menit 02:15 ada simbol dua hati yang terhubung tapi dengan warna merah dan biru? Menurutku ini melambangkan koneksi emosional antara dunia kreator (biru - tenang/inspirasi) dan fans (merah - gairah/semangat) yang saling melengkapi. Bagaimana pendapat kalian? Ada teori lain?",
                author = "Teoris_H2H",
                authorTitle = "H2H Chief Theorist",
                category = "General",
                upvotes = 54,
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
        val updates = communityUpdateDao.getUpdates().firstOrNull()
        if (updates.isNullOrEmpty()) {
            val defaultUpdates = listOf(
                CommunityUpdateEntity(
                    id = "up_vid_01",
                    type = "video",
                    title = "[OFFICIAL MV] Hearts2Hearts - Connection",
                    content = "Single terbaru 'Connection' akhirnya resmi dirilis di YouTube! Tonton dan bantu streaming bersama sekarang untuk merayakan milestone baru kita.",
                    timestamp = System.currentTimeMillis() - 3600000,
                    sourceUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                    thumbnailUrl = "https://img.youtube.com/vi/dQw4w9WgXcQ/0.jpg"
                ),
                CommunityUpdateEntity(
                    id = "up_ann_01",
                    type = "announcement",
                    title = "Merchandise Edisi Terbatas 2nd Anniversary!",
                    content = "Seluruh keuntungan penjualan merchandise gantungan kunci & stiker chibi edisi terbatas 2nd Anniversary akan didonasikan untuk pendanaan event gathering regional mendatang. Silakan cek detail pemesanan di menu event!",
                    timestamp = System.currentTimeMillis() - 7200000,
                    sourceUrl = "https://docs.google.com/forms/d/e/1FAIpQLSfZ6D4oN8-h2h-gather-jkt/viewform",
                    thumbnailUrl = null,
                    isPinned = true
                ),
                CommunityUpdateEntity(
                    id = "up_live_01",
                    type = "live",
                    title = "[UPCOMING LIVE] Special Q&A Midnight Chat",
                    content = "Sesi tanya jawab santai dan hangat bersama seluruh member Hearts2Hearts langsung di platform streaming resmi malam ini pukul 20:00 WIB! Jangan lewatkan kesempatan mengobrol seru ini.",
                    timestamp = System.currentTimeMillis() - 10800000,
                    sourceUrl = "https://www.youtube.com/watch?v=kJQP7kiw5Fk",
                    thumbnailUrl = "https://img.youtube.com/vi/kJQP7kiw5Fk/0.jpg"
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
}
