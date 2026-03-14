package com.example.madproject.navigation

object Routes {
    const val LOGIN = "login"
    const val SIGNUP = "signup"
    const val HOME = "home"
    const val DASHBOARD = "dashboard"
    const val INFO = "info"
    const val ACCOUNT = "account"

    const val GAME_MODE = "game_mode"
    const val GAME = "game/{mode}"
    fun game(mode: String) = "game/$mode"
    const val RULES = "rules"
}

object NavArgs {
    const val MODE = "mode"
}