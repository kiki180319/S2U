import re

with open('app/src/main/java/com/example/ui/viewmodel/HeartsViewModel.kt', 'r') as f:
    content = f.read()

states_to_add = """    private val _isProfileLoading = MutableStateFlow(true)
    val isProfileLoading: StateFlow<Boolean> = _isProfileLoading.asStateFlow()

    private val _profileError = MutableStateFlow<String?>(null)
    val profileError: StateFlow<String?> = _profileError.asStateFlow()

    private val _isVideosLoading = MutableStateFlow(true)
    val isVideosLoading: StateFlow<Boolean> = _isVideosLoading.asStateFlow()

    private val _videosError = MutableStateFlow<String?>(null)
    val videosError: StateFlow<String?> = _videosError.asStateFlow()

"""

# Insert states before userProfile
content = content.replace("    val userProfile: StateFlow<UserEntity?>", states_to_add + "    val userProfile: StateFlow<UserEntity?>")

init_replacement = """    init {
        viewModelScope.launch {
            // Monitor Profile Loading
            launch {
                try {
                    kotlinx.coroutines.withTimeout(10000) {
                        userProfile.filterNotNull().first()
                    }
                    _isProfileLoading.value = false
                } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                    _isProfileLoading.value = false
                    if (userProfile.value == null) {
                        _profileError.value = "Sesi login tidak ditemukan. Silakan login atau buat profil."
                    }
                }
            }

            // Monitor Videos Loading
            launch {
                try {
                    kotlinx.coroutines.withTimeout(10000) {
                        videos.filter { it.isNotEmpty() }.first()
                    }
                    _isVideosLoading.value = false
                } catch (e: kotlinx.coroutines.TimeoutCancellationException) {
                    _isVideosLoading.value = false
                    if (videos.value.isEmpty()) {
                        _videosError.value = "Gagal memuat video atau daftar video kosong. (Waktu habis)"
                    }
                }
            }

            try {
                repository.prepopulateIfEmpty()
            } catch (e: Exception) {
                Log.e("HeartsViewModel", "Gagal melakukan prepopulate database lokal", e)
            }
        }
    }"""

content = content.replace("""    init {
        viewModelScope.launch {
            repository.prepopulateIfEmpty()
        }
    }""", init_replacement)

with open('app/src/main/java/com/example/ui/viewmodel/HeartsViewModel.kt', 'w') as f:
    f.write(content)
