package com.example.madproject.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.madproject.ui.viewmodel.GameViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun DashboardScreen(
    vm: GameViewModel,
    onBack: () -> Unit
) {
    val lastHands by vm.lastHands.collectAsState(initial = emptyList())

    fun formatTime(epochMs: Long): String =
        SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault()).format(Date(epochMs))

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Dashboard (Last 20 hands)", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(lastHands) { hand ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text(hand.mode, style = MaterialTheme.typography.titleSmall)
                        Text(formatTime(hand.playedAtEpochMs))
                        Text("Result: ${hand.result} | RC: ${hand.runningCount}")
                        Text("P: ${hand.playerTotal} | D: ${hand.dealerTotal}")
                        Text("Bet: ${hand.bet}")
                    }
                }
            }
        }

        OutlinedButton(
            onClick = { vm.clearHistory() },
            modifier = Modifier.fillMaxWidth()
        ) { Text("Clear History") }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Back") }
    }
}