import os

screens = {
    "FeedScreen.kt": """package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.HeartsViewModel
import com.example.ui.theme.*

@Composable
fun FeedScreen(viewModel: HeartsViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Feed", color = PremiumWhite)
    }
}
""",
    "EventsScreen.kt": """package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.HeartsViewModel
import com.example.ui.theme.*

@Composable
fun EventsScreen(viewModel: HeartsViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Events", color = PremiumWhite)
    }
}
""",
    "ForumScreen.kt": """package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.HeartsViewModel
import com.example.ui.theme.*

@Composable
fun ForumScreen(viewModel: HeartsViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Forum", color = PremiumWhite)
    }
}
""",
    "ProfileScreen.kt": """package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.HeartsViewModel
import com.example.ui.theme.*

@Composable
fun ProfileScreen(viewModel: HeartsViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Profile", color = PremiumWhite)
    }
}
""",
    "AiChatScreen.kt": """package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.HeartsViewModel
import com.example.ui.theme.*

@Composable
fun AiChatScreen(viewModel: HeartsViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("AI Chat", color = PremiumWhite)
    }
}
""",
    "LeaderboardScreen.kt": """package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.HeartsViewModel
import com.example.ui.theme.*

@Composable
fun LeaderboardScreen(viewModel: HeartsViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Leaderboard", color = PremiumWhite)
    }
}
""",
    "Top50ChatScreen.kt": """package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.HeartsViewModel
import com.example.ui.theme.*

@Composable
fun Top50ChatScreen(viewModel: HeartsViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Top 50 Chat", color = PremiumWhite)
    }
}
""",
    "StreamingPartyScreen.kt": """package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.HeartsViewModel
import com.example.ui.theme.*

@Composable
fun StreamingPartyScreen(viewModel: HeartsViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Streaming Party", color = PremiumWhite)
    }
}
""",
    "VideoDashboardScreen.kt": """package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ui.viewmodel.HeartsViewModel
import com.example.ui.theme.*

@Composable
fun VideoDashboardScreen(viewModel: HeartsViewModel) {
    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Video Dashboard", color = PremiumWhite)
    }
}
""",
    "AdminPanelScreen.kt": """package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.viewmodel.HeartsViewModel
import com.example.ui.theme.*

@Composable
fun AdminPanelScreen(viewModel: HeartsViewModel) {
    val isAdmin by viewModel.isAdmin.collectAsStateWithLifecycle()
    if (!isAdmin) {
        Column(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Lock, contentDescription = "Locked", tint = PremiumMediumGray, modifier = Modifier.size(64.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Text("Access Denied", color = PremiumWhite, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Text("You do not have permission to view the Admin Panel.", color = PremiumLightGray, textAlign = TextAlign.Center)
        }
        return
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Admin Panel", color = PremiumWhite)
    }
}
"""
}

os.makedirs("app/src/main/java/com/example/ui/screens", exist_ok=True)
for filename, content in screens.items():
    with open(f"app/src/main/java/com/example/ui/screens/{filename}", "w") as f:
        f.write(content)

