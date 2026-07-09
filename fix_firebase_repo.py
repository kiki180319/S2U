with open('app/src/main/java/com/example/data/repository/FirebaseHeartsRepository.kt', 'r') as f:
    content = f.read()

content = content.replace("localRepository.clearLocalData()", "communityUpdateDao.clearAllUpdates()")

with open('app/src/main/java/com/example/data/repository/FirebaseHeartsRepository.kt', 'w') as f:
    f.write(content)
