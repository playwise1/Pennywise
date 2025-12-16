package com.example.pennywise.ui

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Permission : Screen("permission")
    object Dashboard : Screen("dashboard")
    object Stats : Screen("stats")
}