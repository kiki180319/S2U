import re

file_path = "app/src/main/java/com/example/ui/viewmodel/HeartsViewModel.kt"

with open(file_path, "r") as f:
    content = f.read()

login_func = """
    fun login(username: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val success = repository.loginWithUsername(username)
                if (success == null) {
                    _profileError.value = "Pengguna '$username' tidak ditemukan. Silakan buat profil."
                } else {
                    _profileError.value = null
                }
            } catch (e: Exception) {
                _profileError.value = "Gagal login: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
"""

if "fun login(" not in content:
    # insert before updateUserProfile
    content = content.replace("    // 5. Update user profile", login_func + "\n    // 5. Update user profile")
    
    with open(file_path, "w") as f:
        f.write(content)
        print("Updated HeartsViewModel")
