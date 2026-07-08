import re

with open('app/src/main/java/com/example/data/repository/HeartsRepository.kt', 'r') as f:
    content = f.read()

# Add override to properties
content = re.sub(r'val userProfile:', r'override val userProfile:', content)
content = re.sub(r'val allEvents:', r'override val allEvents:', content)
content = re.sub(r'val allThreads:', r'override val allThreads:', content)
content = re.sub(r'val allVideos:', r'override val allVideos:', content)
content = re.sub(r'val allUpdates:', r'override val allUpdates:', content)

# Add override to functions
functions = [
    "getCommentsForThread",
    "getEventComments",
    "getEventAttendees",
    "saveUserProfile",
    "addEvent",
    "updateEvent",
    "addEventComment",
    "addEventAttendee",
    "addThread",
    "updateThread",
    "addComment",
    "toggleVideoFavorite",
    "addUpdate"
]

for func in functions:
    content = re.sub(rf'suspend fun {func}', rf'override suspend fun {func}', content)

with open('app/src/main/java/com/example/data/repository/HeartsRepository.kt', 'w') as f:
    f.write(content)
