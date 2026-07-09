import re

with open('app/src/main/java/com/example/data/repository/HeartsRepository.kt', 'r') as f:
    content = f.read()

content = content.replace("imageUrl = \"https://picsum.photos/seed/event1/800/400\",", "category = \"Gathering\",")
content = content.replace("imageUrl = null,", "category = \"Livestream\",")
content = content.replace("imageUrl = \"https://picsum.photos/seed/event3/800/400\",", "category = \"Project\",")

with open('app/src/main/java/com/example/data/repository/HeartsRepository.kt', 'w') as f:
    f.write(content)

