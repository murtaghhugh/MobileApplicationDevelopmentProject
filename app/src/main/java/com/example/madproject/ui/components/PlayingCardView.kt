package com.example.madproject.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
    val suitSymbol = when (card.suit) {
        Suit.CLUBS -> "♣"
        Suit.DIAMONDS -> "♦"
        Suit.HEARTS -> "♥"
        Suit.SPADES -> "♠"
    }

    val suitColor = when (card.suit) {
        Suit.HEARTS, Suit.DIAMONDS -> Color.Red
        Suit.CLUBS, Suit.SPADES -> Color.Black
    }

    Card(
        modifier = modifier
            .width(64.dp)
            .height(92.dp),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color.Black),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.Start
            ) {
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
                fontSize = 18.sp,
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
                    fontSize = 14.sp
                )
                Text(
                    text = suitSymbol,
                    color = suitColor,
                    fontSize = 14.sp
                )
            }
        }
    }
}

@Composable
fun CardHandRow(
    cards: List<Card>,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        cards.forEach { card ->
            PlayingCardView(card = card)
        }
    }
}