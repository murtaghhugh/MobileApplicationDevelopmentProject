# MobileApplicationDevelopmentProject

# Blackjack Card Counting Trainer
Android | Kotlin | Jetpack Compose | MVVM

A **blackjack training application** built using **Kotlin and Jetpack Compose** that simulates casino blackjack while teaching users **card counting techniques**.

The application includes multiple game modes with different deck sizes, running card counts, adjustable betting, and a dashboard of recent gameplay results.

This project demonstrates modern Android architecture practices including:

- Jetpack Compose UI
- MVVM architecture
- StateFlow reactive state management
- Compose Navigation
- ViewModel-driven game engine

---

# Features

## Blackjack Gameplay

- European-style blackjack
- Player receives **two cards**
- Dealer receives **one visible card**
- Player actions:
  - Deal
  - Hit
  - Stand

### Dealer Rules

| Rule | Behaviour |
|-----|-----------|
| Dealer hits | 16 or less |
| Dealer stands | 17 or higher |

---

# Blackjack Rules

| Rule | Behaviour |
|------|-----------|
| Ace value | 1 or 11 |
| Blackjack detection | On initial deal |
| Blackjack payout | **3:2** |

Example payout:
Bet: 10
Blackjack payout: +15
---

# Betting System

| Feature | Behaviour |
|-------|-----------|
| Starting balance | 100 |
| Minimum bet | 5 |
| Adjust bet | +5 / -5 |
| Win | Balance increases |
| Loss | Balance decreases |
| Bankrupt | Balance resets to 100 |

---

# Card Counting

The game uses the **Hi-Lo card counting system**.

| Card | Count Value |
|------|-------------|
| 2–6 | +1 |
| 7–9 | 0 |
| 10–Ace | -1 |

The **running count updates automatically** as cards are dealt.

---

# Game Modes

| Mode | Decks | Shuffle Depth |
|------|------|---------------|
| Beginner | 1 deck | 60–80% |
| Intermediate | 6 decks | 70–80% |
| Advanced | 8 decks | 70–80% |

Each mode simulates a casino **shoe with a cut card**.

---

# Dashboard

The dashboard displays the **last 20 hands played**.

Each record includes:

- Timestamp
- Game mode
- Result (Win / Lose / Push / Blackjack)
- Running count
- Bet size
- Balance after hand

Currently the dashboard is stored **in memory using StateFlow**.

Future versions will persist this data using **Room Database**.

---

# Architecture

The application follows **MVVM architecture**.

---

# Betting System

| Feature | Behaviour |
|-------|-----------|
| Starting balance | 100 |
| Minimum bet | 5 |
| Adjust bet | +5 / -5 |
| Win | Balance increases |
| Loss | Balance decreases |
| Bankrupt | Balance resets to 100 |

---

# Card Counting

The game uses the **Hi-Lo card counting system**.

| Card | Count Value |
|------|-------------|
| 2–6 | +1 |
| 7–9 | 0 |
| 10–Ace | -1 |

The **running count updates automatically** as cards are dealt.

---

# Game Modes

| Mode | Decks | Shuffle Depth |
|------|------|---------------|
| Beginner | 1 deck | 60–80% |
| Intermediate | 6 decks | 70–80% |
| Advanced | 8 decks | 70–80% |

Each mode simulates a casino **shoe with a cut card**.

---

# Dashboard

The dashboard displays the **last 20 hands played**.

Each record includes:

- Timestamp
- Game mode
- Result (Win / Lose / Push / Blackjack)
- Running count
- Bet size
- Balance after hand

Currently the dashboard is stored **in memory using StateFlow**.

Future versions will persist this data using **Room Database**.

---

# Architecture

The application follows **MVVM architecture**.
UI (Jetpack Compose Screens)
│
▼
GameViewModel
│
▼
Game Engine
│
▼
Card + Shoe Models

### Responsibilities

| Layer | Responsibility |
|------|---------------|
| UI | Displays game state |
| ViewModel | Game state and business logic |
| Game Engine | Card logic and blackjack rules |

---

# Project Structure

```
app
|
|-- core
|   |
|   |-- game
|       |-- Card.kt
|       |-- Shoe.kt
|       |-- DeckBuilder.kt
|       |-- HandLogic.kt
|
|-- navigation
|   |
|   |-- NavGraph.kt
|
|-- ui
|   |
|   |-- screens
|   |   |
|   |   |-- auth
|   |   |   |-- LoginScreen.kt
|   |   |   |-- SignUpScreen.kt
|   |   |
|   |   |-- home
|   |   |   |-- HomeScreen.kt
|   |   |
|   |   |-- game
|   |   |   |-- GameModeScreen.kt
|   |   |   |-- GameScreen.kt
|   |   |
|   |   |-- dashboard
|   |   |   |-- DashboardScreen.kt
|   |   |   |-- ShoeDetailScreen.kt
|   |   |
|   |   |-- account
|   |   |   |-- AccountScreen.kt
|   |   |
|   |   |-- info
|   |       |-- InfoScreen.kt
|   |
|   |-- viewmodel
|       |-- GameViewModel.kt
|
|-- MainActivity.kt
```
### Key Directories

- **core/game** – Blackjack engine and card logic  
- **ui/screens** – All Jetpack Compose UI screens  
- **ui/viewmodel** – Application state and game logic  
- **navigation** – Compose navigation graph  
---

# Technologies Used

| Technology | Purpose |
|-----------|--------|
| Kotlin | Programming language |
| Jetpack Compose | UI framework |
| Android ViewModel | State management |
| Kotlin StateFlow | Reactive data updates |
| Compose Navigation | Screen navigation |
| MVVM | Architecture pattern |

---

# Current Status

The **MVP gameplay loop is complete**.

Working features include:

- Blackjack game engine
- Multi-deck shoe system
- Hi-Lo card counting
- Adjustable betting
- Running balance
- Blackjack detection and payout
- Dashboard history
- Navigation between screens

---

# Roadmap

## Persistence
- Room Database for sessions
- Store full shoe statistics
- Long-term dashboard history

## Gameplay
- Double Down
- Split hands
- Insurance option
- True count calculation

## UI Improvements
- Graphical playing cards
- Betting popup interface
- Animations
- Improved layout

## Cloud Features
- Sync stats with Supabase or Firebase
- Multi-device gameplay tracking

---

# Setup Instructions

### Clone the repository

```bash
git clone https://github.com/yourusername/blackjack-card-counter.git
Open in Android Studio

Open the project using Android Studio Hedgehog or newer.

Run the application
Run → app

