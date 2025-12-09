package com.example.pennywise.ui.theme // or com.example.pennywise.ui

sealed class Screen(val route: String) {
    object Permission : Screen("permission")
    object Dashboard : Screen("dashboard")
    object Stats : Screen("stats")
}