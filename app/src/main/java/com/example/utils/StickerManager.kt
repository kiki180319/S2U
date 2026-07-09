package com.example.utils

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.example.ui.theme.HeartsPink
import com.example.ui.theme.PremiumLightGray

/**
 * Representasi Metadata Stiker.
 */
data class StickerMetadata(
    val id: String,
    val name: String,
    val localPlaceholderName: String
)

/**
 * StickerManager mengelola whitelist stiker dan menyediakan helper URL Firebase Storage serta visualisasi Coil.
 */
object StickerManager {
    
    // Whitelist stiker yang valid dalam sistem Hearts2Hearts
    private val stickerWhitelist = mapOf(
        "sticker_spark_love" to StickerMetadata("sticker_spark_love", "Spark Love", "ic_sticker_love"),
        "sticker_spark_happy" to StickerMetadata("sticker_spark_happy", "Spark Happy", "ic_sticker_happy"),
        "sticker_spark_sad" to StickerMetadata("sticker_spark_sad", "Spark Sad", "ic_sticker_sad"),
        "sticker_spark_wink" to StickerMetadata("sticker_spark_wink", "Spark Wink", "ic_sticker_wink"),
        "sticker_spark_cheer" to StickerMetadata("sticker_spark_cheer", "Spark Cheer", "ic_sticker_cheer")
    )

    /**
     * Memvalidasi apakah ID stiker terdaftar di whitelist.
     */
    fun isValidStickerId(stickerId: String?): Boolean {
        if (stickerId == null) return false
        return stickerWhitelist.containsKey(stickerId)
    }

    /**
     * Mendapatkan daftar semua stiker yang valid (whitelist).
     */
    fun getAvailableStickers(): List<StickerMetadata> {
        return stickerWhitelist.values.toList()
    }

    /**
     * Menghasilkan URL download Firebase Storage untuk file stiker secara langsung.
     * File stiker disimpan di bucket Firebase Storage: gs://hearts2hearts-app.appspot.com/stickers/
     */
    fun getStickerStorageUrl(stickerId: String): String {
        val bucketName = "hearts2hearts-app.appspot.com"
        val folderPath = "stickers"
        return "https://firebasestorage.googleapis.com/v0/b/$bucketName/o/" +
                "$folderPath%2F$stickerId.png?alt=media"
    }
}

/**
 * Composable custom untuk me-render Stiker secara aman dari Firebase Storage menggunakan Coil Library.
 */
@Composable
fun StickerImage(
    stickerId: String,
    modifier: Modifier = Modifier.size(80.dp)
) {
    if (!StickerManager.isValidStickerId(stickerId)) {
        // Jika stiker tidak valid/tidak di-whitelist, tampilkan error indicator
        Box(modifier = modifier, contentAlignment = Alignment.Center) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Stiker tidak valid",
                tint = HeartsPink
            )
        }
        return
    }

    val stickerUrl = StickerManager.getStickerStorageUrl(stickerId)

    SubcomposeAsyncImage(
        model = stickerUrl,
        contentDescription = "Stiker: $stickerId",
        modifier = modifier,
        loading = {
            Box(contentAlignment = Alignment.Center) {
                CircularProgressIndicator(
                    color = HeartsPink,
                    strokeWidth = 2.dp,
                    modifier = Modifier.size(24.dp)
                )
            }
        },
        error = {
            // Tampilan alternatif jika gagal mendownload stiker (misal offline)
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Gagal memuat stiker",
                    tint = PremiumLightGray
                )
            }
        }
    )
}
