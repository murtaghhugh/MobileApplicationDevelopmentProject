// AI-assisted: UI cleanup
// Initial guidance for structuring the Jetpack Compose Navigation graph
// (including parameter passing for game mode and session IDs) was obtained
// using ChatGPT. The navigation structure, routes, and screen integration
// were reviewed, modified, and implemented by the developer.

package com.example.madproject.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.NavHostController
import com.example.madproject.ui.screens.account.AccountScreen
import com.example.madproject.ui.screens.auth.LoginScreen
import com.example.madproject.ui.screens.auth.SignUpScreen
import com.example.madproject.ui.screens.dashboard.DashboardScreen
import com.example.madproject.ui.screens.game.GameModeScreen
import com.example.madproject.ui.screens.game.GameScreen
import com.example.madproject.ui.screens.home.HomeScreen
import com.example.madproject.ui.screens.info.InfoScreen
import com.example.madproject.ui.viewmodel.GameViewModel
import com.example.madproject.ui.viewmodel.AuthViewModel
import com.example.madproject.ui.screens.rules.RulesScreen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    gameViewModel: GameViewModel,
    authViewModel: AuthViewModel
) {
    NavHost(
        navController = navController,
        startDestination = Routes.LOGIN
//        startDestination = Routes.HOME
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onGoToRegister = {
                    navController.navigate(Routes.SIGNUP)
                }
            )
        }

        composable(Routes.SIGNUP) {
            SignUpScreen(
                authViewModel = authViewModel,
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                },
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onDashboard = { navController.navigate(Routes.DASHBOARD) },
                onPlay = { navController.navigate(Routes.GAME_MODE) },
                onTips = { navController.navigate(Routes.INFO) },
                onAccount = { navController.navigate(Routes.ACCOUNT) },
                onRules = { navController.navigate(Routes.RULES) }
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                gameViewModel = gameViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.GAME_MODE) {
            GameModeScreen(
                onSelectMode = { mode ->
                    gameViewModel.selectMode(mode)
                    navController.navigate(Routes.game(mode))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Routes.GAME,
            arguments = listOf(
                navArgument(NavArgs.MODE) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val mode = backStackEntry.arguments?.getString(NavArgs.MODE)
                ?: error("Missing mode")

            GameScreen(
                mode = mode,
                vm = gameViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.INFO) {
            InfoScreen(onBack = { navController.popBackStack() })
        }

        composable(Routes.ACCOUNT) {
            AccountScreen(
                onBack = { navController.popBackStack() },
                onSignedOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(Routes.RULES) {
            RulesScreen(onBack = { navController.popBackStack() })
        }
    }
}
