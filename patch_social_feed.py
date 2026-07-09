with open('app/src/main/java/com/example/data/repository/HeartsRepository.kt', 'r') as f:
    content = f.read()

# Add a Facebook update
new_update = """                CommunityUpdateEntity(
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
            )"""

import re
content = re.sub(r'CommunityUpdateEntity\([^)]*sourceUrl = "https://tiktok\.com/@hearts2hearts"[^)]*\)\n\s*\)', r'CommunityUpdateEntity(\g<0>\n' + new_update, content)

# Instead of complex regex, let's just do a string replace on the last one.
# Find the tiktok update.
content = content.replace("""CommunityUpdateEntity(
                    id = "up_soc_03",
                    type = "social_media",
                    title = "New Post from TikTok @hearts2hearts",
                    content = "Lemon Tang Challenge with Carmen and Ye-on! 🍋 Let's dance together! #LemonTangChallenge #Hearts2Hearts",
                    timestamp = System.currentTimeMillis() - 28800000,
                    sourceUrl = "https://tiktok.com/@hearts2hearts",
                    thumbnailUrl = null
                )
            )""", """CommunityUpdateEntity(
                    id = "up_soc_03",
                    type = "social_media",
                    title = "New Post from TikTok @hearts2hearts",
                    content = "Lemon Tang Challenge with Carmen and Ye-on! 🍋 Let's dance together! #LemonTangChallenge #Hearts2Hearts",
                    timestamp = System.currentTimeMillis() - 28800000,
                    sourceUrl = "https://tiktok.com/@hearts2hearts",
                    thumbnailUrl = null
                ),
""" + new_update)

with open('app/src/main/java/com/example/data/repository/HeartsRepository.kt', 'w') as f:
    f.write(content)
