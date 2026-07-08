with open('app/src/main/java/com/example/ui/screens/ProfileScreen.kt', 'w') as f:
    f.write("""package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
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

    var showEditProfile by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var notificationsEnabled by remember { mutableStateOf(true) }

    if (userProfile == null) {
        Box(modifier = Modifier.fillMaxSize().background(PremiumBlack), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = HeartsPink)
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
                    text = "Settings",
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
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Notifications, contentDescription = "Notifications", tint = PremiumWhite)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Push Notifications", color = PremiumWhite, fontSize = 16.sp)
                            }
                            IOSSwitch(
                                checked = notificationsEnabled,
                                onCheckedChange = { notificationsEnabled = it }
                            )
                        }
                        
                        Divider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Person, contentDescription = "Edit Profile", tint = PremiumWhite)
                                Spacer(modifier = Modifier.width(12.dp))
                                Text("Edit Profile Details", color = PremiumWhite, fontSize = 16.sp)
                            }
                            Button(
                                onClick = { showEditProfile = true },
                                colors = ButtonDefaults.buttonColors(containerColor = PremiumMediumGray),
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                                modifier = Modifier.height(32.dp).bounceClick()
                            ) {
                                Text("Edit", color = PremiumWhite, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
            
            // Logout
            item {
                Button(
                    onClick = { showLogoutDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = DestructiveRed),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp).bounceClick()
                ) {
                    Icon(Icons.Default.ExitToApp, contentDescription = "Logout", tint = PremiumWhite)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Logout", color = PremiumWhite, fontSize = 16.sp, fontWeight = FontWeight.Bold)
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
                text = { Text("Are you sure you want to log out? You will need to sign in again.", color = PremiumLightGray) },
                confirmButton = {
                    Button(
                        onClick = {
                            // In a real app, you would handle logout logic here
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
                        Text("Cancel", color = PremiumLightGray)
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
""")
