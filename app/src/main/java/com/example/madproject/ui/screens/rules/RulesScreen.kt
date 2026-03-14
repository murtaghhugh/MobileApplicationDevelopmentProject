// AI-assisted: UI improvements
package com.example.madproject.ui.screens.rules

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.madproject.ui.components.CasinoScreen
import com.example.madproject.ui.theme.BorderLight
import com.example.madproject.ui.theme.CardSurface
import com.example.madproject.ui.theme.TextPrimary
import com.example.madproject.ui.theme.TextOnGreen

@Composable
fun RulesScreen(onBack: () -> Unit) {
    CasinoScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                "Game Rules",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = CardSurface.copy(alpha = 0.35f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    RuleLine("Blackjack pays 3:2")
                    RuleLine("Dealer reveals one up-card initially")
                    RuleLine("Dealer hits until at least 17")
                    RuleLine("Split is allowed on matching ranks")
                    RuleLine("Double down gives one card only")
                    RuleLine("If bankroll hits 0, it resets to 100")
                    RuleLine("Beginner = 1 deck Hi-Lo")
                    RuleLine("Intermediate = 6 deck Hi-Lo")
                    RuleLine("Advanced = 8 deck Omega II")
                }
            }

            Spacer(Modifier.height(20.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.5.dp, BorderLight),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextPrimary
                )
            ) {
                Text("Back")
            }
        }
    }
}

@Composable
private fun RuleLine(text: String) {
    Text(
        text = "• $text",
        style = MaterialTheme.typography.bodyLarge,
        color = TextOnGreen,
        modifier = Modifier.padding(vertical = 4.dp)
    )
}