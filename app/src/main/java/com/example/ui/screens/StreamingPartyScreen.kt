package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LiveTv
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.LiveStreamSession
import com.example.ui.components.UserAvatarWithLiveBadge
import com.example.ui.theme.*
import com.example.ui.viewmodel.HeartsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StreamingPartyScreen(
    viewModel: HeartsViewModel,
    modifier: Modifier = Modifier
) {
    val activeSessions by viewModel.activeLiveSessions.collectAsStateWithLifecycle()
    val currentUserProfile by viewModel.userProfile.collectAsStateWithLifecycle()
    
    var isCreationFormVisible by remember { mutableStateOf(false) }
    var titleInput by remember { mutableStateOf("") }
    var streamUrlInput by remember { mutableStateOf("") }
    var validationError by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(PremiumBlack)
            .padding(16.dp)
    ) {
        // 1. HEADER SECTION
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.LiveTv,
                contentDescription = "Live",
                tint = HeartsPink,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = "Streaming Party",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = PremiumWhite
                )
                Text(
                    text = "Nonton bareng & obrolin live dengan fanbase Hearts2Hearts",
                    fontSize = 12.sp,
                    color = PremiumLightGray
                )
            }
        }

        // 2. HOST MODE: Create Live Stream Card
        Card(
            colors = CardDefaults.cardColors(
                containerColor = PremiumDarkGray
            ),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Mulai Kamar Live Baru 🎙️",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PremiumWhite
                    )
                    
                    Button(
                        onClick = { 
                            isCreationFormVisible = !isCreationFormVisible 
                            validationError = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isCreationFormVisible) PremiumMediumGray else HeartsPink
                        ),
                        shape = RoundedCornerShape(20.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        if (isCreationFormVisible) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Tutup",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Batal", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Mulai Live",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Buat Room", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                AnimatedVisibility(
                    visible = isCreationFormVisible,
                    enter = expandVertically(),
                    exit = shrinkVertically()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp)
                    ) {
                        OutlinedTextField(
                            value = titleInput,
                            onValueChange = { titleInput = it },
                            label = { Text("Judul Live Streaming") },
                            placeholder = { Text("cth: Sesi Reaksi Musik Carmen & Spark! 🎙️") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = PremiumWhite,
                                unfocusedTextColor = PremiumWhite,
                                focusedBorderColor = HeartsPink,
                                unfocusedBorderColor = PremiumMediumGray,
                                focusedLabelColor = HeartsPink,
                                unfocusedLabelColor = PremiumLightGray,
                                focusedContainerColor = PremiumMediumGray.copy(alpha = 0.3f),
                                unfocusedContainerColor = PremiumMediumGray.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = streamUrlInput,
                            onValueChange = { streamUrlInput = it },
                            label = { Text("Stream URL (YouTube)") },
                            placeholder = { Text("cth: https://www.youtube.com/watch?v=dQw4w9WgXcQ") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedTextColor = PremiumWhite,
                                unfocusedTextColor = PremiumWhite,
                                focusedBorderColor = HeartsPink,
                                unfocusedBorderColor = PremiumMediumGray,
                                focusedLabelColor = HeartsPink,
                                unfocusedLabelColor = PremiumLightGray,
                                focusedContainerColor = PremiumMediumGray.copy(alpha = 0.3f),
                                unfocusedContainerColor = PremiumMediumGray.copy(alpha = 0.3f)
                            ),
                            shape = RoundedCornerShape(12.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(onDone = {
                                // trigger submit
                            })
                        )

                        validationError?.let { err ->
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = err,
                                color = Color.Red,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = {
                                when {
                                    titleInput.isBlank() -> {
                                        validationError = "Judul live room tidak boleh kosong!"
                                    }
                                    streamUrlInput.isBlank() -> {
                                        validationError = "URL live stream tidak boleh kosong!"
                                    }
                                    else -> {
                                        viewModel.startHostLiveSession(titleInput, streamUrlInput)
                                        titleInput = ""
                                        streamUrlInput = ""
                                        isCreationFormVisible = false
                                        validationError = null
                                        // Navigate directly to live room
                                        viewModel.selectTab("live_room")
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = HeartsPink),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                "Mulai Sesi Live Sekarang 🚀",
                                color = PremiumWhite,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 3. VIEWER MODE: Active Rooms List
        Text(
            text = "Kamar Live Sedang Berlangsung 🔥",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = PremiumWhite,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        if (activeSessions.isEmpty()) {
            // Premium Empty State
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .background(PremiumDarkGray, RoundedCornerShape(16.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LiveTv,
                        contentDescription = "No Active Streams",
                        tint = PremiumLightGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Belum Ada Kamar Live Aktif",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PremiumWhite
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Jadilah yang pertama untuk memulai siaran langsung dan mengobrol bersama fans lainnya!",
                        fontSize = 12.sp,
                        color = PremiumLightGray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(activeSessions, key = { it.streamId }) { session ->
                    ActiveLiveSessionCard(
                        session = session,
                        onJoin = {
                            viewModel.enterLiveRoom(session.streamId)
                            viewModel.selectTab("live_room")
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ActiveLiveSessionCard(
    session: LiveStreamSession,
    onJoin: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = PremiumDarkGray
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onJoin() }
            .border(1.dp, HeartsPink.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Host profile picture with pulsing red live badge
            UserAvatarWithLiveBadge(
                userName = "Host",
                isLive = session.isActive,
                size = 56.dp
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = session.title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = PremiumWhite,
                    maxLines = 2
                )
                
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = "Viewers",
                        tint = HeartsPink,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${session.viewerCount} penonton",
                        color = PremiumLightGray,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    Icon(
                        imageVector = Icons.Default.Chat,
                        contentDescription = "Messages",
                        tint = HeartsPink,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${session.chatParticipantCount} pesan",
                        color = PremiumLightGray,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = onJoin,
                modifier = Modifier
                    .size(44.dp)
                    .background(HeartsPink, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = "Join Room",
                    tint = PremiumWhite
                )
            }
        }
    }
}
