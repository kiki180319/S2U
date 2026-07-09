with open('app/src/main/java/com/example/data/repository/FirebaseHeartsRepository.kt', 'r') as f:
    content = f.read()

impl = """    override suspend fun clearLocalData() {
        localRepository.clearLocalData()
    }"""

if "override suspend fun clearLocalData()" not in content:
    content = content.replace("override suspend fun prepopulateIfEmpty()", impl + "\n\n    override suspend fun prepopulateIfEmpty()")
    with open('app/src/main/java/com/example/data/repository/FirebaseHeartsRepository.kt', 'w') as f:
        f.write(content)
