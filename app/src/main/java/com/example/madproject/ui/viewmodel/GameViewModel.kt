package com.example.madproject.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.madproject.core.game.Card
import com.example.madproject.core.game.Shoe
import com.example.madproject.core.game.cardsFromCsv
import com.example.madproject.core.game.cardsToCsv
import com.example.madproject.core.game.handTotal
import com.example.madproject.core.game.newShuffledShoe
import com.example.madproject.data.local.DeviceIdProvider
import com.example.madproject.data.local.entities.HandEntity
import com.example.madproject.data.local.entities.ShoeStateEntity
import com.example.madproject.data.remote.auth.AuthRepository
import com.example.madproject.data.remote.mapper.HandMetricsSnapshot
import com.example.madproject.data.remote.mapper.MetricsBuilder
import com.example.madproject.data.repo.HandRepository
import com.example.madproject.data.repo.MetricsRepository
import com.example.madproject.data.session.SessionManager
import com.example.madproject.domain.mapper.toModeCode
import com.example.madproject.domain.mapper.toOutcomeCode
import java.util.UUID
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class HandPhase { READY, PLAYER_TURN, INSURANCE_DECISION, DEALER_TURN, FINISHED }

data class GameUiState(
    val selectedMode: String = "BEGINNER",
    val decks: Int = 1,
    val cutMin: Double = 0.60,
    val cutMax: Double = 0.80,
    val balance: Int = 100,
    val bet: Int = 5,
    val baseBet: Int = 5,
    val runningCount: Int = 0,
    val trueCount: Double = 0.0,
    val dealtCount: Int = 0,
    val shoeLabel: String = "",
    val shoeNumber: Int = 1,
    val dealerCards: List<Card> = emptyList(),
    val playerHands: List<List<Card>> = emptyList(),
    val activeHandIndex: Int = 0,
    val handBets: List<Int> = emptyList(),
    val message: String = "Select a mode and press Deal",
    val phase: HandPhase = HandPhase.READY,
    val canDoubleDown: Boolean = false,
    val canSplit: Boolean = false,
    val dealerHoleCard: Card? = null,
    val insuranceOffered: Boolean = false,
    val insuranceTaken: Boolean = false,
    val insuranceBet: Int = 0,
    val previousBaseBet: Int? = null
)

