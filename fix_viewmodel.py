import re

with open('app/src/main/java/com/example/ui/viewmodel/HeartsViewModel.kt', 'r') as f:
    content = f.read()

# Fix checkAndAddThread
old_check = """    fun checkAndAddThread(title: String, content: String, category: String, onModerationFailed: (String) -> Unit, onSuccess: () -> Unit) {
        _moderationWarning.value = null
        viewModelScope.launch {"""
new_check = """    fun checkAndAddThread(title: String, content: String, category: String, onModerationFailed: (String) -> Unit, onSuccess: () -> Unit) {
        if (title.isBlank() || content.isBlank() || category.isBlank()) {
            onModerationFailed("Judul, konten, dan kategori tidak boleh kosong.")
            return
        }
        _moderationWarning.value = null
        viewModelScope.launch {"""
content = content.replace(old_check, new_check)

# Fix postComment
old_post = """    fun postComment(threadId: Int, content: String) {
        viewModelScope.launch {"""
new_post = """    fun postComment(threadId: Int, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {"""
content = content.replace(old_post, new_post)

# Fix updateUserProfile
old_update = """    fun updateUserProfile(
        name: String,
        favoriteBias: String,
        bio: String,
        avatarName: String = "avatar_spark",
        twitterUrl: String? = null,
        instagramUrl: String? = null,
        youtubeUrl: String? = null,
        tiktokUrl: String? = null
    ) {
        viewModelScope.launch {"""
new_update = """    fun updateUserProfile(
        name: String,
        favoriteBias: String,
        bio: String,
        avatarName: String = "avatar_spark",
        twitterUrl: String? = null,
        instagramUrl: String? = null,
        youtubeUrl: String? = null,
        tiktokUrl: String? = null
    ) {
        if (name.isBlank() || favoriteBias.isBlank()) return
        viewModelScope.launch {"""
content = content.replace(old_update, new_update)

# Fix postEventComment
old_event_post = """    fun postEventComment(eventId: Int, content: String) {
        viewModelScope.launch {"""
new_event_post = """    fun postEventComment(eventId: Int, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {"""
content = content.replace(old_event_post, new_event_post)

# Fix askAiCompanionAboutVideo
old_ask = """    fun askAiCompanionAboutVideo(video: VideoEntity, userQuery: String) {
        _aiResponse.value = ""
        _aiLoading.value = true"""
new_ask = """    fun askAiCompanionAboutVideo(video: VideoEntity, userQuery: String) {
        if (userQuery.isBlank()) return
        _aiResponse.value = ""
        _aiLoading.value = true"""
content = content.replace(old_ask, new_ask)

with open('app/src/main/java/com/example/ui/viewmodel/HeartsViewModel.kt', 'w') as f:
    f.write(content)

