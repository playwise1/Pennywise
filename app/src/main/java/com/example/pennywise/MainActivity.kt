package com.example.pennywise

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.pennywise.ui.AppNavigation
import com.example.pennywise.ui.MainViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Check if the permission is currently granted
        // IMPORTANT: Replace 'Manifest.permission.READ_SMS' with the actual permission you use in PermissionScreen
        val permissionStatus = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_SMS
        )
        val hasPermissions = permissionStatus == PackageManager.PERMISSION_GRANTED

        setContent {
            // 2. Pass the result (hasPermissions) to your navigation
            AppNavigation(
                viewModel = viewModel,
                hasPermissions = hasPermissions
            )
        }
    }
}
