with open('app/src/main/java/com/example/data/repository/HeartsRepository.kt', 'r') as f:
    content = f.read()

content = content.replace('communityUpdateDao: CommunityUpdateDao\n) {', 'communityUpdateDao: CommunityUpdateDao\n) : IHeartsRepository {')
content = content.replace('communityUpdateDao: CommunityUpdateDao) {', 'communityUpdateDao: CommunityUpdateDao) : IHeartsRepository {')

# Also fix the repeated overrides
import re
content = re.sub(r'(override\s+)+', 'override ', content)

with open('app/src/main/java/com/example/data/repository/HeartsRepository.kt', 'w') as f:
    f.write(content)
