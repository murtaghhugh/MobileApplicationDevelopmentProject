# Blackjack Card Counting Trainer
**Android | Kotlin | Jetpack Compose | Room | Supabase | MVVM**

A **Blackjack training application** built using **Kotlin and Jetpack Compose** that simulates casino blackjack while teaching users **card counting techniques**.

The application includes:

- Multiple game modes with different deck sizes  
- Running card counts and true counts  
- Adjustable betting system  
- Persistent gameplay history using Room  
- Cloud metric export using Supabase  
- Integration with the **Context of the Code (COTC)** data pipeline  

---

# Project Purpose

This project was developed for the **Mobile Application Development module**.

The aim is to demonstrate:

- Modern Android architecture
- Reactive UI design with Compose
- Persistent local storage
- Cloud database integration
- Multi-screen navigation
- Clean architecture and separation of concerns

---

# Features

## Blackjack Gameplay

- European-style blackjack simulation
- Player receives **two cards**
- Dealer receives **one visible card**

### Player Actions

| Action | Description |
|------|-------------|
| Deal | Start a new hand |
| Hit | Take another card |
| Stand | End the player's turn |

---

## Dealer Rules

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

Example Payout:
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

The game implements the **Hi-Lo card counting system**.

| Card | Count Value |
|------|-------------|
| 2–6 | +1 |
| 7–9 | 0 |
| 10–Ace | -1 |

### Running Count

The running count automatically updates as cards are dealt.

### True Count
True Count = Running Count / Remaining Decks

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

The game implements the **Hi-Lo card counting system**.

| Card | Count Value |
|------|-------------|
| 2–6 | +1 |
| 7–9 | 0 |
| 10–Ace | -1 |

### Running Count

The running count automatically updates as cards are dealt.

### True Count
True Count = Running Count / Remaining Decks 

The true count adjusts the running count based on the number of decks remaining in the shoe.

---

# Game Modes

| Mode | Decks | Shuffle Depth |
|------|------|---------------|
| Beginner | 1 deck | 60–80% |
| Intermediate | 6 decks | 70–80% |
| Advanced | 8 decks | 70–80% |

Each mode simulates a **casino multi-deck shoe with a cut card**.

---

# Dashboard

The dashboard displays the **most recent blackjack hands played**.

Each record includes:

- Timestamp
- Game mode
- Result (Win / Lose / Push / Blackjack)
- Player total
- Dealer total
- Running count
- True count
- Bet size
- Balance after hand

Data automatically updates using **Flow / StateFlow**.

---

# Local Persistence (Room Database)

The application uses **Room Database** for persistent storage.

## Tables

### `hands`

Stores completed blackjack hands.

| Field | Description |
|------|-------------|
| playedAtEpochMs | Hand completion time |
| mode | Game mode |
| bet | Bet amount |
| result | Hand outcome |
| playerTotal | Player score |
| dealerTotal | Dealer score |
| runningCount | Running card count |
| trueCount | True count value |
| decks | Number of decks |
| cardsRemaining | Remaining cards |
| balanceAfter | Player balance |

---

### `shoe_state`

Stores the **current game state**, allowing the app to resume after restart.

Includes:

- deck configuration
- running count
- current balance
- current bet
- remaining cards
- player cards
- dealer cards
- game phase

---

# Cloud Metrics (Supabase)

Gameplay metrics are exported to **Supabase**.

Each completed hand uploads multiple **metric events**.

## Metrics captured

| Metric | Description |
|------|-------------|
| balance | Player balance after hand |
| bet_amount | Bet size |
| running_count | Running card count |
| true_count | Calculated true count |
| win_rate | Player win percentage |
| mode_code | Game mode identifier |
| shoe_number | Current shoe number |
| player_total | Player hand total |
| dealer_total | Dealer hand total |
| outcome_code | Win / Lose / Push / Blackjack |

### Metadata stored

- `user_id`
- `device_id`
- `session_id`
- `hand_id`
- `recorded_at`

---

# Authentication

User accounts are managed using **Supabase Authentication**.

Features include:

- User sign-up
- User login
- Authenticated metric storage
- Row Level Security (RLS)

---

# COTC Integration

This application acts as a **mobile data producer** for the **Context of the Code (COTC)** project.

### Data Flow

The true count adjusts the running count based on the number of decks remaining in the shoe.

---

# Game Modes

| Mode | Decks | Shuffle Depth |
|------|------|---------------|
| Beginner | 1 deck | 60–80% |
| Intermediate | 6 decks | 70–80% |
| Advanced | 8 decks | 70–80% |

Each mode simulates a **casino multi-deck shoe with a cut card**.

---

# 📊 Dashboard

The dashboard displays the **most recent blackjack hands played**.

Each record includes:

- Timestamp
- Game mode
- Result (Win / Lose / Push / Blackjack)
- Player total
- Dealer total
- Running count
- True count
- Bet size
- Balance after hand

Data automatically updates using **Flow / StateFlow**.

---

# Local Persistence (Room Database)

The application uses **Room Database** for persistent storage.

## Tables

### `hands`

Stores completed blackjack hands.

