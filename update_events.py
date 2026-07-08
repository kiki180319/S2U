with open('app/src/main/java/com/example/ui/screens/EventsScreen.kt', 'w') as f:
    f.write("""package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.People
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
import com.example.data.database.EventAttendeeEntity
import com.example.data.database.EventCommentEntity
import com.example.data.database.EventEntity
import com.example.ui.components.UserAvatarBadge
import com.example.ui.components.bounceClick
import com.example.ui.theme.*
import com.example.ui.viewmodel.HeartsViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun EventsScreen(viewModel: HeartsViewModel) {
    val events by viewModel.events.collectAsStateWithLifecycle()
    val selectedEvent by viewModel.selectedEvent.collectAsStateWithLifecycle()
    val comments by viewModel.eventComments.collectAsStateWithLifecycle()
    val attendees by viewModel.eventAttendees.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize().background(PremiumBlack)) {
        AnimatedContent(
            targetState = selectedEvent,
            transitionSpec = {
                if (targetState != null) {
                    slideInHorizontally { width -> width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> -width } + fadeOut()
                } else {
                    slideInHorizontally { width -> -width } + fadeIn() togetherWith
                            slideOutHorizontally { width -> width } + fadeOut()
                }
            },
            label = "EventNavigation"
        ) { event ->
            if (event == null) {
                EventListScreen(
                    events = events,
                    onEventClick = { viewModel.selectEvent(it) },
                    onJoinEvent = { viewModel.toggleJoinEvent(it) }
                )
            } else {
                EventDetailScreen(
                    event = event,
                    comments = comments,
                    attendees = attendees,
                    onBack = { viewModel.selectEvent(null) },
                    onJoinEvent = { viewModel.toggleJoinEvent(event) },
                    onSendComment = { content -> viewModel.postEventComment(event.id, content) }
                )
            }
        }
    }
}

@Composable
fun EventListScreen(
    events: List<EventEntity>,
    onEventClick: (EventEntity) -> Unit,
    onJoinEvent: (EventEntity) -> Unit
) {
    if (events.isEmpty()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Event,
                contentDescription = "No events",
                tint = PremiumMediumGray,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Belum ada acara mendatang, nantikan info selanjutnya!",
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
            items(events, key = { it.id }) { event ->
                EventCard(
                    event = event,
                    onClick = { onEventClick(event) },
                    onJoin = { onJoinEvent(event) }
                )
            }
            item { Spacer(modifier = Modifier.height(80.dp)) }
        }
    }
}

@Composable
fun EventCard(
    event: EventEntity,
    onClick: () -> Unit,
    onJoin: () -> Unit
) {
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
                Surface(
                    color = HeartsPink.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = event.category,
                        color = HeartsPink,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Date",
                        tint = PremiumLightGray,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = event.date,
                        color = PremiumLightGray,
                        fontSize = 12.sp
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = event.title,
                color = PremiumWhite,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Location",
                    tint = PremiumMediumGray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = event.location,
                    color = PremiumLightGray,
                    fontSize = 14.sp
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Divider(color = PremiumMediumGray, thickness = 0.5.dp)
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.People,
                        contentDescription = "Attendees",
                        tint = PremiumLightGray,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${event.joinedCount} Joined",
                        color = PremiumWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                
                Button(
                    onClick = onJoin,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (event.isJoined) PremiumMediumGray else HeartsPink,
                        contentColor = PremiumWhite
                    ),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(
                        text = if (event.isJoined) "Joined" else "Join Event",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun EventDetailScreen(
    event: EventEntity,
    comments: List<EventCommentEntity>,
    attendees: List<EventAttendeeEntity>,
    onBack: () -> Unit,
    onJoinEvent: () -> Unit,
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
                text = "Detail Event",
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
            item {
                EventCard(event = event, onClick = {}, onJoin = onJoinEvent)
            }
            
            item {
                Text(
                    text = "Tentang Acara",
                    color = PremiumWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, top = 8.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = event.description,
                    color = PremiumLightGray,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
            
            if (attendees.isNotEmpty()) {
                item {
                    Text(
                        text = "Attendees (${attendees.size})",
                        color = PremiumWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 4.dp, top = 16.dp, bottom = 8.dp)
                    )
                    
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 4.dp)
                    ) {
                        items(attendees, key = { it.id }) { attendee ->
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                UserAvatarBadge(userName = attendee.userName, size = 48.dp, fontSize = 20.sp)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = attendee.userName.take(10), // Limit display length
                                    color = PremiumLightGray,
                                    fontSize = 12.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
            
            item {
                Text(
                    text = "Diskusi",
                    color = PremiumWhite,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(start = 4.dp, top = 16.dp, bottom = 8.dp)
                )
            }
            
            if (comments.isEmpty()) {
                item {
                    Text(
                        text = "Belum ada diskusi untuk event ini.",
                        color = PremiumLightGray,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            } else {
                items(comments, key = { it.id }) { comment ->
                    EventCommentCard(comment)
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
                        Text("Tanya sesuatu...", color = PremiumLightGray, fontSize = 15.sp)
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
fun EventCommentCard(comment: EventCommentEntity) {
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
                    Text(
                        text = comment.author,
                        color = PremiumWhite,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
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
""")
