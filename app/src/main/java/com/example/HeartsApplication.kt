package com.example

import android.app.Application
import android.util.Log
import androidx.work.*
import com.example.data.database.AppDatabase
import com.example.data.repository.HeartsRepository
import com.example.data.repository.FirebaseHeartsRepository

import com.example.data.repository.IHeartsRepository
import com.example.data.worker.UpdateSyncWorker
import java.util.concurrent.TimeUnit

interface AppContainer {
    val repository: IHeartsRepository
}

class DefaultAppContainer(private val application: Application) : AppContainer {
    private val database by lazy { AppDatabase.getDatabase(application) }
    override val repository: IHeartsRepository by lazy {
        FirebaseHeartsRepository(
            application,
            database.userDao(),
            database.eventDao(),
            database.forumDao(),
            database.commentDao(),
            database.videoDao(),
            database.eventCommentDao(),
            database.eventAttendeeDao(),
            database.communityUpdateDao()
        )
    }
}

class HeartsApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
        Log.d("HeartsApplication", "HeartsApplication diinisialisasi.")
        
        scheduleBackgroundSync()
    }

    private fun scheduleBackgroundSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
            
        val syncRequest = PeriodicWorkRequestBuilder<UpdateSyncWorker>(1, TimeUnit.HOURS)
            .setConstraints(constraints)
            .build()
            
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "H2HContentUpdateSync",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )
        
        Log.d("HeartsApplication", "Pekerjaan berkala sinkronisasi latar belakang berhasil didaftarkan secara efisien.")
    }
}
