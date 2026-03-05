package com.example.madproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.example.madproject.navigation.AppNavGraph
import com.example.madproject.ui.theme.MADProjectTheme
import com.example.madproject.ui.viewmodel.GameViewModel
import com.example.madproject.ui.viewmodel.GameViewModelFactory

class MainActivity : ComponentActivity() {

    private val vm: GameViewModel by viewModels {
        val app = application as MadProjectApp
        GameViewModelFactory(app.handRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MADProjectTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    // IMPORTANT: use the Activity VM, do NOT create a new one in Compose
                    AppNavGraph(
                        navController = navController,
                        gameViewModel = vm
                    )
                }
            }
        }
    }
}