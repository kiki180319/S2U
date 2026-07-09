import re

with open('app/src/main/java/com/example/data/repository/HeartsRepository.kt', 'r') as f:
    content = f.read()

# Replace Videos
old_videos = """        val videos = videoDao.getVideosSync()
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
                    duration = "55:20",
                    isFavorite = true
                )
            )
            for (vid in defaultVideos) {
                videoDao.insertVideo(vid)
            }
        }"""

new_videos = """        val videos = videoDao.getVideosSync()
        if (videos.isNullOrEmpty()) {
            val defaultVideos = listOf(
                VideoEntity(
                    videoId = "1VqxWNwgf5Q",
                    title = "[OFFICIAL MV] Hearts2Hearts - 'Lemon Tang'",
                    description = "Title track dari 2nd Mini Album 'Lemon Tang'. Saksikan kesegaran dan energi dari Hearts2Hearts!",
                    channelName = "Hearts2Hearts Official",
                    thumbnailUrl = "https://img.youtube.com/vi/1VqxWNwgf5Q/0.jpg",
                    duration = "03:15",
                    isFavorite = true
                ),
                VideoEntity(
                    videoId = "F7sGJVUrkjQ",
                    title = "[OFFICIAL MV] Hearts2Hearts - 'RUDE!'",
                    description = "Digital single terbaru dari Hearts2Hearts yang menunjukkan sisi percaya diri dan karisma mereka.",
                    channelName = "Hearts2Hearts Official",
                    thumbnailUrl = "https://img.youtube.com/vi/F7sGJVUrkjQ/0.jpg",
                    duration = "02:58",
                    isFavorite = true
                ),
                VideoEntity(
                    videoId = "DQBoUMFAe5Q",
                    title = "[OFFICIAL MV] Hearts2Hearts - 'Focus'",
                    description = "Title track dari 1st EP 'Focus'. Rilis pada 20 Oktober 2025.",
                    channelName = "Hearts2Hearts Official",
                    thumbnailUrl = "https://img.youtube.com/vi/DQBoUMFAe5Q/0.jpg",
                    duration = "03:42",
                    isFavorite = false
                ),
                VideoEntity(
                    videoId = "oSozapfclTI",
                    title = "[OFFICIAL MV] Hearts2Hearts - 'STYLE'",
                    description = "Digital single 'STYLE' yang dirilis pada 18 Juni 2025.",
                    channelName = "Hearts2Hearts Official",
                    thumbnailUrl = "https://img.youtube.com/vi/oSozapfclTI/0.jpg",
                    duration = "03:20",
                    isFavorite = false
                )
            )
            for (vid in defaultVideos) {
                videoDao.insertVideo(vid)
            }
        }"""

content = content.replace(old_videos, new_videos)

# Replace user bias
content = content.replace('favoriteBias = "All Members (OT5)"', 'favoriteBias = "Jiwoo (Leader)"')

with open('app/src/main/java/com/example/data/repository/HeartsRepository.kt', 'w') as f:
    f.write(content)
