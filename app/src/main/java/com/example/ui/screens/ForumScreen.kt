package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.database.CommentEntity
import com.example.data.database.ForumEntity
import com.example.ui.components.UserAvatarBadge
import com.example.ui.components.UserAvatarWithLiveBadge
import com.example.ui.components.bounceClick
import com.example.ui.theme.*
import com.example.ui.viewmodel.HeartsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ForumScreen(viewModel: HeartsViewModel) {
    val threads by viewModel.threads.collectAsStateWithLifecycle()
    val selectedThread by viewModel.selectedThread.collectAsStateWithLifecycle()
    val comments by viewModel.threadComments.collectAsStateWithLifecycle()
    
    var showCreateThreadDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(PremiumBlack)) {
        AnimatedContent(
            targetState = selectedThread,
            transitionSpec = {
                if (targetState != null) {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                } else {
                    slideInHorizontally { width -> -width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> width } + fadeOut()
                }
            },
            label = "ForumNavigation"
        ) { thread ->
            if (thread == null) {
                ForumListScreen(
                    threads = threads,
                    onThreadClick = { viewModel.selectThread(it) },
                    onUpvote = { viewModel.toggleUpvoteThread(it) },
                    onLiveChatClick = {
                        val activeSession = viewModel.activeLiveSessions.value.firstOrNull()
                        val streamId = activeSession?.streamId ?: "live_default_carmen"
                        viewModel.enterLiveRoom(streamId)
                        viewModel.selectTab("live_room")
                    }
                )
            } else {
                ThreadDetailScreen(
                    thread = thread,
                    comments = comments,
                    onBack = { viewModel.selectThread(null) },
                    onUpvote = { viewModel.toggleUpvoteThread(thread) },
                    onSendComment = { content, stickerId -> viewModel.postComment(thread.id, content, stickerId) }
                )
            }
        }

        if (selectedThread == null) {
            FloatingActionButton(
                onClick = { showCreateThreadDialog = true },
                containerColor = HeartsPink,
                contentColor = PremiumWhite,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .padding(bottom = 72.dp) // Offset bottom bar/navigation height
                    .bounceClick()
            ) {
                Icon(Icons.Default.Add, contentDescription = "Buat Diskusi Baru")
            }
        }

        if (showCreateThreadDialog) {
            CreateThreadDialog(
                viewModel = viewModel,
                onDismiss = { showCreateThreadDialog = false }
            )
        }
    }
}

@Composable
fun ForumListScreen(
    threads: List<ForumEntity>,
    onThreadClick: (ForumEntity) -> Unit,
    onUpvote: (ForumEntity) -> Unit,
    onLiveChatClick: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            LiveChatCommunityBanner(onClick = onLiveChatClick)
        }
        
        if (threads.isEmpty()) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Forum,
                        contentDescription = "No threads",
                        tint = PremiumMediumGray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Belum ada diskusi, mulai yang pertama!",
                        color = PremiumWhite,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            }
        } else {
            items(threads, key = { it.id }) { thread ->
                ThreadCard(
                    thread = thread,
                    onClick = { onThreadClick(thread) },
                    onUpvote = { onUpvote(thread) }
                )
            }
        }
        item { Spacer(modifier = Modifier.height(80.dp)) }
    }
}

@Composable
fun LiveChatCommunityBanner(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = PremiumDarkGray),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = HeartsPink,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = "● LIVE CHAT",
                            color = PremiumWhite,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Live Chat Komunitas",
                        color = PremiumLightGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Obrolan Komunitas Real-Time",
                    color = PremiumWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Bergabung ke ruang obrolan global, saksikan live streaming, dan berinteraksi bersama fandom Hearts2Hearts secara real-time!",
                    color = PremiumLightGray,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Icon(
                imageVector = Icons.Default.Chat,
                contentDescription = "Masuk Live Chat",
                tint = HeartsPink,
                modifier = Modifier.size(36.dp)
            )
        }
    }
}

