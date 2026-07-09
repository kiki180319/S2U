package com.example.ui.screens

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.BatteryAlert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.MainActivity
import com.example.ui.theme.*
import com.example.ui.viewmodel.HeartsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: HeartsViewModel) {
    val context = LocalContext.current
    val mainActivity = context as? MainActivity
    
    // Status state
    var isNotificationListenerAllowed by remember { mutableStateOf(mainActivity?.checkNotificationPermission() ?: false) }
    var isBatteryOptimizationAllowed by remember { mutableStateOf(mainActivity?.checkBatteryOptimizationPermission() ?: false) }
    
    // Poll for changes when screen is entered / resumed
    LaunchedEffect(Unit) {
        isNotificationListenerAllowed = mainActivity?.checkNotificationPermission() ?: false
        isBatteryOptimizationAllowed = mainActivity?.checkBatteryOptimizationPermission() ?: false
    }

    // Dynamic app version
    val appVersion = remember {
        try {
            val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            pInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0"
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PremiumBlack)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Text(
                text = "Pengaturan Aplikasi",
                color = PremiumWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 24.dp)
            )

            // 1. Perizinan Notification Listener (Membaca Chat Sesi Live)
            Text(
                text = "Perizinan & Akses",
                color = PremiumWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
            
            Card(
                colors = CardDefaults.cardColors(containerColor = PremiumDarkGray),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notification Listener",
                                tint = HeartsPink,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Akses Notifikasi (Chat Reader)",
                                    color = PremiumWhite,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isNotificationListenerAllowed) "Akses sudah diizinkan" else "Dibutuhkan untuk mensinkronisasi live chat",
                                    color = if (isNotificationListenerAllowed) HeartsPink else PremiumLightGray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        
                        if (!isNotificationListenerAllowed) {
                            Button(
                                onClick = {
                                    mainActivity?.requestNotificationPermission()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = HeartsPink),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Izinkan", color = PremiumWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Diizinkan",
                                tint = HeartsPink,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                    
                    HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 16.dp))
                    
                    // 2. Battery Optimization
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(
                                imageVector = Icons.Default.BatteryAlert,
                                contentDescription = "Battery Optimization",
                                tint = HeartsPink,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Optimasi Baterai",
                                    color = PremiumWhite,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 15.sp
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (isBatteryOptimizationAllowed) "Optimasi baterai dikecualikan (Latar belakang aktif)" else "Batasi penghentian paksa di latar belakang",
                                    color = if (isBatteryOptimizationAllowed) HeartsPink else PremiumLightGray,
                                    fontSize = 12.sp
                                )
                            }
                        }
                        
                        if (!isBatteryOptimizationAllowed) {
                            Button(
                                onClick = {
                                    mainActivity?.requestBatteryOptimizationPermission()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = HeartsPink),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("Izinkan", color = PremiumWhite, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Diizinkan",
                                tint = HeartsPink,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }

            // 3. Tema Lainnya (iOS themes info / switch)
            Text(
                text = "Tampilan & Tema",
                color = PremiumWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
            
            Card(
                colors = CardDefaults.cardColors(containerColor = PremiumDarkGray),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val currentTheme by viewModel.currentTheme.collectAsStateWithLifecycle()
                    
                    Text(
                        text = "Pilih Tema iOS (Maksimal 4 Tema)",
                        color = PremiumWhite,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    
                    val themes = listOf(
                        "ios_dark" to "iOS Dark Premium",
                        "ios_light" to "iOS Light Elegant",
                        "ios_indigo" to "iOS Indigo Vibrant",
                        "ios_forest" to "iOS Forest Deep"
                    )
                    
                    themes.forEachIndexed { index, (themeId, themeLabel) ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.selectTheme(themeId)
                                }
                                .padding(vertical = 10.dp)
                        ) {
                            Text(
                                text = themeLabel,
                                color = if (currentTheme == themeId) HeartsPink else PremiumWhite,
                                fontWeight = if (currentTheme == themeId) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 14.sp
                            )
                            if (currentTheme == themeId) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = HeartsPink,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        if (index < themes.lastIndex) {
                            HorizontalDivider(color = PremiumMediumGray)
                        }
                    }
                }
            }

            // 4. App Info / Version
            Text(
                text = "Informasi",
                color = PremiumWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )
            
            Card(
                colors = CardDefaults.cardColors(containerColor = PremiumDarkGray),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Version",
                            tint = HeartsPink,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Versi Aplikasi",
                            color = PremiumWhite,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 15.sp
                        )
                    }
                    Text(
                        text = appVersion,
                        color = PremiumLightGray,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}
