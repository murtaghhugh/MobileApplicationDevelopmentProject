# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Blackjack Card Counting Trainer — an Android app built with Kotlin + Jetpack Compose that simulates casino blackjack while teaching Hi-Lo and Omega II card counting systems.

## Build Commands
```bash
# Build
./gradlew assembleDebug
./gradlew assembleRelease

# Install to connected device/emulator
./gradlew installDebug

# Tests
./gradlew test
./gradlew connectedAndroidTest

# Lint
./gradlew lint

# Clean
./gradlew clean
```

On Windows use `gradlew.bat` instead of `./gradlew`.

## Architecture

**MVVM** with a single `:app` module:
```
Compose UI Screens
    → ViewModels (StateFlow<UiState>)
    → Repositories
    → Room (local) / Supabase (remote)
```

### Key layers

- **`core/game/`** — pure game logic: `Card`, `Shoe`, `Rank`, `Suit`, hand-total calculation. No Android dependencies.
- **`ui/viewmodel/GameViewModel.kt`** — central state machine; dealing, betting, split/double-down, shoe persistence, metrics export.
- **`data/local/`** — Room database (v6): `hands` table and `shoe_state` table. KSP generates DAOs.
- **`data/remote/`** — Supabase: `AuthRepository` for login/signup, `MetricsRepository` for per-hand metric uploads.
- **`data/repo/HandRepository.kt`** — bridges local Room and remote Supabase.
- **`navigation/NavGraph.kt`** — Compose Navigation. Start destination: `LOGIN`.

### Navigation flow
```
LOGIN → (optional SIGNUP)
    → HOME
        ├── GAME_MODE → GAME/{mode}
        ├── DASHBOARD → SHOE_DETAIL/{sessionId}
        ├── INFO
        └── ACCOUNT
```

### Game modes

| Mode         | Decks | Counting System |
|--------------|-------|-----------------|
| BEGINNER     | 1     | Hi-Lo           |
| INTERMEDIATE | 6     | Hi-Lo           |
| ADVANCED     | 8     | Omega II        |

### Supabase / secrets

`SUPABASE_URL` and `SUPABASE_KEY` live in `local.properties` (not committed). Injected as `BuildConfig` fields. Never hard-code credentials.

## Dependencies & Versions

- AGP `8.7.3`, Kotlin `2.1.0`, KSP `2.1.0-1.0.29`
- Compose BOM `2024.11.00`, Navigation Compose `2.8.4`
- Room `2.6.1` (KSP)
- Supabase BOM `3.0.2` (`auth-kt`, `postgrest-kt`)
- Ktor OkHttp `3.0.1`
- Coroutines `1.8.1`
- WorkManager `2.9.0` (already in build.gradle.kts, unused — to be implemented)

## Room Database Notes

- `BlackjackDatabase` version 6.
- `ShoeStateEntity` keyed by `mode` — one row per game mode for session resumption.
- Add migrations when changing entities. `fallbackToDestructiveMigration()` is active during development only.

## AI Usage Policy

This project declares AI tool usage per file with a comment at the top of each file. Format:
`// AI-assisted: <brief description>` or `// AI: not used in this file`
Always add this comment at the top of any file you create or significantly modify.

## Current Tasks (Matthew — do not touch teammate screens)

### Do NOT modify these files (teammate ownership):
- `ui/screens/info/InfoScreen.kt`
- `ui/screens/account/AccountScreen.kt`
- `ui/screens/dashboard/DashboardScreen.kt`
- `ui/screens/auth/` (LoginScreen, SignUpScreen)
- `ui/screens/home/HomeScreen.kt`
- Any UI theme or color files

### Task 1 — WorkManager background upload worker

Create `data/worker/MetricUploadWorker.kt`:
- A `CoroutineWorker` that reads any pending (failed) metric uploads from a local queue and retries them via `MetricsRepository`
- Use `OneTimeWorkRequest` triggered after each failed upload attempt in `MetricsRepository`
- Use `Constraints` requiring network connectivity (`NetworkType.CONNECTED`)
- Add `// AI-assisted: WorkManager scaffolding and constraint configuration` at top of new files

Wire it up in:
- `MadProjectApp.kt` — initialise WorkManager
- `MetricsRepository.kt` — enqueue a retry request on upload failure
- `AndroidManifest.xml` — declare the worker if required

### Task 2 — Offline upload queue for Supabase metrics

When a metric upload fails in `MetricsRepository`, instead of silently dropping it after 2 retries:
- Persist the failed `List<MetricEvent>` to a local Room table (`pending_uploads`)
- On next successful network connection (via WorkManager worker from Task 1), drain the queue and retry
- Add a new entity `PendingUploadEntity` and DAO `PendingUploadDao` to the existing `BlackjackDatabase`
- Increment database version and add a migration

Do not change game logic, ViewModel state, or any UI files.