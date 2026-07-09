import re

with open('app/src/main/java/com/example/data/repository/HeartsRepository.kt', 'r') as f:
    content = f.read()

old_events = """        val events = eventDao.getEventsSync()
        if (events.isNullOrEmpty()) {
            val defaultEvents = listOf(
                EventEntity(
                    id = 1,
                    title = "Gathering Nasional House 2026",
                    date = "August 15, 2026",
                    location = "Jakarta Convention Center, Senayan",
                    organizer = "H2H Indonesia Fanbase",
                    description = "Ajang kumpul terbesar tahunan untuk seluruh fans Hearts2Hearts di Indonesia! Akan ada booth merchandise, penampilan cover dance, games berhadiah, dan nonton bareng konser dokumenter. Dresscode: Pink & Putih.",
                    imageUrl = "https://picsum.photos/seed/event1/800/400",
                    isJoined = true
                ),
                EventEntity(
                    id = 2,
                    title = "Midnight Q&A Streaming",
                    date = "October 31, 2026",
                    location = "Online (YouTube Live)",
                    organizer = "Hearts2Hearts Official",
                    description = "Sesi tanya jawab spesial merayakan Halloween. Member akan tampil dengan kostum spesial dan membaca pertanyaan dari fans secara langsung. Jangan lupa siapkan pertanyaan terbaikmu!",
                    imageUrl = null,
                    isJoined = false
                ),
                EventEntity(
                    id = 3,
                    title = "Mass-Streaming Party: 1M Views",
                    date = "November 10, 2026",
                    location = "Online (Discord/X)",
                    organizer = "Hearts2Hearts Crew",
                    description = "Ayo dukung pencapaian 1 Juta views untuk MV terbaru Hearts2Hearts! Koordinasi mass-streaming party bersama di playlist resmi.",
                    imageUrl = "https://picsum.photos/seed/event3/800/400",
                    isJoined = false
                )
            )"""

new_events = """        val events = eventDao.getEventsSync()
        if (events.isNullOrEmpty()) {
            val defaultEvents = listOf(
                EventEntity(
                    id = 1,
                    title = "Hearts2House 2026 in Jakarta",
                    date = "March 28, 2026",
                    location = "Tennis Indoor Senayan, Jakarta",
                    organizer = "SM Entertainment & H2H Indonesia",
                    description = "Konser Hearts2House 2026 Fan Meetings and Showcase Tour singgah di Jakarta! Mari sambut ke-8 member (Carmen, Jiwoo, Yuha, Stella, Juun, A-na, Ian, Ye-on) dengan gemuruh sorakan di Tennis Indoor Senayan.",
                    imageUrl = "https://picsum.photos/seed/event1/800/400",
                    isJoined = true
                ),
                EventEntity(
                    id = 2,
                    title = "Hearts2House 2026 in Seoul",
                    date = "Feb 21-22, 2026",
                    location = "Olympic Hall, Seoul",
                    organizer = "SM Entertainment",
                    description = "Pembukaan dari Hearts2House 2026 Fan Meetings and Showcase Tour. Saksikan penampilan spektakuler mereka secara langsung di Seoul!",
                    imageUrl = null,
                    isJoined = false
                ),
                EventEntity(
                    id = 3,
                    title = "Focus Showcase",
                    date = "October 20, 2025",
                    location = "Blue Square SOL Travel Hall",
                    organizer = "SM Entertainment",
                    description = "Showcase perilisan 1st EP 'Focus'. Acara spesial bersama para fans (House) untuk menyaksikan penampilan live perdana dari lagu 'Focus' dan 'Style'.",
                    imageUrl = "https://picsum.photos/seed/event3/800/400",
                    isJoined = false
                )
            )"""

content = content.replace(old_events, new_events)

old_threads = """        val threads = forumDao.getThreadsSync()
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
            )"""

new_threads = """        val threads = forumDao.getThreadsSync()
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
            )"""

content = content.replace(old_threads, new_threads)

old_updates = """        val updates = communityUpdateDao.getUpdatesSync()
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
            )"""

new_updates = """        val updates = communityUpdateDao.getUpdatesSync()
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
                )
            )"""

content = content.replace(old_updates, new_updates)

with open('app/src/main/java/com/example/data/repository/HeartsRepository.kt', 'w') as f:
    f.write(content)
