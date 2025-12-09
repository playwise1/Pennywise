package com.example.pennywise.ui

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pennywise.ui.screens.DashboardScreen
import com.example.pennywise.ui.screens.PermissionScreen
import com.example.pennywise.ui.screens.StatsScreen

sealed class Screen(val route: String) {
    object Permission : Screen("permission")
    object Dashboard : Screen("dashboard")
    object Stats : Screen("stats")
}

@Composable
fun AppNavigation(
    viewModel: MainViewModel,
    hasPermissions: Boolean
) {
    val navController = rememberNavController()
    val startDestination = if (hasPermissions) Screen.Dashboard.route else Screen.Permission.route

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Permission.route) {
            PermissionScreen(
                onPermissionGranted = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Permission.route) { inclusive = true }
                    }
                }
            )
        }

        // --- THIS IS THE FIX ---
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToStats = {
                    navController.navigate(Screen.Stats.route)
                }
            )
        }
        // -----------------------

        composable(Screen.Stats.route) {
            StatsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
