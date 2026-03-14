# MobileApplicationDevelopmentProject

## Blackjack Card Counting Trainer
Android | Kotlin | Jetpack Compose | MVVM | Room | Supabase

A Blackjack training application built using Kotlin and Jetpack Compose that simulates casino blackjack while helping users practice card counting techniques.

The application allows players to train using different card counting systems while maintaining realistic blackjack gameplay mechanics. The goal is to provide a learning tool that helps users understand how card counting works while also demonstrating modern Android development practices such as reactive UI, persistent state, background processing, and cloud integration.

This project was developed as part of the Mobile Application Development module.

---

## Application Features

### Blackjack Gameplay
- Full blackjack gameplay simulation
- Dealer logic with hidden hole card
- Player actions including Hit, Stand, Double Down, and Split
- Insurance decision phase when the dealer shows an Ace
- Multiple simultaneous hands when splitting
- Running and true card count calculations

### Card Counting Training Modes

Three difficulty levels are provided to help users gradually learn card counting.

#### Beginner
- Single deck
- Hi-Lo counting system

#### Intermediate
- Six deck shoe
- Hi-Lo counting system
- True count calculation

#### Advanced
- Eight deck shoe
- Omega II counting system

---

## Betting System

The betting system allows the user to adjust their bet before dealing.

Features include:

- Increase bet
- Decrease bet
- Double bet (x2)
- All-in bet
- Undo previous bet change

---

## Dashboard and Statistics

The application includes a reactive dashboard that tracks gameplay history.

Features include:

- LazyColumn based list of previous hands
- Sorting options (Newest / Oldest)
- Persistent storage of completed hands
- Automatic UI updates using Flow

---

## Persistent Data

The application uses Room to store data locally.

Two main tables are used:

- **HandEntity** – stores completed blackjack hands
- **ShoeStateEntity** – stores the current game state and shoe information

This allows the application to restore the current game state if the application is restarted.

---

## Cloud Integration

Supabase is used for cloud functionality including:

- User authentication
- Uploading structured gameplay metrics

Each completed hand generates metric events including:

- Balance
- Bet size
- Running count
- True count
- Hand outcome

If the device is offline, uploads are stored locally and retried using WorkManager.

---

## Architecture

The application follows the recommended Android MVVM architecture.

### UI Layer
Implemented using Jetpack Compose.  
Screens observe the ViewModel state and send user interaction events.

Example screens include:

- LoginScreen
- HomeScreen
- GameModeScreen
- GameScreen
- DashboardScreen
- AccountScreen

### ViewModel Layer
GameViewModel acts as the single source of truth for the UI.

Responsibilities include:

- Handling gameplay logic
- Updating UI state
- Managing player actions
- Triggering persistence and cloud uploads

### Data Layer
The data layer contains repositories and database components.

Responsibilities include:

- Room database access
- Supabase communication
- Background upload management

---

## Technologies Used

Kotlin  
Jetpack Compose  
MVVM Architecture  
Room Database  
Kotlin Coroutines  
StateFlow  
WorkManager  
Supabase

---

## Project Folder Structure

MobileApplicationDevelopmentProject  
│  
├── app  
│   ├── core  
│   │   └── game  
│   │       ├── Card.kt  
│   │       ├── Shoe.kt  
│   │       └── HandLogic.kt  
│   │  
│   ├── data  
│   │   ├── local  
│   │   │   ├── dao  
│   │   │   ├── db  
│   │   │   └── entities  
│   │   │  
│   │   ├── remote  
│   │   │   ├── mapper  
│   │   │   └── SupabaseProvider.kt  
│   │   │  
│   │   └── repo  
│   │  
│   ├── ui  
│   │   ├── components  
│   │   ├── navigation  
│   │   ├── screens  
│   │   │   ├── auth  
│   │   │   ├── dashboard  
│   │   │   ├── game  
│   │   │   └── home  
│   │   │  
│   │   └── viewmodel  
│   │  
│   └── work  
│       └── MetricUploadWorker.kt  
│  
└── build.gradle

---

## Running the Application

1. Clone the repository

git clone https://github.com/your-repository/mobileapplicationdevelopmentproject.git

2. Open the project in Android Studio

3. Sync Gradle

4. Run the application on an emulator or Android device

---

## APK

A runnable APK is included in the project submission.

To install manually:

adb install blackjack_trainer.apk

---

## Generative AI Usage

Generative AI tools were used during development as development assistance tools.

Tools used include:

- ChatGPT
- Google Gemini
- Claude

These tools were used primarily for debugging assistance, architectural discussions, and reviewing implementation approaches.

All AI-generated suggestions were reviewed and tested before being incorporated into the project. Comments have been added within the source code where AI assistance significantly influenced implementation decisions.

---

## Authors

Team 4

Hugh Murtagh  
Peter Kennedy  
Matthew Burke

---

## Academic Context

This project was developed for the Mobile Application Development module. The objective of the assignment was to design and implement a non-trivial Android application demonstrating navigation, persistence, reactive UI, background processing, and cloud data integration.
