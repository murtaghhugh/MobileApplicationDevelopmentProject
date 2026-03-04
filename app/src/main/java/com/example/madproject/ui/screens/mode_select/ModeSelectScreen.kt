package com.example.madproject.ui.screens.mode_select

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.example.madproject.navigation.Routes

@Composable
fun ModeSelectScreen(navController: NavController) {
    Scaffold { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Select Game Mode", style = MaterialTheme.typography.headlineMedium)
            
            Button(onClick = { 
                navController.navigate(Routes.game("BEGINNER"))
            }) {
                Text(text = "Beginner")
            }
            
            Button(onClick = { 
                navController.navigate(Routes.game("INTERMEDIATE"))
            }) {
                Text(text = "Intermediate")
            }
            
            Button(onClick = { 
                navController.navigate(Routes.game("ADVANCED"))
            }) {
                Text(text = "Advanced")
            }

            Button(onClick = { navController.popBackStack() }) {
                Text(text = "Back")
            }
        }
    }
}