class GameViewModel(
    private val handRepository: HandRepository,
    private val authRepository: AuthRepository,
    private val metricsRepository: MetricsRepository,
    private val deviceIdProvider: DeviceIdProvider,
    private val sessionManager: SessionManager
) : ViewModel() {

    private val filterMode = MutableStateFlow<String?>(null)
    private val sortAscending = MutableStateFlow(false)

    @OptIn(ExperimentalCoroutinesApi::class)
    val lastHands = combine(filterMode, sortAscending) { m: String?, asc: Boolean -> m to asc }
        .flatMapLatest { (m, asc) -> handRepository.observeHands(m, asc, 20) }

    fun setFilterMode(mode: String?) { filterMode.value = mode }
    fun setSortAscending(asc: Boolean) { sortAscending.value = asc }

    private val _state = MutableStateFlow(GameUiState())
    val state: StateFlow<GameUiState> = _state

    private var shoe: Shoe? = null
    private val betHistory = mutableListOf<Int>()

    fun selectMode(mode: String) {
        viewModelScope.launch {
            val restored = restoreModeFromDb(mode)
            if (restored) return@launch

            val (decks, cutMin, cutMax) = when (mode) {
                "BEGINNER" -> Triple(1, 0.60, 0.80)
                "INTERMEDIATE" -> Triple(6, 0.70, 0.80)
                "ADVANCED" -> Triple(8, 0.70, 0.80)
                else -> Triple(1, 0.60, 0.80)
            }

            shoe = newShuffledShoe(decks, cutMin, cutMax)

            _state.value = _state.value.copy(
                selectedMode = mode,
                decks = decks,
                cutMin = cutMin,
                cutMax = cutMax,
                runningCount = 0,
                trueCount = 0.0,
                dealtCount = shoe?.dealtCount ?: 0,
                shoeNumber = 1,
                bet = _state.value.baseBet,
                baseBet = _state.value.baseBet,
                dealerCards = emptyList(),
                playerHands = emptyList(),
                activeHandIndex = 0,
                handBets = emptyList(),
                phase = HandPhase.READY,
                message = "Mode set: $mode. Press Deal.",
                shoeLabel = shoeProgress(),
                canDoubleDown = false,
                canSplit = false,
                dealerHoleCard = null,
                insuranceOffered = false,
                insuranceTaken = false,
                insuranceBet = 0
            )

            persistSnapshot()
        }
    }

    private fun pushBetHistory(oldBet: Int) {
        betHistory.add(oldBet)
    }

    private fun updateBaseBet(newBaseBet: Int) {
        val s = _state.value
        val clamped = newBaseBet.coerceIn(5, s.balance.coerceAtLeast(5))
        if (clamped == s.baseBet) return

        pushBetHistory(s.baseBet)

        _state.value = s.copy(
            bet = clamped,
            baseBet = clamped,
            previousBaseBet = s.baseBet
        )

        viewModelScope.launch { persistSnapshot() }
    }

    fun betMinus5() {
        updateBaseBet(_state.value.baseBet - 5)
    }

    fun betPlus5() {
        updateBaseBet(_state.value.baseBet + 5)
    }

    fun betTimesTwo() {
        updateBaseBet(_state.value.baseBet * 2)
    }

    fun betAllIn() {
        updateBaseBet(_state.value.balance)
    }

    fun undoLastBetMove() {
        val last = betHistory.removeLastOrNull() ?: return
        val s = _state.value
        _state.value = s.copy(
            bet = last,
            baseBet = last,
            previousBaseBet = betHistory.lastOrNull()
        )
        viewModelScope.launch { persistSnapshot() }
    }

    fun deal() {
        val s = _state.value

        if (shoe == null) {
            shoe = newShuffledShoe(s.decks, s.cutMin, s.cutMax)
        } else if (shoe!!.pastCut()) {
            shoe!!.reshuffle(s.cutMin, s.cutMax)
            _state.value = _state.value.copy(
                runningCount = 0,
                trueCount = 0.0,
                dealtCount = shoe!!.dealtCount,
                shoeNumber = _state.value.shoeNumber + 1
            )
        }

        val sh = shoe!!

        val p1 = sh.draw().also { adjustCount(it) }
        val d1 = sh.draw().also { adjustCount(it) }
        val p2 = sh.draw().also { adjustCount(it) }
        val d2 = sh.draw().also { adjustCount(it) }

        val player = listOf(p1, p2)
        val dealerUpCard = listOf(d1)
        val insuranceAvailable = d1.rank.label == "A"

        _state.value = _state.value.copy(
            playerHands = listOf(player),
            activeHandIndex = 0,
            handBets = listOf(s.bet),
            dealerCards = dealerUpCard,
            dealerHoleCard = d2,
            phase = if (insuranceAvailable) HandPhase.INSURANCE_DECISION else HandPhase.PLAYER_TURN,
            message = if (insuranceAvailable) {
                "Dealer shows Ace - take insurance?"
            } else {
                "Your turn: Hit or Stand"
            },
            shoeLabel = shoeProgress(),
            dealtCount = sh.dealtCount,
            trueCount = calculateTrueCount(),
            canDoubleDown = !insuranceAvailable,
            canSplit = if (insuranceAvailable) false else canSplitHand(player, s.balance, s.bet),
            insuranceOffered = insuranceAvailable,
            insuranceTaken = false,
            insuranceBet = 0
        )

        viewModelScope.launch { persistSnapshot() }

        val playerTotal = handTotal(player)
        val dealerTotal = handTotal(listOf(d1, d2))
        val playerBlackjack = playerTotal == 21
        val dealerBlackjack = dealerTotal == 21

        when {
            playerBlackjack && dealerBlackjack -> {
                finishHand(
                    result = "PUSH",
                    message = "Both have blackjack - Push",
                    finalDealer = listOf(d1, d2),
                    finalPlayer = player
                )
                return
            }

            playerBlackjack -> {
                finishHand(
                    result = "BLACKJACK",
                    message = "Blackjack! Pays 3:2",
                    finalDealer = listOf(d1, d2),
                    finalPlayer = player
                )
                return
            }

            dealerBlackjack && !insuranceAvailable -> {
                finishHand(
                    result = "LOSE",
                    message = "Dealer has blackjack.",
                    finalDealer = listOf(d1, d2),
                    finalPlayer = player
                )
                return
            }
        }
    }

    fun hit() {
        val s = _state.value
        if (s.phase != HandPhase.PLAYER_TURN) return
        val sh = shoe ?: return

        val currentHand = currentPlayerHand()
        val currentTotal = handTotal(currentHand)
        if (currentTotal >= 21) return

        val card = sh.draw().also { adjustCount(it) }
        val newPlayer = currentHand + card
        val total = handTotal(newPlayer)

        val updatedHands = s.playerHands.toMutableList()
        updatedHands[s.activeHandIndex] = newPlayer

        if (total > 21) {
            _state.value = s.copy(
                playerHands = updatedHands,
                message = "Bust ($total)!",
                shoeLabel = shoeProgress(),
                dealtCount = sh.dealtCount,
                trueCount = calculateTrueCount(),
                canDoubleDown = false,
                canSplit = false
            )
            viewModelScope.launch { persistSnapshot() }
            moveToNextHandOrResolveAfterBust()
        } else if (total == 21) {
            _state.value = s.copy(
                playerHands = updatedHands,
                message = "21! Moving on...",
                shoeLabel = shoeProgress(),
                dealtCount = sh.dealtCount,
                trueCount = calculateTrueCount(),
                canDoubleDown = false,
                canSplit = false
            )
            viewModelScope.launch { persistSnapshot() }
            moveToNextHandOrDealer()
        } else {
            _state.value = s.copy(
                playerHands = updatedHands,
                message = "Hit or Stand",
                shoeLabel = shoeProgress(),
                dealtCount = sh.dealtCount,
                trueCount = calculateTrueCount(),
                canDoubleDown = false,
                canSplit = false
            )
            viewModelScope.launch { persistSnapshot() }
        }
    }

    fun standDealerResolution() {
        val s = _state.value
        if (s.phase != HandPhase.PLAYER_TURN) return
        val sh = shoe ?: return

        viewModelScope.launch {
            var dealer = s.dealerCards

            _state.value = _state.value.copy(
                phase = HandPhase.DEALER_TURN,
                message = "Dealer's turn..."
            )

            delay(500)

            val holeCard = s.dealerHoleCard
            if (holeCard != null) {
                dealer = dealer + holeCard

                _state.value = _state.value.copy(
                    dealerCards = dealer,
                    dealerHoleCard = null,
                    dealtCount = sh.dealtCount,
                    trueCount = calculateTrueCount(),
                    shoeLabel = shoeProgress(),
                    message = "Dealer reveals hole card..."
                )

                persistSnapshot()
                delay(800)
            }

            while (handTotal(dealer) < 17) {
                val nextCard = sh.draw().also { adjustCount(it) }
                dealer = dealer + nextCard

                _state.value = _state.value.copy(
                    dealerCards = dealer,
                    dealtCount = sh.dealtCount,
                    trueCount = calculateTrueCount(),
                    shoeLabel = shoeProgress(),
                    message = "Dealer draws..."
                )

                persistSnapshot()
                delay(800)
            }

            resolveAllHandsAgainstDealer(dealer)
        }
    }

    private fun outcomeFor(playerTotal: Int, dealerTotal: Int): String {
        return when {
            playerTotal > 21 -> "LOSE"
            dealerTotal > 21 -> "WIN"
            playerTotal > dealerTotal -> "WIN"
            playerTotal < dealerTotal -> "LOSE"
            else -> "PUSH"
        }
    }

    private fun deltaFor(result: String, bet: Int): Int {
        return when (result) {
            "WIN" -> bet
            "LOSE" -> -bet
            "BLACKJACK" -> (bet * 3) / 2
            else -> 0
        }
    }

    private fun resolveAllHandsAgainstDealer(finalDealer: List<Card>) {
        val s = _state.value
        val dealerTotal = handTotal(finalDealer)
        val resolvedHands = s.playerHands
        val resolvedBets = if (s.handBets.size == resolvedHands.size) {
            s.handBets
        } else {
            List(resolvedHands.size) { s.baseBet }
        }

        val results = mutableListOf<String>()
        var newBalance = s.balance

        resolvedHands.forEachIndexed { index, hand ->
            val playerTotal = handTotal(hand)
            val betForHand = resolvedBets.getOrElse(index) { s.baseBet }
            val result = outcomeFor(playerTotal, dealerTotal)

            results += "H${index + 1}: $result"
            newBalance += deltaFor(result, betForHand)
        }

        if (newBalance <= 0) newBalance = 100

        shoe?.discard(resolvedHands.flatten() + finalDealer)

        val summaryMessage =
            if (resolvedHands.size == 1) {
                val playerTotal = handTotal(resolvedHands.first())
                val result = outcomeFor(playerTotal, dealerTotal)
                "You: $playerTotal | Dealer: $dealerTotal → $result"
            } else {
                results.joinToString(" | ") + " | Dealer: $dealerTotal"
            }

        _state.value = s.copy(
            balance = newBalance,
            bet = s.baseBet,
            dealerCards = finalDealer,
            playerHands = resolvedHands,
            phase = HandPhase.FINISHED,
            message = summaryMessage,
            shoeLabel = shoeProgress(),
            dealtCount = shoe?.dealtCount ?: s.dealtCount,
            trueCount = calculateTrueCount(),
            canDoubleDown = false,
            canSplit = false,
            dealerHoleCard = null,
            insuranceOffered = false,
            insuranceTaken = false,
            insuranceBet = 0
        )

        viewModelScope.launch { persistSnapshot() }

        viewModelScope.launch {
            resolvedHands.forEachIndexed { index, hand ->
                val playerTotal = handTotal(hand)
                val betForHand = resolvedBets.getOrElse(index) { s.baseBet }
                val result = outcomeFor(playerTotal, dealerTotal)

                handRepository.insertHand(
                    HandEntity(
                        playedAtEpochMs = System.currentTimeMillis(),
                        mode = s.selectedMode,
                        bet = betForHand,
                        result = result,
                        playerTotal = playerTotal,
                        dealerTotal = dealerTotal,
                        runningCount = _state.value.runningCount,
                        trueCount = calculateTrueCount(),
                        decks = s.decks,
                        cardsRemaining = shoe?.remainingCount(),
                        balanceAfter = newBalance
                    )
                )

                uploadHandMetrics(
                    result = result,
                    playerTotal = playerTotal,
                    dealerTotal = dealerTotal,
                    balanceAfter = newBalance,
                    handBet = betForHand
                )
            }
        }
    }

    private fun finalizeAllBustedHands() {
        val s = _state.value
        val resolvedHands = s.playerHands
        val resolvedBets = if (s.handBets.size == resolvedHands.size) {
            s.handBets
        } else {
            List(resolvedHands.size) { s.baseBet }
        }

        var newBalance = s.balance
        resolvedHands.forEachIndexed { index, hand ->
            if (handTotal(hand) > 21) {
                val betForHand = resolvedBets.getOrElse(index) { s.baseBet }
                newBalance -= betForHand
            }
        }

        if (newBalance <= 0) newBalance = 100

        shoe?.discard(resolvedHands.flatten())

        val summaryMessage =
            if (resolvedHands.size == 1) {
                "Bust! You lose."
            } else {
                resolvedHands.mapIndexed { index, _ -> "H${index + 1}: LOSE" }
                    .joinToString(" | ") + " | All hands busted"
            }

        _state.value = s.copy(
            balance = newBalance,
            bet = s.baseBet,
            dealerCards = emptyList(),
            playerHands = resolvedHands,
            phase = HandPhase.FINISHED,
            message = summaryMessage,
            shoeLabel = shoeProgress(),
            dealtCount = shoe?.dealtCount ?: s.dealtCount,
            trueCount = calculateTrueCount(),
            canDoubleDown = false,
            canSplit = false,
            dealerHoleCard = null,
            insuranceOffered = false,
            insuranceTaken = false,
            insuranceBet = 0
        )

        viewModelScope.launch { persistSnapshot() }

        viewModelScope.launch {
            resolvedHands.forEachIndexed { index, hand ->
                if (handTotal(hand) > 21) {
                    val betForHand = resolvedBets.getOrElse(index) { s.baseBet }

                    handRepository.insertHand(
                        HandEntity(
                            playedAtEpochMs = System.currentTimeMillis(),
                            mode = s.selectedMode,
                            bet = betForHand,
                            result = "LOSE",
                            playerTotal = handTotal(hand),
                            dealerTotal = 0,
                            runningCount = _state.value.runningCount,
                            trueCount = calculateTrueCount(),
                            decks = s.decks,
                            cardsRemaining = shoe?.remainingCount(),
                            balanceAfter = newBalance
                        )
                    )

                    uploadHandMetrics(
                        result = "LOSE",
                        playerTotal = handTotal(hand),
                        dealerTotal = 0,
                        balanceAfter = newBalance,
                        handBet = betForHand
                    )
                }
            }
        }
    }

    fun doubleDown() {
        val s = _state.value
        if (s.phase != HandPhase.PLAYER_TURN) return
        if (!s.canDoubleDown) return
        if (s.balance < s.bet) return

        val sh = shoe ?: return
        val currentHand = currentPlayerHand()
        val doubledBet = s.bet * 2

        val updatedBets = s.handBets.toMutableList()
        if (s.activeHandIndex in updatedBets.indices) {
            updatedBets[s.activeHandIndex] = doubledBet
        }

        val card = sh.draw().also { adjustCount(it) }
        val newPlayer = currentHand + card
        val total = handTotal(newPlayer)

        val updatedHands = s.playerHands.toMutableList()
        updatedHands[s.activeHandIndex] = newPlayer

        _state.value = s.copy(
            bet = doubledBet,
            playerHands = updatedHands,
            handBets = updatedBets,
            dealtCount = sh.dealtCount,
            trueCount = calculateTrueCount(),
            shoeLabel = shoeProgress(),
            message = "Double Down! One card only.",
            canDoubleDown = false,
            canSplit = false
        )

        viewModelScope.launch { persistSnapshot() }

        if (total > 21) {
            _state.value = _state.value.copy(message = "Double Down bust ($total)!")
            moveToNextHandOrResolveAfterBust()
        } else {
            moveToNextHandOrDealer()
        }
    }

    private fun canSplitHand(hand: List<Card>, balance: Int, bet: Int): Boolean {
        return hand.size == 2 &&
                hand[0].rank == hand[1].rank &&
                balance >= bet
    }

    private fun currentPlayerHand(): List<Card> {
        val s = _state.value
        return s.playerHands.getOrElse(s.activeHandIndex) { emptyList() }
    }

    fun split() {
        val s = _state.value
        if (s.phase != HandPhase.PLAYER_TURN) return
        if (!s.canSplit) return

        val sh = shoe ?: return
        val originalHand = s.playerHands.getOrNull(s.activeHandIndex) ?: return
        if (originalHand.size != 2) return

        val firstHandBase = listOf(originalHand[0])
        val secondHandBase = listOf(originalHand[1])

        val firstDraw = sh.draw().also { adjustCount(it) }
        val secondDraw = sh.draw().also { adjustCount(it) }

        val splitHands = listOf(
            firstHandBase + firstDraw,
            secondHandBase + secondDraw
        )

        val splitBets = listOf(s.bet, s.bet)

        _state.value = s.copy(
            playerHands = splitHands,
            activeHandIndex = 0,
            handBets = splitBets,
            dealtCount = sh.dealtCount,
            trueCount = calculateTrueCount(),
            shoeLabel = shoeProgress(),
            message = "Split! Playing hand 1 of 2.",
            canDoubleDown = true,
            canSplit = false
        )

        viewModelScope.launch { persistSnapshot() }
    }

    fun takeInsurance() {
        val s = _state.value

        if (s.phase != HandPhase.INSURANCE_DECISION) return
        if (!s.insuranceOffered || s.insuranceTaken) return

        val insuranceCost = (s.bet / 2).coerceAtLeast(1)

        if (s.balance < insuranceCost) {
            _state.value = s.copy(
                message = "Not enough balance for insurance"
            )
            return
        }

        val dealerFullHand = s.dealerCards + listOfNotNull(s.dealerHoleCard)
        val dealerHasBlackjack = handTotal(dealerFullHand) == 21

        if (dealerHasBlackjack) {
            _state.value = s.copy(
                balance = s.balance - insuranceCost,
                insuranceTaken = true,
                insuranceBet = insuranceCost
            )
            finishHand(
                result = "LOSE",
                message = "Dealer has blackjack. Insurance pays 2:1.",
                finalDealer = dealerFullHand,
                finalPlayer = s.playerHands.first()
            )
        } else {
            _state.value = s.copy(
                balance = s.balance - insuranceCost,
                insuranceTaken = true,
                insuranceBet = insuranceCost,
                phase = HandPhase.PLAYER_TURN,
                message = "Insurance taken. Your turn: Hit or Stand",
                canDoubleDown = true,
                canSplit = canSplitHand(s.playerHands.first(), s.balance, s.bet)
            )
            viewModelScope.launch { persistSnapshot() }
        }
    }

    fun skipInsurance() {
        val s = _state.value

        if (s.phase != HandPhase.INSURANCE_DECISION) return

        val dealerFullHand = s.dealerCards + listOfNotNull(s.dealerHoleCard)
        val dealerHasBlackjack = handTotal(dealerFullHand) == 21

        if (dealerHasBlackjack) {
            finishHand(
                result = "LOSE",
                message = "Dealer has blackjack.",
                finalDealer = dealerFullHand,
                finalPlayer = s.playerHands.first()
            )
        } else {
            _state.value = s.copy(
                phase = HandPhase.PLAYER_TURN,
                message = "Your turn: Hit or Stand",
                canDoubleDown = true,
                canSplit = canSplitHand(s.playerHands.first(), s.balance, s.bet)
            )
            viewModelScope.launch { persistSnapshot() }
        }
    }

    fun stand() {
        val s = _state.value
        if (s.phase != HandPhase.PLAYER_TURN) return
        moveToNextHandOrDealer()
    }

    private fun advanceToNextHand(): Boolean {
        val s = _state.value
        if (s.activeHandIndex + 1 < s.playerHands.size) {
            val nextIndex = s.activeHandIndex + 1
            val nextHand = s.playerHands[nextIndex]
            val nextBet = s.handBets.getOrElse(nextIndex) { s.baseBet }

            _state.value = s.copy(
                activeHandIndex = nextIndex,
                bet = nextBet,
                message = "Hand ${nextIndex + 1} of ${s.playerHands.size}",
                canDoubleDown = nextHand.size == 2 && s.balance >= nextBet,
                canSplit = false
            )
            return true
        }
        return false
    }

    private fun moveToNextHandOrDealer() {
        if (!advanceToNextHand()) {
            standDealerResolution()
        }
    }

    private fun moveToNextHandOrResolveAfterBust() {
        if (advanceToNextHand()) return

        val anyLiveHands = _state.value.playerHands.any { handTotal(it) <= 21 }
        if (anyLiveHands) {
            standDealerResolution()
        } else {
            finalizeAllBustedHands()
        }
    }

    private fun finishHand(
        result: String,
        message: String,
        finalDealer: List<Card>,
        finalPlayer: List<Card>
    ) {
        val s = _state.value

        val handBet = s.bet
        val delta = when (result) {
            "WIN" -> handBet
            "LOSE" -> -handBet
            "BLACKJACK" -> (handBet * 3) / 2
            else -> 0
        }

        var insuranceDelta = 0
        if (s.insuranceTaken && handTotal(finalDealer) == 21 && finalDealer.size == 2) {
            insuranceDelta = s.insuranceBet * 3
        }

        var newBalance = s.balance + delta + insuranceDelta
        if (newBalance <= 0) newBalance = 100

        val pt = handTotal(finalPlayer)
        val dt = handTotal(finalDealer)

        shoe?.discard(finalPlayer + finalDealer)

        val updatedHands = if (s.playerHands.isEmpty()) {
            listOf(finalPlayer)
        } else {
            s.playerHands.toMutableList().also { hands ->
                if (s.activeHandIndex in hands.indices) {
                    hands[s.activeHandIndex] = finalPlayer
                }
            }
        }

        _state.value = s.copy(
            balance = newBalance,
            bet = s.baseBet,
            dealerCards = finalDealer,
            playerHands = updatedHands,
            phase = HandPhase.FINISHED,
            message = message,
            shoeLabel = shoeProgress(),
            dealtCount = shoe?.dealtCount ?: s.dealtCount,
            trueCount = calculateTrueCount(),
            canDoubleDown = false,
            canSplit = false,
            dealerHoleCard = null,
            insuranceOffered = false,
            insuranceTaken = false,
            insuranceBet = 0
        )

        viewModelScope.launch { persistSnapshot() }

        viewModelScope.launch {
            handRepository.insertHand(
                HandEntity(
                    playedAtEpochMs = System.currentTimeMillis(),
                    mode = s.selectedMode,
                    bet = handBet,
                    result = result,
                    playerTotal = pt,
                    dealerTotal = dt,
                    runningCount = _state.value.runningCount,
                    trueCount = calculateTrueCount(),
                    decks = s.decks,
                    cardsRemaining = shoe?.remainingCount(),
                    balanceAfter = newBalance
                )
            )

            uploadHandMetrics(
                result = result,
                playerTotal = pt,
                dealerTotal = dt,
                balanceAfter = newBalance,
                handBet = handBet
            )
        }
    }

    private fun adjustCount(card: Card) {
        val s = _state.value

        val countDelta = when (s.selectedMode) {
            "ADVANCED" -> card.omegaIiValue
            else -> card.hiLoValue
        }

        _state.value = s.copy(
            runningCount = s.runningCount + countDelta
        )
    }

    private fun shoeProgress(): String {
        val sh = shoe ?: return ""
        return "Shoe: dealt ${sh.dealtCount}/${52 * sh.decks} | cut @ ${sh.cutIndex}"
    }

    private fun calculateTrueCount(): Double {
        val sh = shoe ?: return 0.0
        val cardsRemaining = sh.remainingCount().coerceAtLeast(1)
        val remainingDecks = cardsRemaining.toDouble() / 52.0
        if (remainingDecks <= 0.0) return 0.0

        val rawTrueCount = _state.value.runningCount.toDouble() / remainingDecks
        return kotlin.math.round(rawTrueCount * 10) / 10.0
    }

    fun clearHistory() {
        viewModelScope.launch {
            handRepository.clearAllGameData()
            shoe = null
            _state.value = GameUiState()
        }
    }

    private suspend fun persistSnapshot() {
        val sh = shoe ?: return
        val s = _state.value

        withContext(Dispatchers.IO) {
            handRepository.saveShoeState(
                ShoeStateEntity(
                    selectedMode = s.selectedMode,
                    decks = sh.decks,
                    cutMin = s.cutMin,
                    cutMax = s.cutMax,
                    balance = s.balance,
                    bet = s.bet,
                    runningCount = s.runningCount,
                    shoeNumber = s.shoeNumber,
                    cutIndex = sh.cutIndex,
                    dealtCount = sh.dealtCount,
                    cardsCsv = cardsToCsv(sh.cards),
                    discardsCsv = cardsToCsv(sh.discards),
                    baseBet = s.baseBet,
                    playerHandsCsv = handsToCsv(s.playerHands),
                    activeHandIndex = s.activeHandIndex,
                    handBetsCsv = intsToCsv(s.handBets),
                    dealerCardsCsv = cardsToCsv(s.dealerCards),
                    phase = s.phase.name,
                    message = s.message
                )
            )
        }
    }

    fun persistOnAppBackground() {
        viewModelScope.launch {
            persistSnapshot()
        }
    }

    private suspend fun restoreModeFromDb(mode: String): Boolean {
        val savedShoe = handRepository.getShoeState(mode) ?: return false

        shoe = Shoe(
            decks = savedShoe.decks,
            cards = cardsFromCsv(savedShoe.cardsCsv),
            cutIndex = savedShoe.cutIndex,
            discards = cardsFromCsv(savedShoe.discardsCsv),
            dealtCount = savedShoe.dealtCount
        )

        val restoredHands = handsFromCsv(savedShoe.playerHandsCsv)
        val restoredBets = intsFromCsv(savedShoe.handBetsCsv)
        val restoredActiveIndex = savedShoe.activeHandIndex.coerceIn(
            0,
            (restoredHands.size - 1).coerceAtLeast(0)
        )

        val activeBet = restoredBets.getOrElse(restoredActiveIndex) { savedShoe.bet }
        val activeHand = restoredHands.getOrElse(restoredActiveIndex) { emptyList() }

        _state.value = GameUiState(
            selectedMode = savedShoe.selectedMode,
            decks = savedShoe.decks,
            cutMin = savedShoe.cutMin,
            cutMax = savedShoe.cutMax,
            balance = savedShoe.balance,
            bet = activeBet,
            baseBet = savedShoe.baseBet,
            runningCount = savedShoe.runningCount,
            trueCount = 0.0,
            dealtCount = savedShoe.dealtCount,
            shoeNumber = savedShoe.shoeNumber,
            shoeLabel = shoeProgress(),
            dealerCards = cardsFromCsv(savedShoe.dealerCardsCsv),
            playerHands = restoredHands,
            activeHandIndex = restoredActiveIndex,
            handBets = restoredBets,
            message = "Continuing saved ${savedShoe.selectedMode} game.",
            phase = HandPhase.valueOf(savedShoe.phase),
            canDoubleDown = activeHand.size == 2 &&
                    savedShoe.phase == HandPhase.PLAYER_TURN.name &&
                    savedShoe.balance >= activeBet,
            canSplit = false
        )

        _state.value = _state.value.copy(
            trueCount = calculateTrueCount()
        )

        return true
    }

    private suspend fun restoreLastKnownBalanceAndBet() {
        val mostRecentHand = handRepository.getMostRecentHand()
        if (mostRecentHand != null) {
            _state.value = _state.value.copy(
                bet = mostRecentHand.bet,
                baseBet = mostRecentHand.bet,
                balance = mostRecentHand.balanceAfter
            )
        }
    }

    fun handsToCsv(hands: List<List<Card>>): String {
        return hands.joinToString("||") { hand -> cardsToCsv(hand) }
    }

    fun handsFromCsv(csv: String): List<List<Card>> {
        if (csv.isBlank()) return emptyList()
        return csv.split("||").map { handCsv ->
            cardsFromCsv(handCsv)
        }
    }

    fun intsToCsv(values: List<Int>): String {
        return values.joinToString(",")
    }

    fun intsFromCsv(csv: String): List<Int> {
        if (csv.isBlank()) return emptyList()
        return csv.split(",").mapNotNull { it.toIntOrNull() }
    }

    private suspend fun uploadHandMetrics(
        result: String,
        playerTotal: Int,
        dealerTotal: Int,
        balanceAfter: Int,
        handBet: Int
    ) {
        val userId = authRepository.currentUserId() ?: return

        val handId = UUID.randomUUID().toString()

        val recentHands = handRepository.getRecentHandsList(limit = 100)
        val completedHands = recentHands.count {
            it.result == "WIN" || it.result == "LOSE" || it.result == "PUSH" || it.result == "BLACKJACK"
        } + 1

        val wins = recentHands.count {
            it.result == "WIN" || it.result == "BLACKJACK"
        } + if (result == "WIN" || result == "BLACKJACK") 1 else 0

        val rawWinRate = if (completedHands == 0) 0.0
        else (wins.toDouble() / completedHands.toDouble()) * 100.0

        val winRate = kotlin.math.round(rawWinRate * 100) / 100.0

        val trueCountValue = calculateTrueCount()

        val snapshot = HandMetricsSnapshot(
            userId = userId,
            deviceId = deviceIdProvider.getDeviceId(),
            sessionId = sessionManager.getSessionId(),
            handId = handId,
            balance = balanceAfter.toDouble(),
            betAmount = handBet.toDouble(),
            runningCount = _state.value.runningCount.toDouble(),
            trueCount = trueCountValue,
            winRate = winRate,
            modeCode = _state.value.selectedMode.toModeCode(),
            shoeNumber = _state.value.shoeNumber.toDouble(),
            playerTotal = playerTotal.toDouble(),
            dealerTotal = dealerTotal.toDouble(),
            outcomeCode = result.toOutcomeCode()
        )

        val metrics = MetricsBuilder.build(snapshot)
        android.util.Log.d(
            "METRICS_UPLOAD",
            "Uploading handId=$handId sessionId=${sessionManager.getSessionId()} result=$result metrics=${metrics.size}"
        )

        metricsRepository.uploadMetrics(metrics)
    }

    init {
        viewModelScope.launch {
            restoreLastKnownBalanceAndBet()
        }
    }
}
