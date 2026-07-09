package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Chat

import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AvatarBadge
import com.example.ui.components.IOSSwitch
import com.example.ui.components.bounceClick
import com.example.ui.theme.*
import com.example.ui.viewmodel.HeartsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(viewModel: HeartsViewModel) {
    val userProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    val threads by viewModel.threads.collectAsStateWithLifecycle()
    val events by viewModel.events.collectAsStateWithLifecycle()
    val context = androidx.compose.ui.platform.LocalContext.current

    var showEditProfile by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var newsNotificationsEnabled by remember { mutableStateOf(true) }
    var liveNotificationsEnabled by remember { mutableStateOf(true) }
    var isDarkMode by remember { mutableStateOf(true) }
    var themeColor by remember { mutableStateOf("Pink") }
    var selectedLanguage by remember { mutableStateOf("Bahasa Indonesia") }
    var cacheSize by remember { mutableStateOf("124 MB") }

    var showCreateProfile by remember { mutableStateOf(false) }
    var showLoginDialog by remember { mutableStateOf(false) }
    var showUsernameDialog by remember { mutableStateOf(false) }
    var showTermsDialog by remember { mutableStateOf(false) }
    var showPrivacyDialog by remember { mutableStateOf(false) }
    var showLicensesDialog by remember { mutableStateOf(false) }

    val isLoading by viewModel.isProfileLoading.collectAsStateWithLifecycle()
    val profileError by viewModel.profileError.collectAsStateWithLifecycle()

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize().background(PremiumBlack), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = HeartsPink)
        }
        return
    }

    if (userProfile == null || profileError != null) {
        Box(modifier = Modifier.fillMaxSize().background(PremiumBlack), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(16.dp)) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Not Logged In",
                    tint = PremiumMediumGray,
                    modifier = Modifier.size(64.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = profileError ?: "Silakan login untuk melihat profil Anda.",
                    color = PremiumWhite,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Button(
                        onClick = { showLoginDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = PremiumDarkGray)
                    ) {
                        Text("Login", color = PremiumWhite)
                    }
                    Button(
                        onClick = { showCreateProfile = true },
                        colors = ButtonDefaults.buttonColors(containerColor = HeartsPink)
                    ) {
                        Text("Buat Profil Baru", color = PremiumWhite)
                    }
                }
            }
        }
        
        if (showLoginDialog) {
            LoginDialog(
                onDismiss = { showLoginDialog = false },
                onLogin = { username ->
                    viewModel.login(username)
                    showLoginDialog = false
                }
            )
        }
        
        if (showCreateProfile) {
            EditProfileDialog(
                currentName = "",
                currentBio = "",
                currentBias = "",
                onDismiss = { showCreateProfile = false },
                onSave = { name, bio, bias ->
                    if (name.isNotBlank() && bias.isNotBlank()) {
                        viewModel.updateUserProfile(
                            name = name,
                            favoriteBias = bias,
                            bio = bio
                        )
                        showCreateProfile = false
                    }
                }
            )
        }
        return
    }

    val user = userProfile!!

    val userThreadsCount = threads.count { it.author == user.name }
    val joinedEventsCount = events.count { it.isJoined }

    Box(modifier = Modifier.fillMaxSize().background(PremiumBlack)) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header Profile
            item {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(contentAlignment = Alignment.BottomEnd) {
                        AvatarBadge(avatarName = user.name.ifBlank { "User" }, size = 100.dp, fontSize = 40.sp)
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(HeartsPink)
                                .border(2.dp, PremiumBlack, CircleShape)
                                .bounceClick()
                                .clickable { showEditProfile = true },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit Profile", tint = PremiumWhite, modifier = Modifier.size(16.dp))
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = user.name.ifBlank { "Guest User" },
                        color = PremiumWhite,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = user.title.ifBlank { "New Member" },
                        color = HeartsPink,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "UUID: ${user.deviceUuid.ifBlank { "N/A" }}",
                        color = PremiumLightGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Normal
                    )
                    
                    Spacer(modifier = Modifier.height(6.dp))
                    if (user.isLive) {
                        Surface(
                            color = DestructiveRed,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "● LIVE STREAMING",
                                color = PremiumWhite,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    } else {
                        Surface(
                            color = PremiumMediumGray.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = "BELUM ADA SIARAN",
                                color = PremiumLightGray,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                    
                    if (user.bio.isNotBlank()) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = user.bio,
                            color = PremiumLightGray,
                            fontSize = 14.sp,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                    }
                }
            }

            // Stats
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    StatItem(label = "Threads", value = userThreadsCount.toString())
                    StatItem(label = "Events Joined", value = joinedEventsCount.toString())
                    StatItem(label = "Bias", value = user.favoriteBias.ifBlank { "TBA" })
                }
            }

            // Settings
            item {
                Text(
                    text = "Account",
                    color = PremiumWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
                )
                
                Surface(
                    color = PremiumDarkGray,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsActionRow(
                            icon = Icons.Default.Person,
                            label = "Edit Profile",
                            onClick = { showEditProfile = true }
                        )
                        HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                        SettingsActionRow(
                            icon = Icons.Default.AccountCircle,
                            label = "Change Username",
                            onClick = { showUsernameDialog = true }
                        )
                        HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showLogoutDialog = true },
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = DestructiveRed)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Logout", color = DestructiveRed, fontSize = 16.sp)
                            }
                        }
                    }
                }
            }
            
            item {
                Text(
                    text = "Appearance",
                    color = PremiumWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp, top = 8.dp)
                )
                
                Surface(
                    color = PremiumDarkGray,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsSwitchRow(
                            icon = Icons.Default.Palette,
                            label = "Dark Mode",
                            checked = isDarkMode,
                            onCheckedChange = { isDarkMode = it }
                        )
                        HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                        SettingsActionRow(
                            icon = Icons.Default.ColorLens,
                            label = "Theme Color",
                            onClick = { 
                                themeColor = when(themeColor) {
                                    "Pink" -> "Purple"
                                    "Purple" -> "Blue"
                                    else -> "Pink"
                                }
                            },
                            value = themeColor
                        )
                    }
                }
            }
            
            item {
                Text(
                    text = "Notifications",
                    color = PremiumWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp, top = 8.dp)
                )
                
                Surface(
                    color = PremiumDarkGray,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsSwitchRow(
                            icon = Icons.Default.Notifications,
                            label = "Push Notifications",
                            checked = notificationsEnabled,
                            onCheckedChange = { notificationsEnabled = it }
                        )
                        HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                        SettingsSwitchRow(
                            icon = Icons.Default.Notifications,
                            label = "News Notifications",
                            checked = newsNotificationsEnabled,
                            onCheckedChange = { newsNotificationsEnabled = it }
                        )
                        HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                        SettingsSwitchRow(
                            icon = Icons.Default.Notifications,
                            label = "Live Notifications",
                            checked = liveNotificationsEnabled,
                            onCheckedChange = { liveNotificationsEnabled = it }
                        )
                    }
                }
            }
            
            item {
                Text(
                    text = "Language",
                    color = PremiumWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp, top = 8.dp)
                )
                
                Surface(
                    color = PremiumDarkGray,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsActionRow(
                            icon = Icons.Default.Language,
                            label = "English",
                            onClick = { selectedLanguage = "English" },
                            value = if (selectedLanguage == "English") "Selected" else ""
                        )
                        HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                        SettingsActionRow(
                            icon = Icons.Default.Language,
                            label = "Bahasa Indonesia",
                            onClick = { selectedLanguage = "Bahasa Indonesia" },
                            value = if (selectedLanguage == "Bahasa Indonesia") "Selected" else ""
                        )
                        HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                        SettingsActionRow(
                            icon = Icons.Default.Language,
                            label = "Korean",
                            onClick = { selectedLanguage = "Korean" },
                            value = if (selectedLanguage == "Korean") "Selected" else ""
                        )
                    }
                }
            }
            
            item {
                Text(
                    text = "Data",
                    color = PremiumWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp, top = 8.dp)
                )
                
                Surface(
                    color = PremiumDarkGray,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsActionRow(
                            icon = Icons.Default.Delete,
                            label = "Clear Cache",
                            onClick = { 
                                viewModel.clearLocalUpdates()
                                cacheSize = "0 MB"
                                android.widget.Toast.makeText(context, "Cache berhasil dibersihkan!", android.widget.Toast.LENGTH_SHORT).show()
                            }
                        )
                        HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                        SettingsActionRow(
                            icon = Icons.Default.Storage,
                            label = "Image Cache Size",
                            onClick = { },
                            value = cacheSize
                        )
                    }
                }
            }
            
            item {
                Text(
                    text = "About",
                    color = PremiumWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp, top = 8.dp)
                )
                
                Surface(
                    color = PremiumDarkGray,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsInfoRow(
                            icon = Icons.Default.Info,
                            label = "App Version",
                            value = "1.0.0"
                        )
                        HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                        SettingsActionRow(
                            icon = Icons.Default.Security,
                            label = "Privacy Policy",
                            onClick = { showPrivacyDialog = true }
                        )
                        HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                        SettingsActionRow(
                            icon = Icons.Default.Description,
                            label = "Terms of Service",
                            onClick = { showTermsDialog = true }
                        )
                        HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                        SettingsActionRow(
                            icon = Icons.Default.Gavel,
                            label = "Licenses",
                            onClick = { showLicensesDialog = true }
                        )
                    }
                }
            }
            
            item {
                Text(
                    text = "Fitur Live Chat Floating (Overlay)",
                    color = PremiumWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp, top = 8.dp)
                )
                
                Surface(
                    color = PremiumDarkGray,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // 1. Permission status / Request
                        val mainAct = context as? com.example.MainActivity
                        var isOverlayAllowed by remember { mutableStateOf(mainAct?.checkOverlayPermission() ?: false) }
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Security, contentDescription = "Overlay Permission", tint = HeartsPink)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Izin Overlay", color = PremiumWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text(
                                        if (isOverlayAllowed) "Sudah Diizinkan" else "Belum Diizinkan (Ketuk untuk meminta)",
                                        color = if (isOverlayAllowed) HeartsPink else PremiumLightGray,
                                        fontSize = 12.sp
                                    )
                                }
                            }
                            if (!isOverlayAllowed) {
                                Button(
                                    onClick = {
                                        mainAct?.requestOverlayPermission()
                                        // Simple polling check
                                        isOverlayAllowed = mainAct?.checkOverlayPermission() ?: false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = HeartsPink),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("Izinkan", color = PremiumWhite, fontSize = 12.sp)
                                }
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Allowed",
                                    tint = HeartsPink,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        
                        HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                        
                        // 2. Start / Stop Service
                        var isServiceRunning by remember { mutableStateOf(false) }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.Chat, contentDescription = "Floating Service", tint = HeartsPink)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text("Floating Live Chat Overlay", color = PremiumWhite, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text("Menampilkan jendela obrolan melayang di atas aplikasi lain", color = PremiumLightGray, fontSize = 12.sp)
                                }
                            }
                            Switch(
                                checked = isServiceRunning,
                                onCheckedChange = { start ->
                                    isOverlayAllowed = mainAct?.checkOverlayPermission() ?: false
                                    if (!isOverlayAllowed) {
                                        android.widget.Toast.makeText(context, "Harap izinkan Izin Overlay terlebih dahulu!", android.widget.Toast.LENGTH_SHORT).show()
                                        return@Switch
                                    }
                                    isServiceRunning = start
                                    val intent = android.content.Intent(context, com.example.data.service.FloatingChatService::class.java)
                                    if (start) {
                                        context.startService(intent)
                                        android.widget.Toast.makeText(context, "Floating Live Chat Dimulai!", android.widget.Toast.LENGTH_SHORT).show()
                                    } else {
                                        context.stopService(intent)
                                        android.widget.Toast.makeText(context, "Floating Live Chat Dihentikan!", android.widget.Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = PremiumWhite,
                                    checkedTrackColor = HeartsPink,
                                    uncheckedThumbColor = PremiumLightGray,
                                    uncheckedTrackColor = PremiumMediumGray
                                )
                            )
                        }
                    }
                }
            }
            
            item {
                Text(
                    text = "Developer Options",
                    color = PremiumWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, bottom = 8.dp, top = 8.dp)
                )
                
                Surface(
                    color = PremiumDarkGray,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        SettingsSwitchRow(
                            icon = Icons.Default.Build,
                            label = "Admin Simulator Mode",
                            checked = user.role == "admin",
                            onCheckedChange = { isSimulatedAdmin ->
                                viewModel.updateUserProfile(
                                    name = user.name,
                                    favoriteBias = user.favoriteBias,
                                    bio = user.bio,
                                    role = if (isSimulatedAdmin) "admin" else "user"
                                )
                                android.widget.Toast.makeText(
                                    context,
                                    if (isSimulatedAdmin) "Admin Mode Diaktifkan!" else "Admin Mode Dinonaktifkan!",
                                    android.widget.Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
        
        // Edit Profile Sheet
        if (showEditProfile) {
            EditProfileDialog(
                currentName = user.name,
                currentBio = user.bio,
                currentBias = user.favoriteBias,
                onDismiss = { showEditProfile = false },
                onSave = { name, bio, bias ->
                    if (name.isNotBlank() && bias.isNotBlank()) {
                        viewModel.updateUserProfile(
                            name = name,
                            favoriteBias = bias,
                            bio = bio
                        )
                        showEditProfile = false
                    }
                }
            )
        }
        
        // Logout Dialog
        if (showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { showLogoutDialog = false },
                containerColor = PremiumDarkGray,
                shape = RoundedCornerShape(20.dp),
                title = { Text("Logout", color = PremiumWhite, fontWeight = FontWeight.Bold) },
                text = { Text("Apakah Anda yakin ingin keluar? Anda harus membuat profil baru untuk masuk kembali.", color = PremiumLightGray) },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.logout()
                            showLogoutDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = DestructiveRed),
                        modifier = Modifier.bounceClick()
                    ) {
                        Text("Logout", color = PremiumWhite)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLogoutDialog = false }) {
                        Text("Batal", color = PremiumLightGray)
                    }
                }
            )
        }

        // Change Username Dialog
        if (showUsernameDialog) {
            var tempUsername by remember { mutableStateOf(user.name) }
            AlertDialog(
                onDismissRequest = { showUsernameDialog = false },
                containerColor = PremiumDarkGray,
                shape = RoundedCornerShape(20.dp),
                title = { Text("Ubah Nama Pengguna", color = PremiumWhite, fontWeight = FontWeight.Bold) },
                text = {
                    Column {
                        Text("Masukkan nama pengguna baru Anda:", color = PremiumLightGray, modifier = Modifier.padding(bottom = 12.dp))
                        ProfileTextField(
                            value = tempUsername,
                            onValueChange = { tempUsername = it },
                            placeholder = "Nama Pengguna"
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (tempUsername.isNotBlank()) {
                                viewModel.updateUserProfile(
                                    name = tempUsername,
                                    favoriteBias = user.favoriteBias,
                                    bio = user.bio
                                )
                                showUsernameDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = HeartsPink),
                        enabled = tempUsername.isNotBlank(),
                        modifier = Modifier.bounceClick()
                    ) {
                        Text("Simpan", color = PremiumWhite)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showUsernameDialog = false }) {
                        Text("Batal", color = PremiumLightGray)
                    }
                }
            )
        }

        // Privacy Policy Dialog
        if (showPrivacyDialog) {
            AlertDialog(
                onDismissRequest = { showPrivacyDialog = false },
                containerColor = PremiumDarkGray,
                shape = RoundedCornerShape(20.dp),
                title = { Text("Kebijakan Privasi", color = PremiumWhite, fontWeight = FontWeight.Bold) },
                text = {
                    Box(modifier = Modifier.heightIn(max = 300.dp)) {
                        Text(
                            text = "Kebijakan Privasi Hearts2Hearts Fan Club:\n\n1. Kami berkomitmen untuk melindungi data pribadi Anda.\n2. Seluruh profil dan aktivitas Anda hanya disimpan secara lokal dalam basis data terenkripsi perangkat ini.\n3. Kami tidak mengumpulkan, menjual, atau membagikan informasi pribadi Anda kepada pihak ketiga.\n4. Integrasi YouTube API digunakan murni untuk menampilkan statistik tontonan video secara aktual.",
                            color = PremiumLightGray,
                            lineHeight = 20.sp,
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showPrivacyDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = HeartsPink)
                    ) {
                        Text("Tutup", color = PremiumWhite)
                    }
                }
            )
        }

        // Terms of Service Dialog
        if (showTermsDialog) {
            AlertDialog(
                onDismissRequest = { showTermsDialog = false },
                containerColor = PremiumDarkGray,
                shape = RoundedCornerShape(20.dp),
                title = { Text("Syarat dan Ketentuan", color = PremiumWhite, fontWeight = FontWeight.Bold) },
                text = {
                    Box(modifier = Modifier.heightIn(max = 300.dp)) {
                        Text(
                            text = "Syarat dan Ketentuan Penggunaan:\n\n1. Layanan aplikasi Hearts2Hearts Fan Club ditujukan murni untuk koordinasi komunitas dan media tontonan para penggemar.\n2. Dilarang memposting konten negatif, merusak, kasar, atau melanggar hak cipta di bagian forum diskusi.\n3. Moderasi AI berbasis Gemini digunakan secara berkala untuk menjaga kebersihan konten komunitas.\n4. Pelanggaran berat dapat berakibat penonaktifan atau pemblokiran profil lokal Anda.",
                            color = PremiumLightGray,
                            lineHeight = 20.sp,
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showTermsDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = HeartsPink)
                    ) {
                        Text("Tutup", color = PremiumWhite)
                    }
                }
            )
        }

        // Licenses Dialog
        if (showLicensesDialog) {
            AlertDialog(
                onDismissRequest = { showLicensesDialog = false },
                containerColor = PremiumDarkGray,
                shape = RoundedCornerShape(20.dp),
                title = { Text("Lisensi Perangkat Lunak", color = PremiumWhite, fontWeight = FontWeight.Bold) },
                text = {
                    Box(modifier = Modifier.heightIn(max = 300.dp)) {
                        Text(
                            text = "Aplikasi ini menggunakan pustaka pihak ketiga berlisensi open source:\n\n- Android Jetpack & Jetpack Compose (Apache 2.0)\n- Room Database Persistence Library (Apache 2.0)\n- Retrofit & OkHttp HTTP Client (Apache 2.0)\n- Coil Image Loading (Apache 2.0)\n- Google Gemini API SDK (Google SDK License)\n- Material Design Components (Apache 2.0)\n- YouTube Android Player (Google Play Services Terms)",
                            color = PremiumLightGray,
                            lineHeight = 20.sp,
                            modifier = Modifier.verticalScroll(rememberScrollState())
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { showLicensesDialog = false },
                        colors = ButtonDefaults.buttonColors(containerColor = HeartsPink)
                    ) {
                        Text("Tutup", color = PremiumWhite)
                    }
                }
            )
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(text = value, color = PremiumWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = label, color = PremiumLightGray, fontSize = 12.sp)
    }
}

