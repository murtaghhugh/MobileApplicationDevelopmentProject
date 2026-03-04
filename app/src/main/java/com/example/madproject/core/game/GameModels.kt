package com.example.madproject.core.game

import kotlin.random.Random

enum class Suit { CLUBS, DIAMONDS, HEARTS, SPADES }
enum class Rank(val label: String, val pipValue: Int) {
    A("A", 11), //Aces being 1 or 11 handled in handTotal
    TWO("2", 2), THREE("3", 3), FOUR("4", 4), FIVE("5", 5), SIX("6", 6),
    SEVEN("7", 7), EIGHT("8", 8), NINE("9", 9),
    TEN("10", 10), J("J", 10), Q("Q", 10), K("K", 10),
}

data class Card(val suit: Suit, val rank: Rank) {
    val hiLoValue: Int = when (rank) {
        Rank.TWO, Rank.THREE, Rank.FOUR, Rank.FIVE, Rank.SIX -> +1
        Rank.SEVEN, Rank.EIGHT, Rank.NINE -> 0
        else -> -1 // TEN/J/Q/K/A
    }

    fun display(): String {
        val s = when (suit) {
            Suit.CLUBS -> "♣"
            Suit.DIAMONDS -> "♦"
            Suit.HEARTS -> "♥"
            Suit.SPADES -> "♠"
        }
        return "${rank.label}$s"
    }
}

data class Shoe(
    val decks: Int,
    val cards: MutableList<Card>,
    val cutIndex: Int,
    var dealtCount: Int = 0
) {
    fun draw(): Card {
        if (cards.isEmpty()) error("Shoe is empty")
        dealtCount += 1
        return cards.removeAt(cards.lastIndex)
    }
    fun pastCut(): Boolean = dealtCount >= cutIndex
}

fun newShuffledShoe(decks: Int, cutMin: Double, cutMax: Double): Shoe {
    val singleDeck = buildList {
        for (s in Suit.entries) for (r in Rank.entries) add(Card(s, r))
    }
    val all = MutableList(52 * decks) { i -> singleDeck[i % 52] }.toMutableList()
    all.shuffle()

    val total = all.size
    val cutPercent = Random.nextDouble(cutMin, cutMax)
    val cutIndex = (total * cutPercent).toInt().coerceIn(1, total)

    return Shoe(decks, all, cutIndex)
}

fun handTotal(cards: List<Card>): Int {
    var total = cards.sumOf { it.rank.pipValue }
    var aces = cards.count { it.rank == Rank.A }
    while (total > 21 && aces > 0) {
        total -= 10
        aces--
    }
    return total
}