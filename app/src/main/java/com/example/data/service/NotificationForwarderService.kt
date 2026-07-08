package com.example.data.service

import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.google.firebase.database.FirebaseDatabase

class NotificationForwarderService : NotificationListenerService() {

    private val WEVERSE_PACKAGE = "co.benx.weverse"

    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        super.onNotificationPosted(sbn)
        if (sbn == null) return

        // 1. Filter: Hanya ambil notifikasi dari Weverse
        if (sbn.packageName == WEVERSE_PACKAGE) {
            val extras = sbn.notification.extras
            val title = extras.getString("android.title") ?: "Weverse Update"
            val text = extras.getCharSequence("android.text")?.toString() ?: ""

            Log.d("H2H_Notif", "Menangkap: $title")
            
            // 2. Langsung kirim ke Firebase
            sendToFirebase(title, text)
        }
    }

    override fun onNotificationRemoved(sbn: StatusBarNotification?) {
        super.onNotificationRemoved(sbn)
    }

    private fun sendToFirebase(title: String, text: String) {
        try {
            // Menghubungkan ke Firebase Realtime Database
            val database = FirebaseDatabase.getInstance()
            val ref = database.getReference("updates") // Ini adalah folder di database Anda

            val data = mapOf(
                "title" to title,
                "text" to text,
                "timestamp" to System.currentTimeMillis()
            )

            // Mengirim data ke Firebase agar bisa dibaca HP user lain
            ref.push().setValue(data).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("H2H_Notif", "Berhasil dikirim ke Firebase!")
                } else {
                    Log.e("H2H_Notif", "Gagal mengirim: ${task.exception?.message}")
                }
            }
        } catch (e: Exception) {
            Log.e("H2H_Notif", "Gagal menghubungkan ke Firebase Realtime Database: ${e.message}")
        }
    }
}
