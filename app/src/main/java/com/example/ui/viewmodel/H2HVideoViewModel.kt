package com.example.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.api.H2HApiClient
import com.example.data.api.H2HVideo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.text.NumberFormat
import java.util.Locale

data class VideoStatsState(
    val viewCount: String = "",
    val likeCount: String = "",
    val commentCount: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isDemo: Boolean = false
)

class H2HVideoViewModel : ViewModel() {
    private val _videos = MutableStateFlow<List<H2HVideo>>(emptyList())
    val videos: StateFlow<List<H2HVideo>> = _videos.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    // Store video statistics mapped by YouTube Video ID
    private val _videoStats = MutableStateFlow<Map<String, VideoStatsState>>(emptyMap())
    val videoStats: StateFlow<Map<String, VideoStatsState>> = _videoStats.asStateFlow()

    init {
        fetchVideos()
    }

    private fun fetchVideos() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val response = H2HApiClient.retrofitService.getH2HData()
                _videos.value = response.videos
                _error.value = null
            } catch (e: Exception) {
                e.printStackTrace()
                _error.value = "Gagal mengambil data video: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchVideoViews(videoId: String) {
        // If already loaded or currently loading, don't refetch
        val currentStats = _videoStats.value[videoId]
        if (currentStats != null && (currentStats.isLoading || currentStats.viewCount.isNotEmpty())) {
            return
        }

        // Set loading state
        _videoStats.value = _videoStats.value + (videoId to VideoStatsState(isLoading = true))

        viewModelScope.launch(Dispatchers.IO) {
            var apiKey = try { BuildConfig.YOUTUBE_API_KEY } catch (e: Exception) { "" }
            var isFallbackKey = false
            
            if (apiKey.isBlank() || apiKey == "YOUR_YOUTUBE_API_KEY" || apiKey == "YOUTUBE_API_KEY") {
                // Fallback to the active YouTube API key provided by the user
                apiKey = "AIzaSyBjt7l8RD6ghK6IMwI-Bq4iqvrmOWj8jos"
                isFallbackKey = true
            }

            if (apiKey.isBlank()) {
                // Generate realistic mock data for H2H videos if API key is not configured
                // We use deterministic hash of videoId to make the mock numbers stable per video!
                val hashCode = Math.abs(videoId.hashCode())
                val mockViews = (hashCode % 800000) + 150000L
                val mockLikes = (mockViews * 0.12).toLong()
                val mockComments = (mockLikes * 0.08).toLong()

                val format = NumberFormat.getNumberInstance(Locale("id", "ID"))
                
                _videoStats.value = _videoStats.value + (videoId to VideoStatsState(
                    viewCount = format.format(mockViews),
                    likeCount = format.format(mockLikes),
                    commentCount = format.format(mockComments),
                    isLoading = false,
                    isDemo = true
                ))
                return@launch
            }

            try {
                val urlString = "https://www.googleapis.com/youtube/v3/videos?part=statistics&id=$videoId&key=$apiKey"
                val url = URL(urlString)
                val urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.connectTimeout = 10000
                urlConnection.readTimeout = 10000
                
                // Set identification headers for Google API Key restrictions
                urlConnection.setRequestProperty("X-Android-Package", BuildConfig.APPLICATION_ID)
                urlConnection.setRequestProperty("X-Android-Cert", "BE470091497F8AD590AC1353CE120ED588099966")
                
                val responseCode = urlConnection.responseCode
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    val bufferedReader = BufferedReader(InputStreamReader(urlConnection.inputStream))
                    val stringBuilder = StringBuilder()
                    var line: String?
                    
                    while (bufferedReader.readLine().also { line = it } != null) {
                        stringBuilder.append(line)
                    }
                    
                    val jsonResponse = JSONObject(stringBuilder.toString())
                    val itemsArray = jsonResponse.getJSONArray("items")
                    
                    if (itemsArray.length() > 0) {
                        val statistics = itemsArray.getJSONObject(0).getJSONObject("statistics")
                        val rawViews = statistics.optString("viewCount", "0")
                        val rawLikes = statistics.optString("likeCount", "0")
                        val rawComments = statistics.optString("commentCount", "0")
                        
                        val format = NumberFormat.getNumberInstance(Locale("id", "ID"))
                        val formattedViews = format.format(rawViews.toLongOrNull() ?: 0L)
                        val formattedLikes = format.format(rawLikes.toLongOrNull() ?: 0L)
                        val formattedComments = format.format(rawComments.toLongOrNull() ?: 0L)
                        
                        _videoStats.value = _videoStats.value + (videoId to VideoStatsState(
                            viewCount = formattedViews,
                            likeCount = formattedLikes,
                            commentCount = formattedComments,
                            isLoading = false,
                            isDemo = false
                        ))
                    } else {
                        _videoStats.value = _videoStats.value + (videoId to VideoStatsState(
                            viewCount = "0",
                            likeCount = "0",
                            commentCount = "0",
                            isLoading = false,
                            error = "Video tidak ditemukan di YouTube."
                        ))
                    }
                } else {
                    val errorText = urlConnection.errorStream?.bufferedReader()?.use { it.readText() }
                    Log.e("YOUTUBE_ERROR", "HTTP Error $responseCode dari YouTube API: $errorText")
                    _videoStats.value = _videoStats.value + (videoId to VideoStatsState(
                        isLoading = false,
                        error = "Gagal memuat statistik (HTTP $responseCode)"
                    ))
                }
            } catch (e: Exception) {
                Log.e("YOUTUBE_ERROR", "Exception terjadi: ${e.message}", e)
                _videoStats.value = _videoStats.value + (videoId to VideoStatsState(
                    isLoading = false,
                    error = "Gagal memuat (Cek Koneksi)"
                ))
            }
        }
    }
}
