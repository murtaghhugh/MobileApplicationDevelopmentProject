    package com.example.madproject.ui.screens.game

    import androidx.compose.foundation.layout.*
    import androidx.compose.material3.*
    import androidx.compose.runtime.Composable
    import androidx.compose.ui.Modifier
    import androidx.compose.ui.unit.dp
    import androidx.compose.runtime.collectAsState
    import androidx.compose.runtime.getValue
    import com.example.madproject.ui.viewmodel.GameViewModel
    import com.example.madproject.ui.viewmodel.HandPhase
    import com.example.madproject.core.game.handTotal

    @Composable
    fun GameScreen(
        mode: String,
        vm: GameViewModel,
        onBack: () -> Unit
    ) {
        val s by vm.state.collectAsState()

        val dealerText =
            if (s.dealerCards.isEmpty()) "—"
            else if (s.phase == HandPhase.PLAYER_TURN) s.dealerCards.first().display()
            else s.dealerCards.joinToString(" ") { it.display() }

        val playerText =
            if (s.playerCards.isEmpty()) "—"
            else s.playerCards.joinToString(" ") { it.display() }

        val playerTotal = handTotal(s.playerCards)

        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("Game", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text("Mode: ${s.selectedMode}")
            Text("Balance: ${s.balance}    Bet: ${s.bet}")
            Text("Hi-Lo Count: ${s.runningCount}")
            if (s.shoeLabel.isNotBlank()) Text(s.shoeLabel)

            Spacer(Modifier.height(16.dp))
            HorizontalDivider()
            Spacer(Modifier.height(12.dp))

            Text("Dealer: $dealerText")
            Text("You:    $playerText")

            Spacer(Modifier.height(12.dp))
            Text(s.message)

            Spacer(Modifier.height(16.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = vm::betMinus5) { Text("-5") }
                OutlinedButton(onClick = vm::betPlus5) { Text("+5") }
            }

            Spacer(Modifier.height(12.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = vm::deal,
                    enabled = s.phase != HandPhase.PLAYER_TURN
                ) { Text("Deal") }

                Button(
                    onClick = vm::hit,
                    enabled = s.phase == HandPhase.PLAYER_TURN && playerTotal < 21
                ) { Text("Hit") }

                Button(
                    onClick = vm::stand,
                    enabled = s.phase == HandPhase.PLAYER_TURN
                ) { Text("Stand") }
            }

            Spacer(Modifier.height(24.dp))

            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth()) { Text("Back") }
        }
    }
