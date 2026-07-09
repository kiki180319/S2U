package com.example

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.screens.MainScreen
import com.example.ui.theme.Hearts2HeartsTheme
import com.example.ui.viewmodel.HeartsViewModel

class MainActivity : ComponentActivity() {

    private var sharedViewModel: HeartsViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val factory = HeartsViewModel.Factory
        val vm = androidx.lifecycle.ViewModelProvider(this, factory).get(HeartsViewModel::class.java)
        sharedViewModel = vm
        
        // Handle initial intent
        handleIntent(intent)

        enableEdgeToEdge()
        setContent {
            val themeName by vm.currentTheme.collectAsStateWithLifecycle()
            Hearts2HeartsTheme(themeName = themeName) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = androidx.compose.material3.MaterialTheme.colorScheme.background
                ) {
                    val context = LocalContext.current
                    val permissionLauncher = rememberLauncherForActivityResult(
                        contract = ActivityResultContracts.RequestPermission()
                    ) { isGranted ->
                        // Dynamic permission callback
                    }
                    LaunchedEffect(Unit) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            if (ContextCompat.checkSelfPermission(
                                    context,
                                    android.Manifest.permission.POST_NOTIFICATIONS
                                ) != PackageManager.PERMISSION_GRANTED
                            ) {
                                permissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    }
                    
                    MainScreen(viewModel = vm)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        val targetScreen = intent?.getStringExtra("target_screen")
        val targetId = intent?.getStringExtra("target_id")
        if (targetScreen != null) {
            sharedViewModel?.handleNotificationIntent(targetScreen, targetId)
        }
    }

    /**
     * Memeriksa apakah Notification Listener Service sudah diizinkan oleh pengguna.
     */
    fun checkNotificationPermission(): Boolean {
        val pkgName = packageName
        val flat = Settings.Secure.getString(contentResolver, "enabled_notification_listeners")
        if (!flat.isNullOrEmpty()) {
            val names = flat.split(":")
            for (name in names) {
                val cn = ComponentName.unflattenFromString(name)
                if (cn != null && cn.packageName == pkgName) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * Mengarahkan pengguna ke menu pengaturan Notification Listener JIKA DAN HANYA JIKA belum aktif.
     */
    fun requestNotificationPermission() {
        if (!checkNotificationPermission()) {
            try {
                val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("MainActivity", "Gagal membuka setelan listener notifikasi", e)
            }
        }
    }

    /**
     * Memeriksa apakah aplikasi sudah dikecualikan dari optimasi baterai (Doze Mode).
     */
    fun checkBatteryOptimizationPermission(): Boolean {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        return powerManager.isIgnoringBatteryOptimizations(packageName)
    }

    /**
     * Mengarahkan pengguna ke menu optimasi baterai agar aplikasi tidak dimatikan di latar belakang.
     */
    fun requestBatteryOptimizationPermission() {
        if (!checkBatteryOptimizationPermission()) {
            try {
                val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                    data = Uri.parse("package:$packageName")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
            } catch (e: Exception) {
                // Fallback jika direct request ditolak / tidak didukung sistem OEM tertentu
                try {
                    val intent = Intent(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(intent)
                } catch (ex: Exception) {
                    Log.e("MainActivity", "Gagal membuka setelan optimasi baterai", ex)
                }
            }
        }
    }

    /**
     * Memeriksa apakah izin overlay (SYSTEM_ALERT_WINDOW) sudah diberikan.
     */
    fun checkOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }

    /**
     * Mengarahkan pengguna ke setelan sistem untuk memberikan izin overlay.
     */
    fun requestOverlayPermission() {
        if (!checkOverlayPermission()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                try {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    ).apply {
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    startActivity(intent)
                } catch (e: Exception) {
                    Log.e("MainActivity", "Gagal meminta izin overlay", e)
                }
            }
        }
    }
}
