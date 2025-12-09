package com.example.pennywise.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pennywise.ui.MainViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val expenses by viewModel.allExpenses.collectAsState()
    val totalSpent by viewModel.totalSpentThisMonth.collectAsState()

    val deepForestBlack = Color(0xFF050A05)
    val metallicGold = Color(0xFFD4AF37)
    val brightGold = Color(0xFFFFD700)

    val categoryTotals = expenses.groupBy { it.category }
        .mapValues { entry -> entry.value.sumOf { it.amount } }
        .toList()
        .sortedByDescending { it.second }

    val chartColors = listOf(
        Color(0xFFFFD700), // Gold
        Color(0xFF00C853), // Emerald Green
        Color(0xFFE0E0E0), // Off-White/Silver
        Color(0xFFFF5722), // Bronze/Orange
        Color(0xFF00BCD4), // Teal
        Color(0xFF9C27B0)  // Purple
    )

    Scaffold(
        containerColor = deepForestBlack,
        topBar = {
            TopAppBar(
                title = { Text("Analysis", color = Color.White, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = metallicGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = deepForestBlack)
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if ((totalSpent ?: 0.0) > 0) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
                    Canvas(modifier = Modifier.size(180.dp)) {
                        var startAngle = -90f
                        val total = categoryTotals.sumOf { it.second }
                        categoryTotals.forEachIndexed { index, entry ->
                            val sweepAngle = (entry.second.toFloat() / total.toFloat()) * 360f
                            val color = chartColors.getOrElse(index) { Color.Gray }
                            drawArc(color, startAngle, sweepAngle, false, style = Stroke(40f))
                            startAngle += sweepAngle
                        }
                    }
                    Text(
                        text = "Total\nRs. ${String.format(Locale.getDefault(), "%.0f", totalSpent)}",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                Text("No data yet", color = Color.Gray)
            }

            Spacer(modifier = Modifier.height(32.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                items(categoryTotals) { (category, amount) ->
                    val index = categoryTotals.indexOfFirst { it.first == category }
                    val color = chartColors.getOrElse(index) { Color.Gray }
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(12.dp).background(color, CircleShape))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(category, color = Color.White, modifier = Modifier.weight(1f))
                        Text("Rs. ${String.format(Locale.getDefault(), "%.2f", amount)}", color = brightGold, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}