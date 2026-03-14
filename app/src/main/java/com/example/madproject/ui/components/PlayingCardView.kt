package com.example.madproject.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.madproject.core.game.Card
import com.example.madproject.core.game.Suit

@Composable
fun PlayingCardView(
    card: Card,
    modifier: Modifier = Modifier
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(card) { visible = true }

    val scale by animateFloatAsState(
        targetValue = if (visible) 1f else 0.85f,
        label = "cardScale"
    )

    val suitSymbol = when (card.suit) {
        Suit.CLUBS -> "♣"
        Suit.DIAMONDS -> "♦"
        Suit.HEARTS -> "♥"
        Suit.SPADES -> "♠"
    }

    val suitColor = when (card.suit) {
        Suit.HEARTS, Suit.DIAMONDS -> Color(0xFFC62828)
        Suit.CLUBS, Suit.SPADES -> Color(0xFF111111)
    }

    AnimatedVisibility(
        visible = visible,
        enter = fadeIn() + scaleIn(initialScale = 0.85f)
    ) {
        Card(
            modifier = modifier
                .width(72.dp)
                .height(104.dp)
                .scale(scale),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, Color(0xFF222222)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(Color.White, Color(0xFFF7F3EA))
                        )
                    )
                    .padding(8.dp)
            ) {
                Column(horizontalAlignment = Alignment.Start) {
                    Text(
                        text = card.rank.label,
                        color = suitColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(
                        text = suitSymbol,
                        color = suitColor,
                        fontSize = 18.sp
                    )
                }

                Text(
                    text = suitSymbol,
                    color = suitColor,
                    fontSize = 28.sp,
                    modifier = Modifier.align(Alignment.Center)
                )

                Column(
                    modifier = Modifier.align(Alignment.BottomEnd),
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = card.rank.label,
                        color = suitColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = suitSymbol,
                        color = suitColor,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun CardBackView(
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(72.dp)
            .height(104.dp),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(1.dp, Color.Black),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(
                        listOf(
                            Color(0xFF0D47A1),
                            Color(0xFF1976D2),
                            Color(0xFF0D47A1)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "♠ ♥\n♣ ♦",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CardHandRow(
    cards: List<Card>,
    modifier: Modifier = Modifier,
    showCardBackAtEnd: Boolean = false
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        cards.forEach { card ->
            PlayingCardView(card = card)
        }
        if (showCardBackAtEnd) {
            CardBackView()
        }
    }
}