| Field | Description |
|------|-------------|
| playedAtEpochMs | Hand completion time |
| mode | Game mode |
| bet | Bet amount |
| result | Hand outcome |
| playerTotal | Player score |
| dealerTotal | Dealer score |
| runningCount | Running card count |
| trueCount | True count value |
| decks | Number of decks |
| cardsRemaining | Remaining cards |
| balanceAfter | Player balance |

---

### `shoe_state`

Stores the **current game state**, allowing the app to resume after restart.

Includes:

- deck configuration
- running count
- current balance
- current bet
- remaining cards
- player cards
- dealer cards
- game phase

---

# Cloud Metrics (Supabase)

Gameplay metrics are exported to **Supabase**.

Each completed hand uploads multiple **metric events**.

## Metrics captured

| Metric | Description |
|------|-------------|
| balance | Player balance after hand |
| bet_amount | Bet size |
| running_count | Running card count |
| true_count | Calculated true count |
| win_rate | Player win percentage |
| mode_code | Game mode identifier |
| shoe_number | Current shoe number |
| player_total | Player hand total |
| dealer_total | Dealer hand total |
| outcome_code | Win / Lose / Push / Blackjack |

### Metadata stored

- `user_id`
- `device_id`
- `session_id`
- `hand_id`
- `recorded_at`

---

# Authentication

User accounts are managed using **Supabase Authentication**.

Features include:

- User sign-up
- User login
- Authenticated metric storage
- Row Level Security (RLS)

---

# COTC Integration

This application acts as a **mobile data producer** for the **Context of the Code (COTC)** project.

### Data Flow
Mobile App
↓
App Supabase Metrics Table
↓
COTC Data Collector
↓
COTC Processing Pipeline

The mobile app produces structured gameplay metrics which are collected and processed by the **COTC system**.

---

# Architecture

The application follows **MVVM architecture**.
UI (Jetpack Compose Screens)
│
▼
ViewModel (GameViewModel)
│
▼
Repositories
│
▼
Local Database (Room)
Cloud Database (Supabase)

### Layer Responsibilities

| Layer | Responsibility |
|------|---------------|
| UI | Displays game state |
| ViewModel | Game logic and state management |
| Repository | Data abstraction layer |
| Local Data | Room database storage |
| Cloud Data | Supabase metric export |

---

# Project Structure
app
│
├── core
│ └── game
│ ├── Card.kt
│ ├── Shoe.kt
│ ├── DeckBuilder.kt
│ └── HandLogic.kt
│
├── data
│ ├── local
│ │ ├── db
│ │ ├── dao
│ │ └── entities
│ │
│ ├── remote
│ │ ├── auth
│ │ └── mapper
│ │
│ ├── repo
│ │ ├── HandRepository.kt
│ │ └── MetricsRepository.kt
│ │
│ └── session
│
├── navigation
│ └── NavGraph.kt
│
├── ui
│ ├── screens
│ │ ├── auth
│ │ ├── game
│ │ ├── dashboard
│ │ ├── account
│ │ └── info
│ │
│ └── viewmodel
│ └── GameViewModel.kt
│
└── MainActivity.kt

---

# Technologies Used

| Technology | Purpose |
|-----------|--------|
| Kotlin | Programming language |
| Jetpack Compose | UI framework |
| Android ViewModel | State management |
| Kotlin Coroutines | Concurrency |
| StateFlow | Reactive UI updates |
| Compose Navigation | Screen navigation |
| Room | Local persistence |
| Supabase | Cloud database & authentication |
| MVVM | Architecture pattern |

---

# Current Status

Core systems currently implemented:

- Blackjack gameplay engine
- Multi-deck shoe simulation
- Hi-Lo card counting
- True count calculation
- Adjustable betting system
- Persistent gameplay history
- Room database integration
- Supabase authentication
- Supabase metric export
- Compose navigation
- Dashboard UI
- Session and device tracking

---

# Planned Improvements

### Gameplay
- Double Down
- Split hands
- Insurance option

### UI Improvements
- Graphical playing cards
- Game animations
- Improved layout and theme

### Additional Features
- Tips & strategy training section
- Player statistics dashboard
- Account information screen

---

# Setup Instructions

### Clone the repository

---

# 🛠 Technologies Used

| Technology | Purpose |
|-----------|--------|
| Kotlin | Programming language |
| Jetpack Compose | UI framework |
| Android ViewModel | State management |
| Kotlin Coroutines | Concurrency |
| StateFlow | Reactive UI updates |
| Compose Navigation | Screen navigation |
| Room | Local persistence |
| Supabase | Cloud database & authentication |
| MVVM | Architecture pattern |

---

# Current Status

Core systems currently implemented:

- Blackjack gameplay engine
- Multi-deck shoe simulation
- Hi-Lo card counting
- True count calculation
- Adjustable betting system
- Persistent gameplay history
- Room database integration
- Supabase authentication
- Supabase metric export
- Compose navigation
- Dashboard UI
- Session and device tracking

---

# Planned Improvements

### Gameplay
- Double Down
- Split hands
- Insurance option

### UI Improvements
- Graphical playing cards
- Game animations
- Improved layout and theme

### Additional Features
- Tips & strategy training section
- Player statistics dashboard
- Account information screen

---

# Setup Instructions

### Clone the repository
git clone https://github.com/yourusername/blackjack-card-counter.git

### Open in Android Studio

Open the project using **Android Studio Hedgehog or newer**.

### Run the application
Run → app


Example payout:
