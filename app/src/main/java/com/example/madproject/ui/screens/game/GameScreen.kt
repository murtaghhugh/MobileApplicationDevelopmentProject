package com.example.madproject.ui.screens.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.madproject.core.game.handTotal
import com.example.madproject.ui.components.CardHandRow
import com.example.madproject.ui.viewmodel.GameViewModel
import com.example.madproject.ui.viewmodel.HandPhase

@Composable
fun GameScreen(
    mode: String,
    vm: GameViewModel,
    onBack: () -> Unit
) {
    val s by vm.state.collectAsState()
    val scrollState = rememberScrollState()

    val countSystem =
        when (s.selectedMode.lowercase()) {
            "beginner" -> "Hi-Lo"
            "intermediate" -> "Hi-Lo"
            "advanced" -> "Omega II"
            else -> "Hi-Lo"
        }

    val trueCountDisplay =
        if (s.trueCount >= 0) "+%.1f".format(s.trueCount)
        else "%.1f".format(s.trueCount)

    val showDealerFullHand =
        s.phase == HandPhase.DEALER_TURN || s.phase == HandPhase.FINISHED

    val dealerCardsToShow =
        if (s.dealerCards.isEmpty()) emptyList()
        else if (showDealerFullHand) s.dealerCards
        else listOf(s.dealerCards.first())

    val dealerVisibleTotal =
        if (dealerCardsToShow.isEmpty()) 0 else handTotal(dealerCardsToShow)

    val activeHand =
        s.playerHands.getOrElse(s.activeHandIndex) { emptyList() }

    val activePlayerTotal =
        if (activeHand.isEmpty()) 0 else handTotal(activeHand)

    val bettingEnabled = s.phase == HandPhase.READY || s.phase == HandPhase.FINISHED

    val showInsuranceButtons =
        s.phase == HandPhase.INSURANCE_DECISION && s.insuranceOffered && !s.insuranceTaken

    val activeHandBet =
        s.handBets.getOrElse(s.activeHandIndex) { s.baseBet }

    val primaryButtonColors = ButtonDefaults.buttonColors(
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text("Game", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(8.dp))

        Text("Mode: ${s.selectedMode}")
        Text("Count System: $countSystem")
        Text("Balance: ${s.balance}    Bet: ${s.bet}")
        Text("Running Count: ${if (s.runningCount >= 0) "+${s.runningCount}" else s.runningCount}")
        if (s.decks > 1) {
            Text("True Count: $trueCountDisplay")
        }
        if (s.shoeLabel.isNotBlank()) {
            Text(s.shoeLabel)
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(12.dp))

        Text("Dealer")
        if (dealerCardsToShow.isNotEmpty()) {
            CardHandRow(cards = dealerCardsToShow)
            Spacer(Modifier.height(4.dp))
            Text("Dealer Total: $dealerVisibleTotal")
        } else {
            Text("—")
        }

        Spacer(Modifier.height(16.dp))
        HorizontalDivider()
        Spacer(Modifier.height(12.dp))

        if (s.playerHands.isEmpty()) {
            Text("You")
            Text("—")
        } else if (s.playerHands.size == 1) {
            Text("You")
            CardHandRow(cards = activeHand)
            Spacer(Modifier.height(4.dp))
            Text("Your Total: $activePlayerTotal")
        } else {
            Text("Your Hands")
            Spacer(Modifier.height(8.dp))

            s.playerHands.forEachIndexed { index, hand ->
                val isActive = index == s.activeHandIndex
                val handBet = s.handBets.getOrElse(index) { s.baseBet }
                val handTotalValue = handTotal(hand)

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    border = if (isActive) {
                        BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                    } else {
                        BorderStroke(1.dp, Color.LightGray)
                    },
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        Text(
                            text = if (isActive) "Hand ${index + 1} (Active)" else "Hand ${index + 1}",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text("Bet: $handBet")
                        Spacer(Modifier.height(6.dp))
                        CardHandRow(cards = hand)
                        Spacer(Modifier.height(6.dp))
                        Text("Total: $handTotalValue")
                    }
                }

                Spacer(Modifier.height(10.dp))
            }
        }

        Spacer(Modifier.height(8.dp))
        Text(s.message)

        if (showInsuranceButtons) {
            Spacer(Modifier.height(16.dp))

            Text(
                text = "Dealer shows an Ace. Take insurance?",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = vm::takeInsurance,
                    modifier = Modifier.weight(1f),
                    colors = primaryButtonColors
                ) {
                    Text("Take Insurance")
                }

                OutlinedButton(
                    onClick = vm::skipInsurance,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Skip Insurance")
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = vm::betMinus5,
                enabled = bettingEnabled,
                modifier = Modifier.weight(1f)
            ) {
                Text("-5")
            }

            OutlinedButton(
                onClick = vm::betPlus5,
                enabled = bettingEnabled,
                modifier = Modifier.weight(1f)
            ) {
                Text("+5")
            }

            OutlinedButton(
                onClick = vm::betTimesTwo,
                enabled = bettingEnabled,
                modifier = Modifier.weight(1f)
            ) {
                Text("x2")
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = vm::betAllIn,
                enabled = bettingEnabled,
                modifier = Modifier.weight(1f)
            ) {
                Text("All In")
            }

            OutlinedButton(
                onClick = vm::undoLastBetMove,
                enabled = bettingEnabled && s.previousBaseBet != null,
                modifier = Modifier.weight(1f)
            ) {
                Text("Undo")
            }
        }

        Spacer(Modifier.height(12.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = vm::deal,
                enabled = s.phase == HandPhase.READY || s.phase == HandPhase.FINISHED,
                colors = primaryButtonColors,
                modifier = Modifier.weight(1f)
            ) {
                Text("Deal")
            }

            Button(
                onClick = vm::hit,
                enabled = s.phase == HandPhase.PLAYER_TURN && activePlayerTotal < 21,
                colors = primaryButtonColors,
                modifier = Modifier.weight(1f)
            ) {
                Text("Hit")
            }

            Button(
                onClick = vm::stand,
                enabled = s.phase == HandPhase.PLAYER_TURN,
                colors = primaryButtonColors,
                modifier = Modifier.weight(1f)
            ) {
                Text("Stand")
            }
        }

        Spacer(Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = vm::doubleDown,
                enabled = s.phase == HandPhase.PLAYER_TURN &&
                        s.canDoubleDown &&
                        s.balance >= activeHandBet,
                colors = primaryButtonColors,
                modifier = Modifier.weight(1f)
            ) {
                Text("Double")
            }

            Button(
                onClick = vm::split,
                enabled = s.phase == HandPhase.PLAYER_TURN && s.canSplit,
                colors = primaryButtonColors,
                modifier = Modifier.weight(1f)
            ) {
                Text("Split")
            }
        }

        Spacer(Modifier.height(24.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }

        Spacer(Modifier.height(12.dp))
    }
}