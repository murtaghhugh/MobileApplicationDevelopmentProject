package com.example.madproject.ui.screens.game

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun GameModeScreen(
    onSelectMode: (String) -> Unit,
    onBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Select Game Mode", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = { onSelectMode("BEGINNER") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Beginner")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onSelectMode("INTERMEDIATE") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Intermediate")
        }
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onSelectMode("ADVANCED") },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Advanced")
        }

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Back")
        }
    }
}
