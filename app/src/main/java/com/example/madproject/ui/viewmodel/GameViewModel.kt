package com.example.madproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import com.example.madproject.core.game.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class HandPhase { READY, PLAYER_TURN, FINISHED }

data class GameUiState(
    val selectedMode: String = "BEGINNER",
    val decks: Int = 1,
    val cutMin: Double = 0.60,
    val cutMax: Double = 0.80,
    val balance: Int = 100,
    val bet: Int = 5,
    val runningCount: Int = 0,
    val shoeLabel: String = "",
    val dealerCards: List<Card> = emptyList(),
    val playerCards: List<Card> = emptyList(),
    val message: String = "Select a mode and press Deal",
    val phase: HandPhase = HandPhase.READY,
    val dashboard: List<DashboardItem> = emptyList()
)

class GameViewModel : ViewModel() {

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state

    private var shoe: Shoe? = null
    private var nextId = 1L

    private fun nowLabel(): String =
        SimpleDateFormat("HH:mm:ss dd/MM/yyyy", Locale.getDefault()).format(Date())

    fun selectMode(mode: String) {
        val (decks, cutMin, cutMax) = when (mode) {
            "BEGINNER" -> Triple(1, 0.60, 0.80)
            "INTERMEDIATE" -> Triple(6, 0.70, 0.80)
            "ADVANCED" -> Triple(8, 0.70, 0.80) // still hi-lo today, omega later
            else -> Triple(1, 0.60, 0.80)
        }

        shoe = newShuffledShoe(decks, cutMin, cutMax)

        _state.value = _state.value.copy(
            selectedMode = mode,
            decks = decks,
            cutMin = cutMin,
            cutMax = cutMax,
            runningCount = 0,
            dealerCards = emptyList(),
            playerCards = emptyList(),
            phase = HandPhase.READY,
            message = "Mode set: $mode. Press Deal.",
            shoeLabel = shoeProgress()
        )
    }

    fun betMinus5() {
        val s = _state.value
        _state.value = s.copy(bet = (s.bet - 5).coerceAtLeast(5))
    }

    fun betPlus5() {
        val s = _state.value
        _state.value = s.copy(bet = (s.bet + 5).coerceAtMost(s.balance.coerceAtLeast(5)))
    }

    fun deal() {
        val s = _state.value
        if (shoe == null) shoe = newShuffledShoe(s.decks, s.cutMin, s.cutMax)
        if (shoe!!.pastCut()) {
            shoe = newShuffledShoe(s.decks, s.cutMin, s.cutMax)
            _state.value = _state.value.copy(runningCount = 0)
        }

        val sh = shoe!!

        // Player, Dealer, Player (dealer has 1 visible card)
        val p1 = sh.draw().also { adjustCount(it) }
        val d1 = sh.draw().also { adjustCount(it) }
        val p2 = sh.draw().also { adjustCount(it) }

        val player = listOf(p1, p2)
        val dealer = listOf(d1)

        _state.value = _state.value.copy(
            playerCards = player,
            dealerCards = dealer,
            phase = HandPhase.PLAYER_TURN,
            message = "Your turn: Hit or Stand",
            shoeLabel = shoeProgress()
        )

        val playerTotal = handTotal(player)
        val isBlackjack = playerTotal == 21 // since player is always 2 cards here

        if (isBlackjack) {
            finishHand(
                result = "BLACKJACK",
                message = "Blackjack! Pays 3:2",
                finalDealer = dealer,
                finalPlayer = player
            )
        }
    }

    fun hit() {
        val s = _state.value
        if (s.phase != HandPhase.PLAYER_TURN) return
        val sh = shoe ?: return

        val currentTotal = handTotal(s.playerCards)
        if (currentTotal >= 21) return

        val card = sh.draw().also { adjustCount(it) }
        val newPlayer = s.playerCards + card
        val total = handTotal(newPlayer)

        if (total > 21) {
            finishHand(
                result = "LOSE",
                message = "Bust ($total)! You lose.",
                finalDealer = s.dealerCards, // dealer stays hidden-ish for now
                finalPlayer = newPlayer
            )
        } else {
            val msg = if (total == 21) "21! You must Stand." else "Hit or Stand (Total: $total)"
            _state.value = s.copy(
                playerCards = newPlayer,
                message = msg,
                shoeLabel = shoeProgress()
            )
        }
    }

    fun stand() {
        val s = _state.value
        if (s.phase != HandPhase.PLAYER_TURN) return
        val sh = shoe ?: return

        // Dealer draws hole card now
        var dealer = s.dealerCards + sh.draw().also { adjustCount(it) }

        while (handTotal(dealer) < 17) {
            dealer = dealer + sh.draw().also { adjustCount(it) }
        }

        val pt = handTotal(s.playerCards)
        val dt = handTotal(dealer)

        val result = when {
            dt > 21 -> "WIN"
            pt > dt -> "WIN"
            pt < dt -> "LOSE"
            else -> "PUSH"
        }

        finishHand(
            result = result,
            message = "You: $pt | Dealer: $dt → $result",
            finalDealer = dealer,
            finalPlayer = s.playerCards
        )
    }

    private fun finishHand(result: String, message: String, finalDealer: List<Card>, finalPlayer: List<Card>) {
        val s = _state.value
        val delta = when (result) {
            "WIN" -> s.bet
            "LOSE" -> -s.bet
            "BLACKJACK" -> (s.bet * 3) / 2   // 3:2 payout
            else -> 0
        }

        var newBalance = s.balance + delta
        if (newBalance <= 0) newBalance = 100

        val item = DashboardItem(
            id = nextId++,
            timeLabel = nowLabel(),
            mode = s.selectedMode,
            result = result,
            runningCount = s.runningCount,
            bet = s.bet,
            balanceAfter = newBalance
        )

        _state.value = s.copy(
            balance = newBalance,
            dealerCards = finalDealer,
            playerCards = finalPlayer,
            phase = HandPhase.FINISHED,
            message = message,
            dashboard = (listOf(item) + s.dashboard).take(20),
            shoeLabel = shoeProgress()
        )
    }

    private fun adjustCount(card: Card) {
        val s = _state.value
        _state.value = s.copy(runningCount = s.runningCount + card.hiLoValue)
    }

    private fun shoeProgress(): String {
        val sh = shoe ?: return ""
        return "Shoe: dealt ${sh.dealtCount}/${52 * sh.decks} | cut @ ${sh.cutIndex}"
    }
}