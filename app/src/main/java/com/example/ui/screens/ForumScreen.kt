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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.Forum
import androidx.compose.material.icons.filled.Send
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
                    onUpvote = { viewModel.toggleUpvoteThread(it) }
                )
            } else {
                ThreadDetailScreen(
                    thread = thread,
                    comments = comments,
                    onBack = { viewModel.selectThread(null) },
                    onUpvote = { viewModel.toggleUpvoteThread(thread) },
                    onSendComment = { content -> viewModel.postComment(thread.id, content) }
                )
            }
        }
    }
}

@Composable
fun ForumListScreen(
    threads: List<ForumEntity>,
    onThreadClick: (ForumEntity) -> Unit,
    onUpvote: (ForumEntity) -> Unit
) {
    if (threads.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
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
                fontSize = 18.sp
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(threads, key = { it.id }) { thread ->
                ThreadCard(
                    thread = thread,
                    onClick = { onThreadClick(thread) },
                    onUpvote = { onUpvote(thread) }
                )
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
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
                UserAvatarBadge(userName = thread.author, size = 32.dp, fontSize = 14.sp)
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
    onSendComment: (String) -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    
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
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp).fillMaxWidth(),
                verticalAlignment = Alignment.Bottom
            ) {
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
                
                Box(
                    modifier = Modifier
                        .padding(bottom = 2.dp)
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(if (commentText.isNotBlank()) HeartsPink else PremiumMediumGray)
                        .clickable {
                            if (commentText.isNotBlank()) {
                                onSendComment(commentText)
                                commentText = ""
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

@Composable
fun CommentCard(comment: CommentEntity) {
    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val dateString = dateFormat.format(Date(comment.timestamp))

    Row(modifier = Modifier.fillMaxWidth()) {
        UserAvatarBadge(userName = comment.author, size = 32.dp, fontSize = 14.sp)
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
                    Text(
                        text = comment.content,
                        color = PremiumLightGray,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )
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
