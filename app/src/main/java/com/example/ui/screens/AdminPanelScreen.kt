package com.example.ui.screens

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
