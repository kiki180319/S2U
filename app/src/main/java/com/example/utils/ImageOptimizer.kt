package com.example.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import java.io.ByteArrayOutputStream
import java.io.InputStream
import kotlin.math.max

object ImageOptimizer {
    
    // Max constraints for optimization to save Firebase Storage
    private const val MAX_RESOLUTION = 1080
    private const val THUMBNAIL_RESOLUTION = 300
    private const val MAX_FILE_SIZE_KB = 500
    
    /**
     * Resizes and compresses an image before uploading to Firebase Storage.
     * Returns a ByteArray containing the optimized JPEG.
     */
    fun optimizeForUpload(context: Context, imageUri: Uri, isThumbnail: Boolean = false): ByteArray? {
        Log.d("ImageOptimizer", "Mulai mengoptimalkan gambar. Mode Thumbnail: $isThumbnail")
        
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null
            inputStream?.close()
            
            // 1. Resize to reasonable resolution
            val targetResolution = if (isThumbnail) THUMBNAIL_RESOLUTION else MAX_RESOLUTION
            val resizedBitmap = resizeBitmap(originalBitmap, targetResolution)
            
            // 2. Compress until it fits under 500KB
            return compressUnderSize(resizedBitmap, MAX_FILE_SIZE_KB)
            
        } catch (e: Exception) {
            Log.e("ImageOptimizer", "Gagal mengoptimalkan gambar", e)
            return null
        }
    }
    
    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        
        val maxDimension = max(width, height)
        if (maxDimension <= maxSize) {
            return bitmap // Already small enough
        }
        
        val ratio = maxSize.toFloat() / maxDimension
        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()
        
        Log.d("ImageOptimizer", "Meresize gambar dari ${width}x${height} menjadi ${newWidth}x${newHeight}")
        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
    
    private fun compressUnderSize(bitmap: Bitmap, maxSizeKb: Int): ByteArray {
        var quality = 100
        var stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
        
        var byteArray = stream.toByteArray()
        
        // Loop to reduce quality until it fits the max size
        while (byteArray.size / 1024 > maxSizeKb && quality > 10) {
            quality -= 10
            stream.reset() // Clear the stream
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
            byteArray = stream.toByteArray()
        }
        
        Log.d("ImageOptimizer", "Gambar dikompresi menjadi ukuran: ${byteArray.size / 1024} KB dengan kualitas: $quality%")
        return byteArray
    }
}
