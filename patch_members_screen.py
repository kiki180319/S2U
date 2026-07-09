with open('app/src/main/java/com/example/ui/screens/MembersScreen.kt', 'w') as f:
    f.write("""package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.ui.theme.*

data class MemberProfile(
    val stageName: String,
    val fullName: String,
    val birthday: String,
    val nationality: String,
    val position: String,
    val imageUrl: String
)

val membersList = listOf(
    MemberProfile(
        stageName = "Carmen",
        fullName = "Nyoman Ayu Carmenita",
        birthday = "2006-03-28",
        nationality = "Indonesia",
        position = "Member",
        imageUrl = "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/carmen.jpeg"
    ),
    MemberProfile(
        stageName = "Jiwoo",
        fullName = "Choi Ji-woo",
        birthday = "2006-09-07",
        nationality = "South Korea",
        position = "Leader",
        imageUrl = "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/jiwoo.jpg"
    ),
    MemberProfile(
        stageName = "Yuha",
        fullName = "Yu Ha-ram",
        birthday = "2007-04-12",
        nationality = "South Korea",
        position = "Member",
        imageUrl = "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/yuha.webp"
    ),
    MemberProfile(
        stageName = "Stella",
        fullName = "Kim Da-hyun",
        birthday = "2007-06-18",
        nationality = "South Korea / Canada",
        position = "Member",
        imageUrl = "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/stella.jpg"
    ),
    MemberProfile(
        stageName = "Juun",
        fullName = "Kim Ju-eun",
        birthday = "2008-12-03",
        nationality = "South Korea",
        position = "Member",
        imageUrl = "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/juun.png"
    ),
    MemberProfile(
        stageName = "A-na",
        fullName = "Roh Yu-na",
        birthday = "2008-12-20",
        nationality = "South Korea",
        position = "Member",
        imageUrl = "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/ana.jpeg"
    ),
    MemberProfile(
        stageName = "Ian",
        fullName = "Jeong Lee-an",
        birthday = "2009-10-09",
        nationality = "South Korea",
        position = "Member",
        imageUrl = "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/ian.jpeg"
    ),
    MemberProfile(
        stageName = "Ye-on",
        fullName = "Kim Na-yeon",
        birthday = "2010-04-19",
        nationality = "South Korea",
        position = "Member",
        imageUrl = "https://raw.githubusercontent.com/kiki180319/S2U/main/assets/.aistudio/yeon.jpg"
    )
)

@Composable
fun MembersScreen(onBack: () -> Unit) {
    var selectedMember by remember { mutableStateOf<MemberProfile?>(null) }

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

@Composable
fun MemberCard(member: MemberProfile, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = PremiumDarkGray),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(
                    model = member.imageUrl,
                    contentDescription = member.stageName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .background(PremiumMediumGray)
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
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = PremiumWhite) }
            Text(member.stageName, color = PremiumWhite, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(24.dp))
        
        Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
            AsyncImage(
                model = member.imageUrl,
                contentDescription = member.stageName,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape)
                    .background(PremiumMediumGray)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = PremiumDarkGray),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                DetailRow("Stage Name", member.stageName)
                HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                DetailRow("Full Name", member.fullName)
                HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                DetailRow("Position", member.position)
                HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                DetailRow("Birthday", member.birthday)
                HorizontalDivider(color = PremiumMediumGray, modifier = Modifier.padding(vertical = 12.dp))
                DetailRow("Nationality", member.nationality)
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
""")
