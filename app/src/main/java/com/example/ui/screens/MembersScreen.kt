package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.*
import org.json.JSONArray
import java.io.InputStreamReader

data class MemberProfile(
    val stageName: String,
    val fullName: String,
    val nativeName: String,
    val birthday: String,
    val position: String,
    val heightCm: Double,
    val bloodType: String,
    val mbti: String,
    val birthplace: String,
    val nationality: String,
    val imageUrl: String
)

fun loadMembers(context: android.content.Context): List<MemberProfile> {
    return try {
        val inputStream = context.assets.open("data/members.json")
        val jsonString = InputStreamReader(inputStream).readText()
        val jsonArray = JSONArray(jsonString)
        val members = mutableListOf<MemberProfile>()
        for (i in 0 until jsonArray.length()) {
            val obj = jsonArray.getJSONObject(i)
            members.add(
                MemberProfile(
                    stageName = obj.optString("stageName", ""),
                    fullName = obj.optString("fullName", ""),
                    nativeName = obj.optString("nativeName", ""),
                    birthday = obj.optString("birthday", ""),
                    position = obj.optString("position", ""),
                    heightCm = obj.optDouble("heightCm", 0.0),
                    bloodType = obj.optString("bloodType", ""),
                    mbti = obj.optString("mbti", ""),
                    birthplace = obj.optString("birthplace", ""),
                    nationality = obj.optString("nationality", ""),
                    imageUrl = obj.optString("imageUrl", "")
                )
            )
        }
        members
    } catch (e: Exception) {
        e.printStackTrace()
        emptyList()
    }
}

@Composable
fun MembersScreen(onBack: () -> Unit) {
    val context = LocalContext.current
    val membersList = remember { loadMembers(context) }
    var selectedMember by remember { mutableStateOf<MemberProfile?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(androidx.compose.ui.graphics.Color.Transparent)) {

        AnimatedContent(targetState = selectedMember, label = "MemberScreenTransition") { member ->
            if (member != null) {
                MemberDetailScreen(member = member, onBack = { selectedMember = null })
            } else {
                Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PremiumWhite) }
                        Text("Members Profile", color = PremiumWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        items(membersList) { m ->
                            MemberCard(member = m, onClick = { selectedMember = m })
                        }
                    }
                }
            }
        }
    }
}

fun getMemberImageId(stageName: String): Int {
    return when (stageName.lowercase()) {
        "jiwoo" -> com.example.R.drawable.img_jiwoo
        "carmen" -> com.example.R.drawable.img_carmen
        "yuha" -> com.example.R.drawable.img_yuha
        "stella" -> com.example.R.drawable.img_stella
        "juun" -> com.example.R.drawable.img_juun
        "a-na" -> com.example.R.drawable.img_ana
        "ian" -> com.example.R.drawable.img_ian
        "ye-on" -> com.example.R.drawable.img_yeon
        else -> com.example.R.drawable.img_logo
    }
}

@Composable
fun MemberCard(member: MemberProfile, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.4f)),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = getMemberImageId(member.stageName),
                    contentDescription = member.stageName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(member.stageName, color = PremiumWhite, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    Text(member.fullName, color = PremiumLightGray, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(member.position, color = HeartsPink, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@Composable
fun MemberDetailScreen(member: MemberProfile, onBack: () -> Unit) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState())) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PremiumWhite) }
            Text(member.stageName, color = PremiumWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))
        
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = getMemberImageId(member.stageName),
                contentDescription = member.stageName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.4f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                DetailRow("Stage Name", member.stageName)
                HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                DetailRow("Full Name", "${member.fullName} (${member.nativeName})")
                HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                DetailRow("Position", member.position)
                HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                DetailRow("Birthday", member.birthday)
                HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                DetailRow("Birthplace", member.birthplace)
                HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                DetailRow("Nationality", member.nationality)
                HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                DetailRow("Height", "${member.heightCm} cm")
                HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                DetailRow("Blood Type", member.bloodType)
                HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                DetailRow("MBTI", member.mbti)
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(label, color = PremiumLightGray, fontSize = 16.sp)
        Text(value, color = PremiumWhite, fontWeight = FontWeight.Bold, fontSize = 16.sp)
    }
}
