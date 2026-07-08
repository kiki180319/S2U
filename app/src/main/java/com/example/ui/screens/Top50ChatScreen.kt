package com.example.ui.screens

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
