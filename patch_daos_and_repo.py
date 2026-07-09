with open('app/src/main/java/com/example/data/database/DatabaseDaos.kt', 'r') as f:
    content = f.read()

content = content.replace(
    '    fun getUser(): Flow<UserEntity?>',
    '    fun getUser(): Flow<UserEntity?>\n    @Query("SELECT * FROM user_profile WHERE id = 1 LIMIT 1")\n    suspend fun getUserSync(): UserEntity?'
)

content = content.replace(
    '    fun getEvents(): Flow<List<EventEntity>>',
    '    fun getEvents(): Flow<List<EventEntity>>\n    @Query("SELECT * FROM events ORDER BY id DESC")\n    suspend fun getEventsSync(): List<EventEntity>'
)

content = content.replace(
    '    fun getThreads(): Flow<List<ForumEntity>>',
    '    fun getThreads(): Flow<List<ForumEntity>>\n    @Query("SELECT * FROM forum_threads ORDER BY timestamp DESC")\n    suspend fun getThreadsSync(): List<ForumEntity>'
)

content = content.replace(
    '    fun getVideos(): Flow<List<VideoEntity>>',
    '    fun getVideos(): Flow<List<VideoEntity>>\n    @Query("SELECT * FROM videos")\n    suspend fun getVideosSync(): List<VideoEntity>'
)

content = content.replace(
    '    fun getUpdates(): Flow<List<CommunityUpdateEntity>>',
    '    fun getUpdates(): Flow<List<CommunityUpdateEntity>>\n    @Query("SELECT * FROM community_updates ORDER BY timestamp DESC")\n    suspend fun getUpdatesSync(): List<CommunityUpdateEntity>'
)

with open('app/src/main/java/com/example/data/database/DatabaseDaos.kt', 'w') as f:
    f.write(content)

with open('app/src/main/java/com/example/data/repository/HeartsRepository.kt', 'r') as f:
    content2 = f.read()

content2 = content2.replace('val user = userDao.getUser().firstOrNull()', 'val user = userDao.getUserSync()')
content2 = content2.replace('val videos = videoDao.getVideos().firstOrNull()', 'val videos = videoDao.getVideosSync()')
content2 = content2.replace('val events = eventDao.getEvents().firstOrNull()', 'val events = eventDao.getEventsSync()')
content2 = content2.replace('val threads = forumDao.getThreads().firstOrNull()', 'val threads = forumDao.getThreadsSync()')
content2 = content2.replace('val updates = communityUpdateDao.getUpdates().firstOrNull()', 'val updates = communityUpdateDao.getUpdatesSync()')

with open('app/src/main/java/com/example/data/repository/HeartsRepository.kt', 'w') as f:
    f.write(content2)

