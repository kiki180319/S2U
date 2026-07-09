import re

with open('app/src/main/java/com/example/data/repository/HeartsRepository.kt', 'r') as f:
    content = f.read()

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
                )
            )"""

# using a substring match to replace the `val updates = ...` block
# The easiest way is to use regex:
pattern = re.compile(r'val updates = communityUpdateDao\.getUpdatesSync\(\)\s*if \(updates\.isNullOrEmpty\(\)\) \{\s*val defaultUpdates = listOf\([\s\S]*?\)\s*for \(up in defaultUpdates\) \{\s*communityUpdateDao\.insertUpdate\(up\)\s*\}\s*\}')

match = pattern.search(content)
if match:
    replacement = new_updates + """
            for (up in defaultUpdates) {
                communityUpdateDao.insertUpdate(up)
            }
        }"""
    content = content[:match.start()] + replacement + content[match.end():]

with open('app/src/main/java/com/example/data/repository/HeartsRepository.kt', 'w') as f:
    f.write(content)
