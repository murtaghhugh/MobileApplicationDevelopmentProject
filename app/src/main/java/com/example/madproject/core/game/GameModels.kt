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
    var cutIndex: Int,
    val discards: MutableList<Card> = mutableListOf(),
    var dealtCount: Int = 0
) {
    fun draw(): Card {
        if (cards.isEmpty()) error("Shoe is empty")
        dealtCount += 1
        return cards.removeAt(cards.lastIndex)
    }

    fun discard(played: List<Card>) {
        discards.addAll(played)
    }

    fun pastCut(): Boolean = dealtCount >= cutIndex
    fun remainingCount(): Int = cards.size

    fun reshuffle(cutMin: Double, cutMax: Double) {
        // Bring discards back into the shoe
        cards.addAll(discards)
        discards.clear()

        // Shuffle everything
        cards.shuffle()

        // Re-roll cut card position based on % of total
        val total = cards.size
        val cutPercent = Random.nextDouble(cutMin, cutMax)
        cutIndex = (total * cutPercent).toInt().coerceIn(1, total)

        // Reset dealing progress
        dealtCount = 0
    }
}

fun newShuffledShoe(decks: Int, cutMin: Double, cutMax: Double): Shoe {
    val all = mutableListOf<Card>()
    repeat(decks) {
        for (s in Suit.entries) for (r in Rank.entries) {
            all.add(Card(s, r))
        }
    }
    all.shuffle()

    val total = all.size
    val cutPercent = Random.nextDouble(cutMin, cutMax)
    val cutIndex = (total * cutPercent).toInt().coerceIn(1, total)

    return Shoe(decks = decks, cards = all, cutIndex = cutIndex)
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

fun Card.toCode(): String = "${rank.name}:${suit.name}"

fun cardFromCode(code: String): Card {
    val (r, s) = code.split(":")
    return Card(Suit.valueOf(s), Rank.valueOf(r))
}

fun cardsToCsv(cards: List<Card>): String = cards.joinToString(",") { it.toCode() }
fun cardsFromCsv(csv: String): MutableList<Card> =
    if (csv.isBlank()) mutableListOf() else csv.split(",").map { cardFromCode(it) }.toMutableList()