@Composable
fun EditProfileDialog(
    currentName: String,
    currentBio: String,
    currentBias: String,
    onDismiss: () -> Unit,
    onSave: (String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(currentName) }
    var bio by remember { mutableStateOf(currentBio) }
    var bias by remember { mutableStateOf(currentBias) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = PremiumDarkGray,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Edit Profile", color = PremiumWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    IconButton(onClick = onDismiss, modifier = Modifier.size(24.dp)) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = PremiumLightGray)
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Name Input
                Text("Display Name", color = PremiumLightGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                ProfileTextField(
                    value = name,
                    onValueChange = { name = it },
                    placeholder = "Enter your name"
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Bias Input
                Text("Favorite Bias", color = PremiumLightGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                ProfileTextField(
                    value = bias,
                    onValueChange = { bias = it },
                    placeholder = "Who is your bias?"
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Bio Input
                Text("Bio", color = PremiumLightGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                ProfileTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    placeholder = "Tell us about yourself",
                    singleLine = false,
                    modifier = Modifier.height(80.dp)
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = { onSave(name, bio, bias) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (name.isNotBlank() && bias.isNotBlank()) HeartsPink else PremiumMediumGray,
                        contentColor = if (name.isNotBlank() && bias.isNotBlank()) PremiumWhite else PremiumLightGray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).bounceClick(),
                    enabled = name.isNotBlank() && bias.isNotBlank()
                ) {
                    Text("Save Changes", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    singleLine: Boolean = true,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(PremiumMediumGray)
            .border(1.dp, PremiumBlack, RoundedCornerShape(12.dp))
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        if (value.isEmpty()) {
            Text(placeholder, color = PremiumLightGray.copy(alpha = 0.5f), fontSize = 15.sp)
        }
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(color = PremiumWhite, fontSize = 15.sp),
            cursorBrush = SolidColor(HeartsPink),
            singleLine = singleLine,
            modifier = Modifier.fillMaxWidth()
        )
    }
}


@Composable
fun SettingsActionRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit,
    value: String = "",
    isImplemented: Boolean = true
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val displayValue = if (!isImplemented && value.isEmpty()) "Coming Soon" else value
    
    val actualOnClick = if (isImplemented) onClick else {
        { android.widget.Toast.makeText(context, "$label: Coming Soon", android.widget.Toast.LENGTH_SHORT).show() }
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { actualOnClick() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = label, tint = if (isImplemented) PremiumWhite else PremiumLightGray, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, color = if (isImplemented) PremiumWhite else PremiumLightGray, fontSize = 16.sp)
        }
        if (displayValue.isNotEmpty()) {
            Text(displayValue, color = PremiumLightGray, fontSize = 14.sp)
        }
    }
}

@Composable
fun SettingsSwitchRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    isImplemented: Boolean = true
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    
    val actualOnCheckedChange = if (isImplemented) onCheckedChange else {
        { _: Boolean -> android.widget.Toast.makeText(context, "$label: Coming Soon", android.widget.Toast.LENGTH_SHORT).show() }
    }
    
    Row(
        modifier = Modifier.fillMaxWidth().clickable(enabled = !isImplemented) { actualOnCheckedChange(!checked) },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = label, tint = if (isImplemented) PremiumWhite else PremiumLightGray, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, color = if (isImplemented) PremiumWhite else PremiumLightGray, fontSize = 16.sp)
        }
        if (!isImplemented) {
            Text("Coming Soon", color = PremiumLightGray, fontSize = 14.sp)
        } else {
            IOSSwitch(
                checked = checked,
                onCheckedChange = actualOnCheckedChange
            )
        }
    }
}

