with open('app/src/main/java/com/example/ui/viewmodel/HeartsViewModel.kt', 'r') as f:
    content = f.read()

# I will add a call to clear all data and prepopulate.
# Wait, this might clear user profile as well. I'll just clear the videos, events, threads, updates.

replacement = """            try {
                repository.clearLocalData()
                repository.prepopulateIfEmpty()
            } catch (e: Exception) {"""

content = content.replace("""            try {
                repository.prepopulateIfEmpty()
            } catch (e: Exception) {""", replacement)

with open('app/src/main/java/com/example/ui/viewmodel/HeartsViewModel.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/data/repository/IHeartsRepository.kt', 'r') as f:
    repo_iface = f.read()

if "suspend fun clearLocalData()" not in repo_iface:
    repo_iface = repo_iface.replace("suspend fun prepopulateIfEmpty()", "suspend fun clearLocalData()\n    suspend fun prepopulateIfEmpty()")
    with open('app/src/main/java/com/example/data/repository/IHeartsRepository.kt', 'w') as f:
        f.write(repo_iface)

with open('app/src/main/java/com/example/data/repository/HeartsRepository.kt', 'r') as f:
    repo_impl = f.read()

clear_impl = """    override suspend fun clearLocalData() = withContext(Dispatchers.IO) {
        communityUpdateDao.clearAllUpdates()
        // we can clear others if we want, but let's just clear updates for now
    }"""

if "override suspend fun clearLocalData()" not in repo_impl:
    repo_impl = repo_impl.replace("    override suspend fun prepopulateIfEmpty()", clear_impl + "\n\n    override suspend fun prepopulateIfEmpty()")
    with open('app/src/main/java/com/example/data/repository/HeartsRepository.kt', 'w') as f:
        f.write(repo_impl)

