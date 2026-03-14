package com.example.madproject.ui.screens.info

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.madproject.R

private data class LinkItem(
    val title: String,
    val description: String,
    val url: String
)

@Composable
fun InfoScreen(onBack: () -> Unit) {
    val uriHandler = LocalUriHandler.current

    val blackjackLinks = listOf(
        LinkItem(
            title = "Blackjack Basic Strategy",
            description = "Core strategy and how to play hands correctly.",
            url = "https://www.blackjackapprenticeship.com/how-to-play-blackjack/"
        ),
        LinkItem(
            title = "Hi-Lo Card Counting Guide",
            description = "Running count, true count, and betting basics.",
            url = "https://www.blackjackapprenticeship.com/how-to-count-cards/"
        ),
        LinkItem(
            title = "Deck Estimation",
            description = "Useful for converting running count into true count.",
            url = "https://www.blackjackapprenticeship.com/bja-guide-to-deck-estimation/"
        )
    )

    val supportLinks = listOf(
        LinkItem(
            title = "GambleAware Ireland",
            description = "Free counselling and gambling support.",
            url = "https://www.gambleaware.ie/"
        ),
        LinkItem(
            title = "HSE Addiction Support",
            description = "Mental health and addiction support information.",
            url = "https://www2.hse.ie/mental-health/life-situations-events/causes-of-addiction/"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Tips & Resources", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(12.dp))

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    )
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            "Basic Strategy Quick View",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(8.dp))
                        Image(
                            painter = painterResource(id = R.drawable.blackjack_strategy_chart),
                            contentDescription = "Blackjack basic strategy chart",
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(1.5f),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }

            item {
                Text(
                    "Blackjack Learning",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(blackjackLinks) { link ->
                LinkCard(link = link, onClick = { uriHandler.openUri(link.url) })
            }

            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Support",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(supportLinks) { link ->
                LinkCard(link = link, onClick = { uriHandler.openUri(link.url) })
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.tertiary,
                        contentColor = MaterialTheme.colorScheme.onTertiary
                    )
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            "Reminder",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(Modifier.height(6.dp))
                        Text("This app is for training and learning, not gambling encouragement.")
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}

@Composable
private fun LinkCard(
    link: LinkItem,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = link.title,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = link.description,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = link.url,
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}