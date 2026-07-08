import re

with open('app/src/main/java/com/example/data/repository/HeartsRepository.kt', 'r') as f:
    content = f.read()

content = content.replace("class HeartsRepository : IHeartsRepository {\n    constructor(", "class HeartsRepository(\n")
content = content.replace("class HeartsRepository(\n    private val userDao: UserDao", "class HeartsRepository(\n    private val userDao: UserDao")
# wait, actually I should just use `class HeartsRepository(...) : IHeartsRepository {`

content = re.sub(r'class HeartsRepository \: IHeartsRepository \{\n\s*constructor\(\n(.*?)Dao\)', r'class HeartsRepository(\n\1Dao) : IHeartsRepository {', content, flags=re.DOTALL)

with open('app/src/main/java/com/example/data/repository/HeartsRepository.kt', 'w') as f:
    f.write(content)
