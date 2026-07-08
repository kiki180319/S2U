package com.example.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.database.AppDatabase
import com.example.data.database.CommunityUpdateEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdateSyncWorker(
    appContext: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        Log.d("UpdateSyncWorker", "Memulai pemeriksaan pembaruan konten di latar belakang secara hemat baterai...")

        try {
            val database = AppDatabase.getDatabase(applicationContext)
            val updateDao = database.communityUpdateDao()

            // Simulasi pengambilan data pembaruan dari server resmi / API secara hemat data
            // Dalam implementasi nyata, di sini akan memanggil API endpoint Retrofit / Ktor
            val simulatedFetchedUpdates = listOf(
                CommunityUpdateEntity(
                    id = "up_sync_work_${System.currentTimeMillis()}",
                    type = "announcement",
                    title = "[LATEST UPDATE] Sinkronisasi Latar Belakang Berhasil!",
                    content = "WorkManager terpicu secara berkala dengan batasan (constraints) hemat daya. Sinkronisasi sukses dilakukan secara aman dan efisien.",
                    timestamp = System.currentTimeMillis(),
                    sourceUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                    thumbnailUrl = null
                )
            )

            // Simpan ke Room Database local
            for (update in simulatedFetchedUpdates) {
                updateDao.insertUpdate(update)
            }

            Log.d("UpdateSyncWorker", "Sinkronisasi konten berhasil diselesaikan.")
            Result.success()
        } catch (e: Exception) {
            Log.e("UpdateSyncWorker", "Kesalahan sinkronisasi: ${e.message}", e)
            Result.retry()
        }
    }
}
