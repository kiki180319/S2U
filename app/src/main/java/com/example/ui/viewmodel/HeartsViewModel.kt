package com.example.ui.viewmodel
import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.api.*
import com.example.data.database.AppDatabase
import com.example.data.database.CommentEntity
import com.example.data.database.EventEntity
import com.example.data.database.ForumEntity
import com.example.data.database.UserEntity
import com.example.data.database.VideoEntity
import com.example.data.database.EventCommentEntity
import com.example.data.database.EventAttendeeEntity
import com.example.data.database.CommunityUpdateEntity
import com.example.data.repository.HeartsRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.lifecycle.ViewModel
import com.example.data.repository.IHeartsRepository
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.HeartsApplication




data class ChatMessage(
    val sender: String, // "user" or "model"
    val text: String,
    val searchSources: List<Pair<String, String>>? = null // title, uri
)


class HeartsViewModel(private val repository: IHeartsRepository) : ViewModel() {

    // Current Tab
    private val _currentTab = MutableStateFlow("home")
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()
    val isAdmin: StateFlow<Boolean> = repository.userProfile.map { it?.role == "admin" }.stateIn(viewModelScope, SharingStarted.Lazily, false)

    init {
        fetchAndSaveFcmToken()
    }

    private fun fetchAndSaveFcmToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("HeartsViewModel", "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            val db = FirebaseFirestore.getInstance()
            val data = hashMapOf(
                "token" to token,
                "timestamp" to System.currentTimeMillis()
            )
            db.collection("user_tokens").document(token)
                .set(data)
                .addOnSuccessListener { Log.d("HeartsViewModel", "Token saved to Firestore initially") }
        }
    }

    fun handleNotificationIntent(targetScreen: String, targetId: String?) {
        selectTab(targetScreen)
        if (targetId != null) {
            when (targetScreen) {
                "forum" -> {
                    val threadId = targetId.toIntOrNull()
                    if (threadId != null) {
                        viewModelScope.launch {
                            // Let's delay slightly to ensure threads are loaded if not already
                            kotlinx.coroutines.delay(500)
                            val thread = threads.value.find { it.id == threadId }
                            if (thread != null) {
                                selectThread(thread)
                            }
                        }
                    }
                }
                "events" -> {
                    val eventId = targetId.toIntOrNull()
                    if (eventId != null) {
                        viewModelScope.launch {
                            kotlinx.coroutines.delay(500)
                            val event = events.value.find { it.id == eventId }
                            if (event != null) {
                                selectEvent(event)
                            }
                        }
                    }
                }
            }
        }
    }

    // Community Unified Updates Feed State
    val updates: StateFlow<List<CommunityUpdateEntity>> = repository.allUpdates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Video Feed State
    val videos: StateFlow<List<VideoEntity>> = repository.allVideos
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedVideo = MutableStateFlow<VideoEntity?>(null)
    val selectedVideo: StateFlow<VideoEntity?> = _selectedVideo.asStateFlow()

    // AI Helper State
    private val _aiResponse = MutableStateFlow<String>("")
    val aiResponse: StateFlow<String> = _aiResponse.asStateFlow()

    private val _aiLoading = MutableStateFlow(false)
    val aiLoading: StateFlow<Boolean> = _aiLoading.asStateFlow()

    // Event Calendar State
    val events: StateFlow<List<EventEntity>> = repository.allEvents
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _selectedEvent = MutableStateFlow<EventEntity?>(null)
    val selectedEvent: StateFlow<EventEntity?> = _selectedEvent.asStateFlow()

    private val _eventComments = MutableStateFlow<List<EventCommentEntity>>(emptyList())
    val eventComments: StateFlow<List<EventCommentEntity>> = _eventComments.asStateFlow()

    private val _eventAttendees = MutableStateFlow<List<EventAttendeeEntity>>(emptyList())
    val eventAttendees: StateFlow<List<EventAttendeeEntity>> = _eventAttendees.asStateFlow()

    // Forum State
    val threads: StateFlow<List<ForumEntity>> = repository.allThreads
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedThread = MutableStateFlow<ForumEntity?>(null)
    val selectedThread: StateFlow<ForumEntity?> = _selectedThread.asStateFlow()

    private val _threadComments = MutableStateFlow<List<CommentEntity>>(emptyList())
    val threadComments: StateFlow<List<CommentEntity>> = _threadComments.asStateFlow()

    // Profile State
    val userProfile: StateFlow<UserEntity?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Moderation/Forum warning state
    private val _moderationWarning = MutableStateFlow<String?>(null)
    val moderationWarning: StateFlow<String?> = _moderationWarning.asStateFlow()

    // Chatbot States
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _chatLoading = MutableStateFlow(false)
    val chatLoading: StateFlow<Boolean> = _chatLoading.asStateFlow()

    private val _chatModel = MutableStateFlow("gemini-3.5-flash") // Default is gemini-3.5-flash (with grounding)
    val chatModel: StateFlow<String> = _chatModel.asStateFlow()

    private val _chatPersona = MutableStateFlow("companion")
    val chatPersona: StateFlow<String> = _chatPersona.asStateFlow()

    fun selectChatModel(model: String) {
        _chatModel.value = model
    }

    fun selectChatPersona(persona: String) {
        _chatPersona.value = persona
    }

    fun clearChat() {
        _chatMessages.value = emptyList()
    }

    fun sendMessageToChatbot(userMessage: String) {
        if (userMessage.isBlank()) return
        
        val updatedList = _chatMessages.value.toMutableList()
        updatedList.add(ChatMessage("user", userMessage))
        _chatMessages.value = updatedList
        
        _chatLoading.value = true
        
        viewModelScope.launch {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                _chatLoading.value = false
                val mockResponseText = when (_chatPersona.value) {
                    "scholar" -> "Halo! Saya adalah H2H Lore Scholar 🎓. Di mode demonstrasi offline, saya bisa berasumsi bahwa pesan Anda '${userMessage}' adalah sebuah teori yang menarik! Tambahkan API Key di panel Secrets untuk menganalisis lore H2H yang sebenarnya menggunakan kecerdasan Gemini."
                    "songwriter" -> "Hai! Kreator H2H AI di sini 📝. Pesan Anda '${userMessage}' terdengar puitis! Dalam mode demo, saya sarankan membuat lirik bernada ceria dengan yel-yel 'H2H Jaya!'. Hubungkan API Key di Secrets untuk berkreasi bersama Gemini secara langsung."
                    else -> "Hai kak! H2H Fan Companion di sini 💖. Terbawa suasana senang membaca pesanmu '${userMessage}'! Di mode demo offline ini, aku mau ngingetin kamu buat dengerin lagu terbaru Hearts2Hearts ya! Hubungkan API Key di Secrets untuk berbincang interaktif denganku via Gemini!"
                }
                val finalMessages = _chatMessages.value.toMutableList()
                finalMessages.add(ChatMessage("model", mockResponseText))
                _chatMessages.value = finalMessages
                return@launch
            }

            val systemInstructionText = when (_chatPersona.value) {
                "scholar" -> "Anda adalah Ahli Teori & Lore Hearts2Hearts (H2H Lore Scholar). Tugas Anda adalah melakukan analisis mendalam tentang konsep album, petunjuk visual MV, lirik lagu, dan teori lore fiksi ilmiah/fantasi dari grup Hearts2Hearts secara cerdas, objektif, dan menarik. Gunakan gaya bahasa semi-formal yang informatif."
                "songwriter" -> "Anda adalah Kreator Musik Fanbase Hearts2Hearts (H2H Fan Creator). Anda ahli dalam membantu fans menulis lirik lagu tribute, merancang chant konser, menyusun yel-yel, atau membuat skema acara gathering fans secara kreatif dan musikal. Gunakan bahasa santai, inspiratif, dan penuh ritme."
                else -> "Anda adalah Pendamping AI Hearts2Hearts (H2H AI Companion), seorang sahabat dekat fanbase OT5 yang super ramah, penuh emoji, selalu bersemangat, dan siap membagikan kebahagiaan serta info menarik tentang grup kesayangan kita. Gunakan Bahasa Indonesia yang sangat akrab, santun, hangat, dan positif."
            }

            val historyToInclude = _chatMessages.value.takeLast(10)
            val apiContents = historyToInclude.map { msg ->
                Content(
                    role = if (msg.sender == "user") "user" else "model",
                    parts = listOf(Part(text = msg.text))
                )
            }

            val modelName = _chatModel.value
            var config: GenerationConfig? = null
            var toolsList: List<Tool>? = null

            when (modelName) {
                "gemini-3.1-flash-lite-preview" -> {
                    config = GenerationConfig(temperature = 0.7f)
                }
                "gemini-3.5-flash" -> {
                    toolsList = listOf(Tool(googleSearch = emptyMap()))
                }
                "gemini-3.1-pro-preview" -> {
                    config = GenerationConfig(
                        thinkingConfig = ThinkingConfig(thinkingLevel = "HIGH"),
                        temperature = 0.7f
                    )
                }
            }

            val request = GenerateContentRequest(
                contents = apiContents,
                systemInstruction = Content(parts = listOf(Part(text = systemInstructionText))),
                generationConfig = config,
                tools = toolsList
            )

            try {
                val response = GeminiApiClient.service.generateContent(modelName, apiKey, request)
                val candidate = response.candidates?.firstOrNull()
                val text = candidate?.content?.parts?.firstOrNull()?.text ?: "Maaf, saya tidak menerima jawaban kosong dari model. Silakan coba lagi!"
                
                val searchSources = mutableListOf<Pair<String, String>>()
                candidate?.groundingMetadata?.let { metadata ->
                    metadata.groundingChunks?.forEach { chunk ->
                        val web = chunk.web
                        if (web != null && !web.title.isNullOrBlank() && !web.uri.isNullOrBlank()) {
                            searchSources.add(Pair(web.title, web.uri))
                        }
                    }
                }

                val finalMessages = _chatMessages.value.toMutableList()
                finalMessages.add(
                    ChatMessage(
                        sender = "model",
                        text = text,
                        searchSources = if (searchSources.isNotEmpty()) searchSources.distinct() else null
                    )
                )
                _chatMessages.value = finalMessages
            } catch (e: Exception) {
                val finalMessages = _chatMessages.value.toMutableList()
                finalMessages.add(ChatMessage("model", "Terjadi kesalahan koneksi atau konfigurasi model: ${e.message}. Pastikan internet menyala dan coba lagi!"))
                _chatMessages.value = finalMessages
            } finally {
                _chatLoading.value = false
            }
        }
    }

    init {
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }
    }

    fun selectTab(tab: String) {
        _currentTab.value = tab
    }

    fun selectVideo(video: VideoEntity?) {
        _selectedVideo.value = video
        _aiResponse.value = "" // clear previous AI responses
    }

    fun selectThread(thread: ForumEntity?) {
        _selectedThread.value = thread
        if (thread != null) {
            viewModelScope.launch {
                repository.getCommentsForThread(thread.id).collect { comments ->
                    _threadComments.value = comments
                }
            }
        } else {
            _threadComments.value = emptyList()
        }
    }

    fun toggleFavoriteVideo(video: VideoEntity) {
        viewModelScope.launch {
            repository.updateVideo(video.copy(isFavorite = !video.isFavorite))
        }
    }

    fun selectEvent(event: EventEntity?) {
        _selectedEvent.value = event
        if (event != null) {
            viewModelScope.launch {
                repository.getCommentsForEvent(event.id).collect { comments ->
                    _eventComments.value = comments
                }
            }
            viewModelScope.launch {
                repository.getAttendeesForEvent(event.id).collect { attendees ->
                    _eventAttendees.value = attendees
                }
            }
        } else {
            _eventComments.value = emptyList()
            _eventAttendees.value = emptyList()
        }
    }

    fun toggleJoinEvent(event: EventEntity) {
        viewModelScope.launch {
            val newIsJoined = !event.isJoined
            val newCount = if (newIsJoined) event.joinedCount + 1 else event.joinedCount - 1
            val updatedEvent = event.copy(isJoined = newIsJoined, joinedCount = newCount)
            repository.updateEvent(updatedEvent)
            
            if (_selectedEvent.value?.id == event.id) {
                _selectedEvent.value = updatedEvent
            }

            val user = userProfile.value ?: UserEntity(name = "H2H_Spark", title = "Senior Fan Club Member", favoriteBias = "All Members (OT5)", bio = "Pecinta musik Hearts2Hearts sejak rilis single pertama!", joinedDate = "July 2026", avatarName = "avatar_spark")
            if (newIsJoined) {
                repository.addEventAttendee(
                    EventAttendeeEntity(
                        eventId = event.id,
                        userName = user.name,
                        userTitle = user.title,
                        userAvatar = user.avatarName
                    )
                )
            } else {
                repository.removeEventAttendee(event.id, user.name)
            }
        }
    }

    fun postEventComment(eventId: Int, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            val user = userProfile.value ?: UserEntity(name = "H2H_Spark", title = "Senior Fan Club Member", favoriteBias = "All Members (OT5)", bio = "Pecinta musik Hearts2Hearts sejak rilis single pertama!", joinedDate = "July 2026", avatarName = "avatar_spark")
            repository.addEventComment(
                EventCommentEntity(
                    eventId = eventId,
                    author = user.name,
                    authorTitle = user.title,
                    authorAvatar = user.avatarName,
                    content = content
                )
            )
        }
    }

    fun syncEventsFromGoogleSheet(url: String, onSuccess: (Int) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val client = okhttp3.OkHttpClient()
                val request = okhttp3.Request.Builder().url(url).build()
                val response = withContext(Dispatchers.IO) {
                    client.newCall(request).execute()
                }
                if (!response.isSuccessful) {
                    onError("HTTP error code: ${response.code}")
                    return@launch
                }
                val csvData = response.body?.string()
                if (csvData.isNullOrBlank()) {
                    onError("File CSV kosong atau tidak dapat diunduh")
                    return@launch
                }
                
                val lines = csvData.split("\n")
                val newEvents = mutableListOf<EventEntity>()
                
                for (i in 1 until lines.size) { // skip header
                    val line = lines[i].trim()
                    if (line.isEmpty()) continue
                    
                    val parts = parseCsvLine(line)
                    if (parts.size >= 5) {
                        val title = parts[0]
                        val desc = parts[1]
                        val category = parts[2]
                        val date = parts[3]
                        val location = parts[4]
                        val formUrl = if (parts.size >= 6 && parts[5].isNotBlank()) parts[5] else null
                        
                        newEvents.add(
                            EventEntity(
                                title = title,
                                description = desc,
                                category = category,
                                date = date,
                                location = location,
                                organizer = "H2H Official Sheet",
                                joinedCount = (10..150).random(),
                                isJoined = false,
                                customFormUrl = formUrl
                            )
                        )
                    }
                }
                
                if (newEvents.isNotEmpty()) {
                    withContext(Dispatchers.IO) {
                        for (evt in newEvents) {
                            repository.addEvent(evt)
                        }
                    }
                    onSuccess(newEvents.size)
                } else {
                    onError("Format kolom tidak sesuai. Pastikan ada minimal 5 kolom: Judul, Deskripsi, Kategori, Tanggal, Lokasi")
                }
            } catch (e: Exception) {
                onError(e.message ?: "Kesalahan koneksi atau URL tidak valid")
            }
        }
    }

    private fun parseCsvLine(line: String): List<String> {
        val result = mutableListOf<String>()
        val current = StringBuilder()
        var inQuotes = false
        var i = 0
        while (i < line.length) {
            val c = line[i]
            if (c == '"') {
                inQuotes = !inQuotes
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString().trim())
                current.setLength(0)
            } else {
                current.append(c)
            }
            i++
        }
        result.add(current.toString().trim())
        return result.map { it.removeSurrounding("\"") }
    }

    fun filterEvents(category: String) {
        _selectedCategory.value = category
    }

    // 1. Google Form event coordination (Submit a gathering or RSVP)
    fun addCustomEvent(title: String, desc: String, category: String, date: String, location: String, formUrl: String?) {
        viewModelScope.launch {
            repository.addEvent(
                EventEntity(
                    title = title,
                    description = desc,
                    category = category,
                    date = date,
                    location = location,
                    organizer = userProfile.value?.name ?: "H2H_Fan",
                    joinedCount = 1,
                    isJoined = true,
                    customFormUrl = formUrl
                )
            )
        }
    }

    // 2. Add Thread with Gemini Content Safety & Moderation
    fun checkAndAddThread(title: String, content: String, category: String, onModerationFailed: (String) -> Unit, onSuccess: () -> Unit) {
        if (title.isBlank() || content.isBlank() || category.isBlank()) {
            onModerationFailed("Judul, konten, dan kategori tidak boleh kosong.")
            return
        }
        _moderationWarning.value = null
        viewModelScope.launch {
            _aiLoading.value = true
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                // If local API key is placeholder, proceed with local naive keyword filter to ensure offline/mock safety
                val containsHate = content.lowercase().contains("toxic") || content.lowercase().contains("hate") || content.lowercase().contains("kasar")
                if (containsHate) {
                    _aiLoading.value = false
                    onModerationFailed("Postingan Anda terdeteksi mengandung kata kasar atau tidak pantas. Harap jaga komunitas tetap ramah dan positif.")
                    return@launch
                }
                saveThreadLocal(title, content, category)
                _aiLoading.value = false
                onSuccess()
                return@launch
            }

            // Real Gemini content filter
            val prompt = """
                Analisislah teks forum fanbase Hearts2Hearts berikut dari segi kelayakan komunitas (tidak boleh ada hate speech, cyberbullying, spam, pornografi, bahasa kasar, atau link promosi berbahaya).
                Jika aman, jawab dengan satu kata: "SAFE".
                Jika tidak aman/tidak pantas, jawab dengan penjelasan singkat dalam bahasa Indonesia mengapa teks ini dilarang dan cara memperbaikinya dengan sopan.
                
                Judul: "$title"
                Konten: "$content"
            """.trimIndent()

            try {
                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                    systemInstruction = Content(parts = listOf(Part(text = "Kamu adalah sistem moderasi AI komunitas fanbase Hearts2Hearts yang ramah namun disiplin.")))
                )
                val response = GeminiApiClient.service.generateContent("gemini-3.5-flash", apiKey, request)
                val resultText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim() ?: "SAFE"

                if (resultText.startsWith("SAFE", ignoreCase = true)) {
                    saveThreadLocal(title, content, category)
                    onSuccess()
                } else {
                    onModerationFailed(resultText)
                }
            } catch (e: Exception) {
                // Fail-safe to local check if network error
                saveThreadLocal(title, content, category)
                onSuccess()
            } finally {
                _aiLoading.value = false
            }
        }
    }

    private suspend fun saveThreadLocal(title: String, content: String, category: String) {
        val user = userProfile.value
        repository.addThread(
            ForumEntity(
                title = title,
                content = content,
                author = user?.name ?: "H2H_Spark",
                authorTitle = user?.title ?: "Senior Fan Club Member",
                category = category,
                upvotes = 1,
                isUpvoted = true
            )
        )
    }

    // 3. Post a comment to thread
    fun postComment(threadId: Int, content: String) {
        if (content.isBlank()) return
        viewModelScope.launch {
            val user = userProfile.value
            val authorName = user?.name ?: "H2H_Spark"
            
            repository.addComment(
                CommentEntity(
                    threadId = threadId,
                    author = authorName,
                    authorTitle = user?.title ?: "Senior Fan Club Member",
                    content = content
                )
            )
            
            // Trigger automatic notification by writing to Firestore
            // Finds the original thread owner
            val thread = threads.value.find { it.id == threadId }
            if (thread != null && thread.author != authorName) {
                val db = FirebaseFirestore.getInstance()
                val notificationData = hashMapOf(
                    "targetUser" to thread.author,
                    "senderName" to authorName,
                    "threadId" to threadId.toString(),
                    "title" to "Balasan Baru di Forum",
                    "body" to "$authorName mengomentari postingan Anda: '${thread.title}'",
                    "target_screen" to "forum",
                    "target_id" to threadId.toString(),
                    "timestamp" to System.currentTimeMillis()
                )
                db.collection("forum_notifications").add(notificationData)
                    .addOnSuccessListener { Log.d("HeartsViewModel", "Trigger notification written to Firestore") }
                    .addOnFailureListener { e -> Log.e("HeartsViewModel", "Error writing trigger", e) }
            }
        }
    }

    // 4. Upvote thread
    fun toggleUpvoteThread(thread: ForumEntity) {
        viewModelScope.launch {
            val newIsUpvoted = !thread.isUpvoted
            val newUpvotes = if (newIsUpvoted) thread.upvotes + 1 else thread.upvotes - 1
            repository.updateThread(thread.copy(isUpvoted = newIsUpvoted, upvotes = newUpvotes))
            
            // Refresh currently viewed thread if open
            if (_selectedThread.value?.id == thread.id) {
                _selectedThread.value = thread.copy(isUpvoted = newIsUpvoted, upvotes = newUpvotes)
            }
        }
    }

    // 5. Update user profile
    fun updateUserProfile(
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
        viewModelScope.launch {
            val current = userProfile.value ?: UserEntity(
                deviceUuid = java.util.UUID.randomUUID().toString(),
                name = "",
                title = "",
                favoriteBias = "",
                bio = "",
                joinedDate = ""
            )
            // Choose a title based on bias or dynamic fun level
            val dynamicTitle = if (favoriteBias.contains("All", ignoreCase = true)) {
                "H2H OT5 Eternal Supporter"
            } else {
                "Dedicated H2H Listener"
            }
            repository.saveUserProfile(
                current.copy(
                    name = name,
                    title = dynamicTitle,
                    favoriteBias = favoriteBias,
                    bio = bio,
                    joinedDate = current.joinedDate.ifEmpty { "July 2026" },
                    avatarName = avatarName,
                    twitterUrl = twitterUrl?.ifBlank { null },
                    instagramUrl = instagramUrl?.ifBlank { null },
                    youtubeUrl = youtubeUrl?.ifBlank { null },
                    tiktokUrl = tiktokUrl?.ifBlank { null }
                )
            )
        }
    }

    // 6. Gemini-powered interactive companion for video lore & discussion
    fun askAiCompanionAboutVideo(video: VideoEntity, userQuery: String) {
        if (userQuery.isBlank()) return
        _aiResponse.value = ""
        _aiLoading.value = true
        viewModelScope.launch {
            val apiKey = BuildConfig.GEMINI_API_KEY
            if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                _aiLoading.value = false
                _aiResponse.value = "Halo! Pendamping AI Hearts2Hearts siap melayani Anda setelah API Key ditambahkan di panel Secrets. Saat ini saya berjalan dalam mode demonstrasi offline.\n\nTentang video '${video.title}': Ini adalah karya luar biasa dari Hearts2Hearts yang sangat menginspirasi seluruh fanbase!"
                return@launch
            }

            val prompt = """
                Pengguna menanyakan pertanyaan ini: "$userQuery"
                Terkait dengan konten video berikut:
                - Judul: "${video.title}"
                - Deskripsi: "${video.description}"
                - Kreator: "${video.channelName}"
                
                Berikan jawaban yang ramah, hangat, bersemangat (sesuai kepribadian fanbase Hearts2Hearts), informatif, dan edukatif. Tulis dalam Bahasa Indonesia yang santun namun akrab.
            """.trimIndent()

            try {
                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt)))),
                    systemInstruction = Content(parts = listOf(Part(text = "Anda adalah Pendamping AI Hearts2Hearts (H2H AI Companion), seorang pemandu komunitas fanbase H2H yang ramah, bersemangat, tahu banyak tentang musik dan lore Hearts2Hearts, serta selalu mengajak fans menjaga atmosfer positif.")))
                )
                val response = GeminiApiClient.service.generateContent("gemini-3.5-flash", apiKey, request)
                val text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text ?: "Maaf, saya tidak dapat merespons saat ini. Silakan coba lagi!"
                _aiResponse.value = text
            } catch (e: Exception) {
                _aiResponse.value = "Terjadi kesalahan saat menghubungi H2H AI Companion: ${e.message}. Silakan pastikan koneksi internet aktif dan API Key valid!"
            } finally {
                _aiLoading.value = false
            }
        }
    }

    fun addCommunityUpdate(update: CommunityUpdateEntity) {
        viewModelScope.launch {
            repository.addUpdate(update)
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as HeartsApplication)
                HeartsViewModel(repository = application.container.repository)
            }
        }
    }
}
