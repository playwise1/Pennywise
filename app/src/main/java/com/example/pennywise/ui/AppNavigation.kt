package com.example.pennywise.ui

import android.Manifest
import android.content.pm.PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.pennywise.ui.screens.DashboardScreen
import com.example.pennywise.ui.screens.PermissionScreen
import com.example.pennywise.ui.screens.SplashScreen
import com.example.pennywise.ui.screens.StatsScreen

@Composable
fun AppNavigation(
    viewModel: MainViewModel,
    hasPermissions: Boolean
) {
    val navController = rememberNavController()
    val startDestination = Screen.Splash.route
    val context = LocalContext.current

    NavHost(navController = navController, startDestination = startDestination) {
        composable(Screen.Splash.route) {
            SplashScreen(onTimeout = {
                navController.popBackStack()
                val destination = if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECEIVE_SMS) == PackageManager.PERMISSION_GRANTED) {
                    Screen.Dashboard.route
                } else {
                    Screen.Permission.route
                }
                navController.navigate(destination)
            })
        }

        composable(Screen.Permission.route) {
            PermissionScreen(
                onPermissionGranted = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Permission.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToStats = {
                    navController.navigate(Screen.Stats.route)
                }
            )
        }

        composable(Screen.Stats.route) {
            StatsScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
