package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.HeartsViewModel
import com.example.ui.theme.*
import com.example.ui.components.bounceClick

@Composable
fun AdminPanelScreen(viewModel: HeartsViewModel) {
    val isAdmin by viewModel.isAdmin.collectAsStateWithLifecycle()
    if (!isAdmin) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Lock,
                contentDescription = "Locked",
                tint = PremiumMediumGray,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Text(
                text = "Akses Ditolak",
                color = PremiumWhite,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Hanya akun dengan peran Administrator yang dapat mengakses panel kontrol ini.",
                color = PremiumLightGray,
                textAlign = TextAlign.Center,
                lineHeight = 20.sp
            )
        }
        return
    }

    val threads by viewModel.threads.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle()
    
    var activeTab by remember { mutableStateOf("stats") } // "stats", "threads", "events"
    var threadToDelete by remember { mutableStateOf<com.example.data.database.ForumEntity?>(null) }
    var eventToDelete by remember { mutableStateOf<com.example.data.database.EventEntity?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PremiumBlack)
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = "Admin Shield",
                tint = HeartsPink,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Control Center",
                    color = PremiumWhite,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Mode Simulator Administrator Aktif",
                    color = HeartsPink,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Navigation Tabs for Admin
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(PremiumDarkGray)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            listOf("stats" to "Statistik", "threads" to "Moderasi Diskusi", "events" to "Moderasi Event").forEach { (tabKey, tabLabel) ->
                val isSelected = activeTab == tabKey
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) HeartsPink else PremiumDarkGray)
                        .clickable { activeTab = tabKey }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tabLabel,
                        color = if (isSelected) PremiumWhite else PremiumLightGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Main Content Area based on selected tab
        when (activeTab) {
            "stats" -> {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        Text(
                            text = "Ringkasan Sistem",
                            color = PremiumWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            AdminStatCard(
                                modifier = Modifier.weight(1f),
                                title = "Total Diskusi",
                                value = threads.size.toString(),
                                icon = Icons.Default.Forum,
                                iconColor = HeartsPink
                            )
                            AdminStatCard(
                                modifier = Modifier.weight(1f),
                                title = "Total Event",
                                value = events.size.toString(),
                                icon = Icons.Default.Event,
                                iconColor = androidx.compose.ui.graphics.Color(0xFF4CAF50)
                            )
                        }
                    }
                    
                    item {
                        val hasGemini = com.example.BuildConfig.GEMINI_API_KEY.isNotEmpty() && com.example.BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY"
                        Surface(
                            color = PremiumDarkGray,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = "Gemini AI Status",
                                        tint = if (hasGemini) HeartsPink else PremiumLightGray,
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = "Moderasi AI Gemini",
                                            color = PremiumWhite,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (hasGemini) "Koneksi Aktif (Aman)" else "Failsafe Offline Aktif",
                                            color = PremiumLightGray,
                                            fontSize = 12.sp
                                        )
                                    }
                                }
                                Surface(
                                    color = if (hasGemini) androidx.compose.ui.graphics.Color(0xFF4CAF50).copy(alpha = 0.2f) else PremiumMediumGray,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Text(
                                        text = if (hasGemini) "AKTIF" else "LOCAL ONLY",
                                        color = if (hasGemini) androidx.compose.ui.graphics.Color(0xFF4CAF50) else PremiumLightGray,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                    
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Tindakan Cepat",
                            color = PremiumWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    item {
                        Surface(
                            color = PremiumDarkGray,
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Button(
                                    onClick = {
                                        viewModel.clearLocalUpdates()
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = PremiumMediumGray,
                                        contentColor = PremiumWhite
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier.fillMaxWidth().height(48.dp).bounceClick()
                                ) {
                                    Icon(Icons.Default.SettingsBackupRestore, contentDescription = "Reset Updates")
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Bersihkan Riwayat Update Komunitas", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
            "threads" -> {
                if (threads.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Tidak ada diskusi forum untuk dimoderasi.", color = PremiumLightGray, fontSize = 14.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(threads, key = { it.id }) { thread ->
                            Surface(
                                color = PremiumDarkGray,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                color = HeartsPink.copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = thread.category,
                                                    color = HeartsPink,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "oleh " + thread.author,
                                                color = PremiumLightGray,
                                                fontSize = 11.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = thread.title,
                                            color = PremiumWhite,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = thread.content,
                                            color = PremiumLightGray,
                                            fontSize = 12.sp,
                                            maxLines = 1
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    IconButton(
                                        onClick = { threadToDelete = thread },
                                        colors = IconButtonDefaults.iconButtonColors(
                                            contentColor = DestructiveRed
                                        )
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Hapus Diskusi")
                                    }
                                }
                            }
                        }
                    }
                }
            }
            "events" -> {
                if (events.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize().padding(32.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Tidak ada event untuk dimoderasi.", color = PremiumLightGray, fontSize = 14.sp)
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(events, key = { it.id }) { event ->
                            Surface(
                                color = PremiumDarkGray,
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Surface(
                                                color = androidx.compose.ui.graphics.Color(0xFF4CAF50).copy(alpha = 0.2f),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                Text(
                                                    text = event.category,
                                                    color = androidx.compose.ui.graphics.Color(0xFF4CAF50),
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = event.date,
                                                color = PremiumLightGray,
                                                fontSize = 11.sp
                                            )
                                        }
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = event.title,
                                            color = PremiumWhite,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = event.location,
                                            color = PremiumLightGray,
                                            fontSize = 12.sp,
                                            maxLines = 1
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    IconButton(
                                        onClick = { eventToDelete = event },
                                        colors = IconButtonDefaults.iconButtonColors(
                                            contentColor = DestructiveRed
                                        )
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Hapus Event")
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Thread Delete Confirmation Dialog
    if (threadToDelete != null) {
        AlertDialog(
            onDismissRequest = { threadToDelete = null },
            title = { Text("Hapus Diskusi Forum?", color = PremiumWhite, fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus diskusi ini? Tindakan ini permanen dan tidak dapat dibatalkan.", color = PremiumLightGray) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteThread(threadToDelete!!.id)
                        threadToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DestructiveRed, contentColor = PremiumWhite)
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { threadToDelete = null }) {
                    Text("Batal", color = PremiumWhite)
                }
            },
            containerColor = PremiumDarkGray
        )
    }

    // Event Delete Confirmation Dialog
    if (eventToDelete != null) {
        AlertDialog(
            onDismissRequest = { eventToDelete = null },
            title = { Text("Hapus Event Komunitas?", color = PremiumWhite, fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus event ini? Tindakan ini permanen dan tidak dapat dibatalkan.", color = PremiumLightGray) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteEvent(eventToDelete!!.id)
                        eventToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = DestructiveRed, contentColor = PremiumWhite)
                ) {
                    Text("Hapus", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { eventToDelete = null }) {
                    Text("Batal", color = PremiumWhite)
                }
            },
            containerColor = PremiumDarkGray
        )
    }
}

@Composable
fun AdminStatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: androidx.compose.ui.graphics.Color
) {
    Surface(
        color = PremiumDarkGray,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = iconColor,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = value,
                color = PremiumWhite,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                color = PremiumLightGray,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
