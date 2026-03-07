package com.example.madproject.ui.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.madproject.ui.viewmodel.GameViewModel

@Composable
fun DashboardScreen(
    gameViewModel: GameViewModel,
    onBack: () -> Unit
) {
    val hands by gameViewModel.lastHands.collectAsState(initial = emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(onClick = onBack) {
                Text("Back")
            }

            Text(
                text = "Dashboard",
                style = MaterialTheme.typography.headlineMedium
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        DashboardFilters(
            onFilterAll = { gameViewModel.setFilterMode(null) },
            onFilterBeginner = { gameViewModel.setFilterMode("BEGINNER") },
            onFilterIntermediate = { gameViewModel.setFilterMode("INTERMEDIATE") },
            onFilterAdvanced = { gameViewModel.setFilterMode("ADVANCED") },
            onSortNewest = { gameViewModel.setSortAscending(false) },
            onSortOldest = { gameViewModel.setSortAscending(true) }
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (hands.isEmpty()) {
            Text("No hands recorded yet.")
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(hands) { hand ->
                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text("Mode: ${hand.mode}")
                            Text("Result: ${hand.result}")
                            Text("Bet: ${hand.bet}")
                            Text("Player: ${hand.playerTotal}")
                            Text("Dealer: ${hand.dealerTotal}")
                            Text("Running Count: ${hand.runningCount}")
                            Text("Balance After: ${hand.balanceAfter}")
                            Text("Decks: ${hand.decks ?: 0}")
                            Text("Cards Remaining: ${hand.cardsRemaining ?: 0}")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DashboardFilters(
    onFilterAll: () -> Unit,
    onFilterBeginner: () -> Unit,
    onFilterIntermediate: () -> Unit,
    onFilterAdvanced: () -> Unit,
    onSortNewest: () -> Unit,
    onSortOldest: () -> Unit
) {
    Column {
        Text("Filter by Mode")

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onFilterAll, modifier = Modifier.weight(1f)) {
                Text("All")
            }
            Button(onClick = onFilterBeginner, modifier = Modifier.weight(1f)) {
                Text("Beginner")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onFilterIntermediate, modifier = Modifier.weight(1f)) {
                Text("Intermediate")
            }
            Button(onClick = onFilterAdvanced, modifier = Modifier.weight(1f)) {
                Text("Advanced")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text("Sort by Time")

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(onClick = onSortNewest, modifier = Modifier.weight(1f)) {
                Text("Newest")
            }
            Button(onClick = onSortOldest, modifier = Modifier.weight(1f)) {
                Text("Oldest")
            }
        }
    }
}