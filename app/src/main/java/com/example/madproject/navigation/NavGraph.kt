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
import com.example.madproject.ui.screens.dashboard.ShoeDetailScreen
import com.example.madproject.ui.screens.game.GameModeScreen
import com.example.madproject.ui.screens.game.GameScreen
import com.example.madproject.ui.screens.home.HomeScreen
import com.example.madproject.ui.screens.info.InfoScreen
import com.example.madproject.ui.viewmodel.GameViewModel

@Composable
fun AppNavGraph(
    navController: NavHostController,
    gameViewModel: GameViewModel,
    startDestination: String = Routes.LOGIN
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    // Prevent going back to Login after login
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onGoToRegister = { navController.navigate(Routes.SIGNUP) }
            )
        }

        composable(Routes.SIGNUP) {
            SignUpScreen(
                onRegisterSuccess = {
                    navController.navigate(Routes.HOME) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                        launchSingleTop = true
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.HOME) {
            HomeScreen(
                onDashboard = { navController.navigate(Routes.DASHBOARD) },
                onPlay = { navController.navigate(Routes.GAME_MODE) },
                onTips = { navController.navigate(Routes.INFO) },
                onAccount = { navController.navigate(Routes.ACCOUNT) }
            )
        }

        composable(Routes.DASHBOARD) {
            DashboardScreen(
                vm = gameViewModel,
                onBack = { navController.popBackStack() }
            )
        }

        composable(route = Routes.SHOE_DETAIL,
            arguments = listOf(
                navArgument(NavArgs.SESSION_ID) { type = NavType.LongType }
            )
        ) { backStackEntry ->
            val sessionId = backStackEntry.arguments?.getLong(NavArgs.SESSION_ID)
                ?: error("Missing sessionId")

            ShoeDetailScreen(
                sessionId = sessionId,
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
            AccountScreen(onBack = { navController.popBackStack() })
        }
    }
}