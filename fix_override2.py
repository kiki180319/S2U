import re

with open('app/src/main/java/com/example/data/repository/HeartsRepository.kt', 'r') as f:
    content = f.read()

# First strip all overrides to start clean
content = re.sub(r'override val ', r'val ', content)
content = re.sub(r'override suspend fun ', r'suspend fun ', content)
content = re.sub(r'override fun ', r'fun ', content)

# Add override to properties
content = re.sub(r'val userProfile:', r'override val userProfile:', content)
content = re.sub(r'val allEvents:', r'override val allEvents:', content)
content = re.sub(r'val allThreads:', r'override val allThreads:', content)
content = re.sub(r'val allVideos:', r'override val allVideos:', content)
content = re.sub(r'val allUpdates:', r'override val allUpdates:', content)

# Add override to functions
functions = [
    "getCommentsForEvent",
    "getAttendeesForEvent",
    "getCommentsForThread",
    "getThreadById"
]
for func in functions:
    content = re.sub(rf'fun {func}', rf'override fun {func}', content)

suspend_functions = [
    "saveUserProfile",
    "addEvent",
    "updateEvent",
    "addEventComment",
    "addEventAttendee",
    "removeEventAttendee",
    "addThread",
    "updateThread",
    "addComment",
    "toggleVideoFavorite",
    "updateVideo",
    "addUpdate",
    "prepopulateIfEmpty"
]

for func in suspend_functions:
    content = re.sub(rf'suspend fun {func}', rf'override suspend fun {func}', content)

with open('app/src/main/java/com/example/data/repository/HeartsRepository.kt', 'w') as f:
    f.write(content)
