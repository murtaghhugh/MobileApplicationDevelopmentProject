package com.example.madproject.ui.screens.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen(
    onDashboard: () -> Unit,
    onPlay: () -> Unit,
    onTips: () -> Unit,
    onAccount: () -> Unit
) {
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Home", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        Button(onClick = onDashboard, modifier = Modifier.fillMaxWidth()) { Text("Dashboard") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = onPlay, modifier = Modifier.fillMaxWidth()) { Text("Play") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = onTips, modifier = Modifier.fillMaxWidth()) { Text("Tips & Tricks") }
        Spacer(Modifier.height(8.dp))
        Button(onClick = onAccount, modifier = Modifier.fillMaxWidth()) { Text("Account") }
    }
}