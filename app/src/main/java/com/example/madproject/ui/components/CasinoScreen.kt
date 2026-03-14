package com.example.madproject.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.madproject.ui.theme.TableGreen
import com.example.madproject.ui.theme.TableGreenDark

@Composable
fun CasinoScreen(
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        TableGreen,
                        TableGreenDark
                    )
                )
            )
            .padding(20.dp)
    ) {
        content()
    }
}