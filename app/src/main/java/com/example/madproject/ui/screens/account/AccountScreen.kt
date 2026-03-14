package com.example.madproject.ui.screens.account

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.madproject.data.remote.auth.AuthRepository
import kotlinx.coroutines.launch

@Composable
fun AccountScreen(
    onBack: () -> Unit,
    onSignedOut: () -> Unit
) {
    val authRepository = remember { AuthRepository() }
    val scope = rememberCoroutineScope()

    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf(authRepository.currentUserEmail().orEmpty()) }
    var userId by remember { mutableStateOf(authRepository.currentUserId().orEmpty()) }
    var isLoading by remember { mutableStateOf(true) }
    var isSaving by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        authRepository.getProfile()
            .onSuccess { profile ->
                username = profile.username
                email = profile.email
                userId = profile.id
            }
            .onFailure {
                message = it.message ?: "Could not load account details."
            }

        isLoading = false
    }

    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text("Account", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            message = null
                        },
                        label = { Text("Username") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading && !isSaving
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = {},
                        label = { Text("Email") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )

                    Spacer(Modifier.height(12.dp))

                    OutlinedTextField(
                        value = userId,
                        onValueChange = {},
                        label = { Text("User ID") },
                        modifier = Modifier.fillMaxWidth(),
                        readOnly = true
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Button(
                onClick = {
                    val trimmed = username.trim()
                    if (trimmed.isBlank()) {
                        message = "Username cannot be empty."
                        return@Button
                    }

                    scope.launch {
                        isSaving = true
                        authRepository.updateUsername(trimmed)
                            .onSuccess {
                                username = it.username
                                message = "Username updated."
                            }
                            .onFailure {
                                message = it.message ?: "Could not update username."
                            }
                        isSaving = false
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading && !isSaving
            ) {
                Text(if (isSaving) "Saving..." else "Save Username")
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Back")
            }

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = {
                    scope.launch {
                        authRepository.signOut()
                        onSignedOut()
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Sign Out")
            }

            message?.let {
                Spacer(Modifier.height(12.dp))
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        }
    }
}