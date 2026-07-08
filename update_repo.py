with open('app/src/main/java/com/example/data/repository/IHeartsRepository.kt', 'r') as f:
    content = f.read()

new_methods = """    suspend fun addUpdates(updates: List<CommunityUpdateEntity>)
    suspend fun prepopulateIfEmpty()

    // Pagination & Refresh for Firebase Optimization
    suspend fun loadMoreThreads()
    suspend fun refreshThreads()
    suspend fun loadMoreEvents()
    suspend fun refreshEvents()"""
content = content.replace('    suspend fun addUpdates(updates: List<CommunityUpdateEntity>)\n    suspend fun prepopulateIfEmpty()', new_methods)

with open('app/src/main/java/com/example/data/repository/IHeartsRepository.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/data/repository/HeartsRepository.kt', 'r') as f:
    repo_content = f.read()

repo_methods = """    override suspend fun prepopulateIfEmpty() = withContext(Dispatchers.IO) {
        // ... (we will just append to the end)
"""
if "override suspend fun loadMoreThreads()" not in repo_content:
    repo_content += """
    override suspend fun loadMoreThreads() {}
    override suspend fun refreshThreads() {}
    override suspend fun loadMoreEvents() {}
    override suspend fun refreshEvents() {}
}
"""
    repo_content = repo_content.replace('}\n\n    override suspend fun loadMoreThreads', '    override suspend fun loadMoreThreads')
    with open('app/src/main/java/com/example/data/repository/HeartsRepository.kt', 'w') as f:
        f.write(repo_content)