@Composable
fun ThreadCard(
    thread: ForumEntity,
    onClick: () -> Unit,
    onUpvote: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(thread.timestamp))

    Surface(
        color = PremiumDarkGray,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .bounceClick()
            .clickable { onClick() }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                UserAvatarWithLiveBadge(userName = thread.author, isLive = (thread.author == "Carmen"), size = 32.dp, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = thread.author,
                        color = PremiumWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = dateString,
                        color = PremiumLightGray,
                        fontSize = 12.sp
                    )
                }
                Surface(
                    color = HeartsPink.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = thread.category,
                        color = HeartsPink,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = thread.title,
                color = PremiumWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = thread.content,
                color = PremiumLightGray,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            HorizontalDivider(color = PremiumMediumGray, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .bounceClick()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onUpvote() }
                        .padding(end = 12.dp, top = 4.dp, bottom = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowUpward,
                        contentDescription = "Upvote",
                        tint = if (thread.isUpvoted) HeartsPink else PremiumLightGray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${thread.upvotes}",
                        color = if (thread.isUpvoted) HeartsPink else PremiumLightGray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(end = 12.dp, top = 4.dp, bottom = 4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comments",
                        tint = PremiumLightGray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Reply", // You can calculate actual comments count if available from DB, otherwise simply 'Reply'
                        color = PremiumLightGray,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun ThreadDetailScreen(
    thread: ForumEntity,
    comments: List<CommentEntity>,
    onBack: () -> Unit,
    onUpvote: () -> Unit,
    onSendComment: (String, String?) -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    var selectedStickerId by remember { mutableStateOf<String?>(null) }
    var showStickerPanel by remember { mutableStateOf(false) }

    val stickers = com.example.utils.StickerManager.getAvailableStickers()
    
    BackHandler { onBack() }

    Column(modifier = Modifier.fillMaxSize().background(PremiumBlack)) {
        // Top Bar
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp)
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PremiumWhite)
            }
            Text(
                text = "Thread",
                color = PremiumWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )
        }
        
        LazyColumn(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Main Thread Content
            item {
                ThreadCard(thread = thread, onClick = {}, onUpvote = onUpvote)
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Comments",
                    color = PremiumWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, top = 16.dp, bottom = 8.dp)
                )
            }
            
            // Comments List
            if (comments.isEmpty()) {
                item {
                    Text(
                        text = "Belum ada komentar. Jadilah yang pertama!",
                        color = PremiumLightGray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            } else {
                items(comments, key = { it.id }) { comment ->
                    CommentCard(comment)
                }
            }
        }
        
        // Comment Input
        Surface(
            color = PremiumDarkGray,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                
                // 1. Sticker Whitelist Selection Panel
                if (showStickerPanel) {
                    Text(
                        text = "Pilih Stiker Whitelist:",
                        color = PremiumLightGray,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 16.dp, top = 8.dp, bottom = 4.dp)
                    )
                    androidx.compose.foundation.lazy.LazyRow(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(stickers) { sticker ->
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(PremiumMediumGray)
                                    .clickable {
                                        selectedStickerId = sticker.id
                                        showStickerPanel = false
                                    },
                                contentAlignment = Alignment.Center
                            ) {
                                com.example.utils.StickerImage(stickerId = sticker.id, modifier = Modifier.size(44.dp))
                            }
                        }
                    }
                }

                // 2. Selected Sticker Preview
                selectedStickerId?.let { sId ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            color = PremiumMediumGray,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.padding(vertical = 4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                com.example.utils.StickerImage(stickerId = sId, modifier = Modifier.size(40.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Stiker Terpilih",
                                    color = PremiumWhite,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Hapus Stiker",
                                    tint = DestructiveRed,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable { selectedStickerId = null }
                                )
                            }
                        }
                    }
                }

                // 3. Text and Action Input Bar
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Sticker Panel Toggle Button
                    IconButton(
                        onClick = { showStickerPanel = !showStickerPanel },
                        modifier = Modifier.padding(bottom = 2.dp)
                    ) {
                        Text("😀", fontSize = 24.sp)
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(24.dp))
                            .background(PremiumMediumGray)
                            .border(1.dp, PremiumBlack, RoundedCornerShape(24.dp))
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        if (commentText.isEmpty()) {
                            Text("Tulis komentar...", color = PremiumLightGray, fontSize = 15.sp)
                        }
                        BasicTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            textStyle = TextStyle(color = PremiumWhite, fontSize = 15.sp),
                            cursorBrush = SolidColor(HeartsPink),
                            modifier = Modifier.fillMaxWidth().heightIn(min = 20.dp, max = 120.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    val isSendEnabled = commentText.isNotBlank() || selectedStickerId != null
                    Box(
                        modifier = Modifier
                            .padding(bottom = 2.dp)
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isSendEnabled) HeartsPink else PremiumMediumGray)
                            .clickable {
                                if (isSendEnabled) {
                                    onSendComment(commentText, selectedStickerId)
                                    commentText = ""
                                    selectedStickerId = null
                                    showStickerPanel = false
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = "Send",
                            tint = PremiumWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CommentCard(comment: CommentEntity) {
    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(comment.timestamp))

    Row(modifier = Modifier.fillMaxWidth()) {
        UserAvatarWithLiveBadge(userName = comment.author, isLive = (comment.author == "Carmen"), size = 32.dp, fontSize = 14.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Surface(
                color = PremiumDarkGray,
                shape = RoundedCornerShape(
                    topStart = 4.dp,
                    topEnd = 16.dp,
                    bottomStart = 16.dp,
                    bottomEnd = 16.dp
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = comment.author,
                            color = PremiumWhite,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        text = comment.authorTitle,
                        color = HeartsPink,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    if (comment.content.isNotEmpty()) {
                        Text(
                            text = comment.content,
                            color = PremiumLightGray,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                    comment.stickerId?.let { sId ->
                        Spacer(modifier = Modifier.height(8.dp))
                        com.example.utils.StickerImage(stickerId = sId, modifier = Modifier.size(72.dp))
                    }
                }
            }
            Text(
                text = dateString,
                color = PremiumMediumGray,
                fontSize = 11.sp,
                modifier = Modifier.padding(start = 4.dp, top = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateThreadDialog(
    viewModel: HeartsViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Umum") }
    var moderationError by remember { mutableStateOf<String?>(null) }
    
    val categories = listOf("Umum", "Lore", "Teori", "Media", "Event")
    val aiLoading by viewModel.aiLoading.collectAsStateWithLifecycle()

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Surface(
            color = PremiumDarkGray,
            shape = RoundedCornerShape(24.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(androidx.compose.foundation.rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Mulai Diskusi Baru",
                        color = PremiumWhite,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Tutup",
                            tint = PremiumLightGray
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Kategori Selector (Horizontal Chips)
                Text(
                    text = "Pilih Kategori",
                    color = PremiumLightGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.forEach { category ->
                        val isSelected = selectedCategory == category
                        Surface(
                            color = if (isSelected) HeartsPink else PremiumMediumGray,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .bounceClick()
                                .clickable { selectedCategory = category }
                        ) {
                            Text(
                                text = category,
                                color = if (isSelected) PremiumWhite else PremiumLightGray,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                // Judul Input
                Text(
                    text = "Judul Diskusi",
                    color = PremiumLightGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(PremiumMediumGray)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    if (title.isEmpty()) {
                        Text("Judul diskusi Anda...", color = PremiumLightGray.copy(alpha = 0.5f), fontSize = 15.sp)
                    }
                    BasicTextField(
                        value = title,
                        onValueChange = { title = it },
                        textStyle = TextStyle(color = PremiumWhite, fontSize = 15.sp),
                        cursorBrush = SolidColor(HeartsPink),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Konten Input
                Text(
                    text = "Konten Diskusi",
                    color = PremiumLightGray,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(PremiumMediumGray)
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    if (content.isEmpty()) {
                        Text("Tulis isi diskusi Anda disini...", color = PremiumLightGray.copy(alpha = 0.5f), fontSize = 15.sp)
                    }
                    BasicTextField(
                        value = content,
                        onValueChange = { content = it },
                        textStyle = TextStyle(color = PremiumWhite, fontSize = 15.sp),
                        cursorBrush = SolidColor(HeartsPink),
                        singleLine = false,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                
                // Moderation Error Display
                if (moderationError != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = moderationError!!,
                        color = DestructiveRed,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                
                Spacer(modifier = Modifier.height(28.dp))
                
                Button(
                    onClick = {
                        viewModel.checkAndAddThread(
                            title = title,
                            content = content,
                            category = selectedCategory,
                            onModerationFailed = { error ->
                                moderationError = error
                            },
                            onSuccess = {
                                onDismiss()
                            }
                        )
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (title.isNotBlank() && content.isNotBlank() && !aiLoading) HeartsPink else PremiumMediumGray,
                        contentColor = if (title.isNotBlank() && content.isNotBlank() && !aiLoading) PremiumWhite else PremiumLightGray
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .bounceClick(),
                    enabled = title.isNotBlank() && content.isNotBlank() && !aiLoading
                ) {
                    if (aiLoading) {
                        CircularProgressIndicator(color = PremiumWhite, modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                    } else {
                        Text("Kirim Postingan", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
