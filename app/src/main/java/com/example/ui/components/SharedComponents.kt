package com.example.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.UserEntity
import com.example.ui.theme.*

@Composable
fun AvatarBadge(avatarName: String, size: Dp = 40.dp, fontSize: TextUnit = 18.sp) {
    Box(
        modifier = Modifier
            .size(size)
            .background(PremiumDarkGray, CircleShape)
            .border(1.dp, HeartsPink.copy(alpha = 0.5f), CircleShape)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = avatarName.take(1).uppercase(),
            color = HeartsPink,
            fontWeight = FontWeight.SemiBold,
            fontSize = fontSize
        )
    }
}

@Composable
fun UserAvatarBadge(userName: String, size: Dp = 36.dp, fontSize: TextUnit = 16.sp) {
    AvatarBadge(avatarName = userName, size = size, fontSize = fontSize)
}

@Composable
fun DetailRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Text(label, color = PremiumLightGray, fontSize = 13.sp)
        Text(value, color = PremiumWhite, fontSize = 17.sp, fontWeight = FontWeight.Medium)
    }
}

// iOS style scale button modifier
fun Modifier.bounceClick(
    scaleDown: Float = 0.95f
) = composed {
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isPressed) scaleDown else 1f, label = "scale")
    
    this
        .scale(scale)
        .pointerInput(isPressed) {
            awaitPointerEventScope {
                isPressed = if (isPressed) {
                    waitForUpOrCancellation()
                    false
                } else {
                    awaitFirstDown(false)
                    true
                }
            }
        }
}

@Composable
fun SocialButton(name: String, color: Color, icon: ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier
            .height(50.dp)
            .fillMaxWidth()
            .bounceClick()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onClick
            ),
        shape = RoundedCornerShape(12.dp),
        color = color
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(icon, contentDescription = name, tint = PremiumWhite, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(name, color = PremiumWhite, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
        }
    }
}

@Composable
fun MiniSocialButton(label: String, url: String, context: Context) {
    Surface(
        modifier = Modifier
            .height(32.dp)
            .bounceClick()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(intent)
                }
            ),
        shape = RoundedCornerShape(8.dp),
        color = HeartsPink
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.padding(horizontal = 12.dp)) {
            Text(label, fontSize = 13.sp, color = PremiumWhite, fontWeight = FontWeight.Medium)
        }
    }
}

@Composable
fun UserProfileDialog(user: UserEntity, onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = PremiumDarkGray,
        shape = RoundedCornerShape(20.dp),
        title = { Text(user.name, color = PremiumWhite, fontWeight = FontWeight.Bold) },
        text = {
            Column {
                Text(user.title, color = HeartsPink, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(user.bio, color = PremiumLightGray, fontSize = 15.sp)
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = PremiumMediumGray, thickness = 0.5.dp)
                DetailRow("Bias", user.favoriteBias)
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = HeartsPink, fontWeight = FontWeight.SemiBold, fontSize = 17.sp)
            }
        }
    )
}

@Composable
fun IOSSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)?,
    modifier: Modifier = Modifier
) {
    Switch(
        checked = checked,
        onCheckedChange = onCheckedChange,
        modifier = modifier,
        colors = SwitchDefaults.colors(
            checkedThumbColor = PremiumWhite,
            checkedTrackColor = StatusGreen,
            uncheckedThumbColor = PremiumWhite,
            uncheckedTrackColor = PremiumMediumGray,
            uncheckedBorderColor = Color.Transparent
        )
    )
}
