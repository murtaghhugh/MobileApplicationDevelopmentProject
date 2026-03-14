package com.example.madproject.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.example.madproject.ui.viewmodel.GameViewModel
import kotlin.math.absoluteValue

@Composable
fun DashboardScreen(
    gameViewModel: GameViewModel,
    onBack: () -> Unit
) {
    val hands by gameViewModel.lastHands.collectAsState(initial = emptyList())
    val listState = rememberLazyListState()

    var selectedMode by remember { mutableStateOf("All") }
    var selectedSort by remember { mutableStateOf("Newest") }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(onClick = onBack) {
                    Text("Back")
                }

                Text(
                    text = "Dashboard",
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            DashboardFilters(
                selectedMode = selectedMode,
                selectedSort = selectedSort,
                onFilterAll = {
                    selectedMode = "All"
                    gameViewModel.setFilterMode(null)
                },
                onFilterBeginner = {
                    selectedMode = "Beginner"
                    gameViewModel.setFilterMode("BEGINNER")
                },
                onFilterIntermediate = {
                    selectedMode = "Intermediate"
                    gameViewModel.setFilterMode("INTERMEDIATE")
                },
                onFilterAdvanced = {
                    selectedMode = "Advanced"
                    gameViewModel.setFilterMode("ADVANCED")
                },
                onSortNewest = {
                    selectedSort = "Newest"
                    gameViewModel.setSortAscending(false)
                },
                onSortOldest = {
                    selectedSort = "Oldest"
                    gameViewModel.setSortAscending(true)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (hands.isEmpty()) {
                Text(
                    text = "No hands recorded yet.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(end = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(hands) { hand ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(
                                        text = "${hand.mode} • ${hand.result}",
                                        style = MaterialTheme.typography.titleMedium
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text("Bet: ${hand.bet}")
                                    Text("Player: ${hand.playerTotal}    Dealer: ${hand.dealerTotal}")
                                    Text("Running Count: ${hand.runningCount}")
                                    Text("Balance After: ${hand.balanceAfter}")
                                    Text("Decks: ${hand.decks ?: 0}    Cards Remaining: ${hand.cardsRemaining ?: 0}")
                                }
                            }
                        }
                    }

                    DashboardScrollbar(
                        listState = listState,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .fillMaxHeight()
                            .width(6.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DashboardFilters(
    selectedMode: String,
    selectedSort: String,
    onFilterAll: () -> Unit,
    onFilterBeginner: () -> Unit,
    onFilterIntermediate: () -> Unit,
    onFilterAdvanced: () -> Unit,
    onSortNewest: () -> Unit,
    onSortOldest: () -> Unit
) {
    Column {
        Text("Filter by Mode", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip("All", selectedMode == "All", onFilterAll)
            FilterChip("Beginner", selectedMode == "Beginner", onFilterBeginner)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip("Intermediate", selectedMode == "Intermediate", onFilterIntermediate)
            FilterChip("Advanced", selectedMode == "Advanced", onFilterAdvanced)
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text("Sort by Time", style = MaterialTheme.typography.titleMedium)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip("Newest", selectedSort == "Newest", onSortNewest)
            FilterChip("Oldest", selectedSort == "Oldest", onSortOldest)
        }
    }
}

@Composable
private fun RowScope.FilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    AssistChip(
        onClick = onClick,
        label = { Text(label) },
        modifier = Modifier.weight(1f),
        colors = if (selected) {
            AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.primary,
                labelColor = MaterialTheme.colorScheme.onPrimary
            )
        } else {
            AssistChipDefaults.assistChipColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                labelColor = MaterialTheme.colorScheme.onSurface
            )
        }
    )
}

@Composable
private fun DashboardScrollbar(
    listState: LazyListState,
    modifier: Modifier = Modifier
) {
    val showScrollbar by remember {
        derivedStateOf {
            listState.layoutInfo.totalItemsCount > listState.layoutInfo.visibleItemsInfo.size
        }
    }

    if (!showScrollbar) return

    val thumbFraction by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val totalItems = layoutInfo.totalItemsCount
            val visibleItems = layoutInfo.visibleItemsInfo.size.coerceAtLeast(1)
            (visibleItems.toFloat() / totalItems.toFloat()).coerceIn(0.08f, 1f)
        }
    }

    val startFraction by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) {
                0f
            } else {
                val firstItem = visibleItems.first()
                val estimatedOffset = firstItem.index + (firstItem.offset.absoluteValue / firstItem.size.toFloat())
                (estimatedOffset / layoutInfo.totalItemsCount.toFloat()).coerceIn(0f, 1f)
            }
        }
    }

    val thumbColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f))
    ) {
        androidx.compose.foundation.Canvas(
            modifier = Modifier.fillMaxSize()
        ) {
            val thumbHeight = size.height * thumbFraction
            val maxOffset = size.height - thumbHeight
            val top = (size.height * startFraction).coerceIn(0f, maxOffset)

            drawRoundRect(
                color = thumbColor,
                topLeft = Offset(0f, top),
                size = Size(size.width, thumbHeight),
                cornerRadius = CornerRadius(size.width, size.width)
            )
        }
    }
}
