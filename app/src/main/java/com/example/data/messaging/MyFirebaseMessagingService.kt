package com.example.data.messaging

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.data.database.AppDatabase
import com.example.data.database.CommunityUpdateEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class MyFirebaseMessagingService : FirebaseMessagingService() {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCMService", "FCM Token baru diterima: $token")
        saveTokenToFirestore(token)
    }

    private fun saveTokenToFirestore(token: String) {
        val db = FirebaseFirestore.getInstance()
        val data = hashMapOf(
            "token" to token,
            "timestamp" to System.currentTimeMillis()
        )
        db.collection("user_tokens").document(token)
            .set(data)
            .addOnSuccessListener { Log.d("FCMService", "Token saved to Firestore") }
            .addOnFailureListener { e -> Log.e("FCMService", "Error saving token", e) }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Log.d("FCMService", "Pesan push FCM diterima dari: ${remoteMessage.from}")

        val data = remoteMessage.data
        if (data.isNotEmpty()) {
            val id = data["id"] ?: "fcm_${System.currentTimeMillis()}"
            val type = data["type"] ?: "announcement"
            val title = data["title"] ?: remoteMessage.notification?.title ?: "Pembaruan H2H Baru!"
            val content = data["content"] ?: remoteMessage.notification?.body ?: "Silakan buka aplikasi untuk melihat info selengkapnya."
            val sourceUrl = data["sourceUrl"]
            val thumbnailUrl = data["thumbnailUrl"]
            val timestamp = data["timestamp"]?.toLongOrNull() ?: System.currentTimeMillis()
            
            val targetScreen = data["target_screen"]
            val targetId = data["target_id"]

            scope.launch {
                try {
                    val database = AppDatabase.getDatabase(applicationContext)
                    val updateDao = database.communityUpdateDao()
                    val updateEntity = CommunityUpdateEntity(
                        id = id,
                        type = type,
                        title = title,
                        content = content,
                        timestamp = timestamp,
                        sourceUrl = sourceUrl,
                        thumbnailUrl = thumbnailUrl
                    )
                    updateDao.insertUpdate(updateEntity)
                    Log.d("FCMService", "Berhasil menyimpan update FCM ke database lokal.")
                } catch (e: Exception) {
                    Log.e("FCMService", "Gagal menyimpan update FCM ke database: ${e.message}")
                }
            }
            sendNotification(title, content, targetScreen, targetId)
        } else {
            remoteMessage.notification?.let {
                sendNotification(it.title ?: "Pembaruan H2H!", it.body ?: "Info baru telah tersedia!", null, null)
            }
        }
    }

    private fun sendNotification(title: String, messageBody: String, targetScreen: String?, targetId: String?) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            if (targetScreen != null) {
                putExtra("target_screen", targetScreen)
            }
            if (targetId != null) {
                putExtra("target_id", targetId)
            }
        }
        val pendingIntent = PendingIntent.getActivity(
            this, System.currentTimeMillis().toInt(), intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val channelId = "h2h_updates_channel"
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(messageBody)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Pembaruan Komunitas Hearts2Hearts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifikasi pembaruan video resmi, siaran langsung, dan pengumuman komunitas."
            }
            notificationManager.createNotificationChannel(channel)
        }

        notificationManager.notify(System.currentTimeMillis().toInt(), notificationBuilder.build())
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
    }
}