@Composable
fun SettingsInfoRow(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = label, tint = PremiumWhite)
            Spacer(modifier = Modifier.width(12.dp))
            Text(label, color = PremiumWhite, fontSize = 16.sp)
        }
        Text(value, color = PremiumLightGray, fontSize = 14.sp)
    }
}


@Composable
fun LoginDialog(
    onDismiss: () -> Unit,
    onLogin: (String) -> Unit
) {
    var username by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PremiumDarkGray,
        title = { Text("Login", color = PremiumWhite, fontWeight = FontWeight.Bold) },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Masukkan username Anda:", color = PremiumLightGray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = username,
                    onValueChange = { username = it },
                    label = { Text("Username", color = PremiumLightGray) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = HeartsPink,
                        unfocusedBorderColor = PremiumMediumGray,
                        focusedTextColor = PremiumWhite,
                        unfocusedTextColor = PremiumWhite
                    ),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text("Developer? Gunakan username 'developer' atau 'pcool180399@gmail.com'.", color = PremiumMediumGray, fontSize = 11.sp)
            }
        },
        confirmButton = {
            Button(
                onClick = { if (username.isNotBlank()) onLogin(username.trim()) },
                colors = ButtonDefaults.buttonColors(containerColor = HeartsPink)
            ) {
                Text("Masuk", color = PremiumWhite)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Batal", color = PremiumLightGray)
            }
        }
    )
}
