// AI-assisted: UI cleanup
package com.example.madproject.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.madproject.ui.components.CasinoScreen
import com.example.madproject.ui.theme.CardSurface
import com.example.madproject.ui.theme.GoldAccent
import com.example.madproject.ui.theme.TextDark
import com.example.madproject.ui.theme.TextOnGreen
import com.example.madproject.ui.theme.TextPrimary

@Composable
fun HomeScreen(
    onDashboard: () -> Unit,
    onPlay: () -> Unit,
    onTips: () -> Unit,
    onAccount: () -> Unit,
    onRules: () -> Unit
) {
    CasinoScreen {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "Blackjack Trainer",
                style = MaterialTheme.typography.headlineLarge,
                color = TextPrimary
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Practice blackjack, test card counting, and review your sessions.",
                style = MaterialTheme.typography.bodyLarge,
                color = TextOnGreen
            )

            Spacer(Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = CardSurface.copy(alpha = 0.35f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    MenuButton("Play", onPlay)
                    Spacer(Modifier.height(10.dp))
                    MenuButton("Dashboard", onDashboard)
                    Spacer(Modifier.height(10.dp))
                    MenuButton("Tips & Tricks", onTips)
                    Spacer(Modifier.height(10.dp))
                    MenuButton("Rules", onRules)
                    Spacer(Modifier.height(10.dp))
                    MenuButton("Account", onAccount)
                }
            }
        }
    }
}

@Composable
private fun MenuButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = GoldAccent,
            contentColor = TextDark
        )
    ) {
        Text(text = text, style = MaterialTheme.typography.titleMedium)
    }
}
