package com.example.pennywise.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.pennywise.data.Expense
import com.example.pennywise.ui.MainViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: MainViewModel,
    onNavigateToStats: () -> Unit
) {
    val totalSpent by viewModel.totalSpentThisMonth.collectAsState()
    val expenses by viewModel.allExpenses.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }

    val deepForestBlack = Color(0xFF050A05)
    val darkGreen = Color(0xFF1B5E20)
    val metallicGold = Color(0xFFD4AF37)
    val brightGold = Color(0xFFFFD700)
    val cardBackground = Color(0xFF0F140F)

    Scaffold(
        containerColor = deepForestBlack,
        topBar = {
            TopAppBar(
                title = { Text("Pennywise", color = Color.White, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToStats) {
                        Icon(Icons.Default.Analytics, contentDescription = "Analysis", tint = metallicGold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = deepForestBlack)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = metallicGold,
                contentColor = Color.Black
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Expense")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // Dialogs
            if (showDialog) {
                AddExpenseDialog(
                    onDismiss = { showDialog = false },
                    onConfirm = { amount, merchant, category ->
                        viewModel.addManualExpense(amount, merchant, category)
                        showDialog = false
                    }
                )
            }

            expenseToDelete?.let { expense ->
                AlertDialog(
                    onDismissRequest = { expenseToDelete = null },
                    title = { Text("Delete Transaction?") },
                    text = { Text("Are you sure you want to remove this transaction?") },
                    confirmButton = {
                        Button(
                            onClick = {
                                viewModel.deleteExpense(expense)
                                expenseToDelete = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                        ) {
                            Text("Delete")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { expenseToDelete = null }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Total Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = darkGreen)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Total Spent This Month", color = Color.White.copy(alpha = 0.8f))
                    val formattedTotal = String.format(Locale.getDefault(), "%.2f", totalSpent ?: 0.0)
                    Text(text = "Rs. $formattedTotal", color = brightGold, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Recent Transactions", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(16.dp))

            // List
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(expenses) { expense ->
                    ExpenseItem(
                        expense = expense,
                        onDeleteClick = { expenseToDelete = expense } // Explicitly using 'expense'
                    )
                }
            }
        }
    }
}

@Composable
fun ExpenseItem(expense: Expense, onDeleteClick: () -> Unit) {
    val categoryIcon = when (expense.category) {
        "Food" -> Icons.Default.Fastfood
        "Transport" -> Icons.Default.DirectionsCar
        "Shopping" -> Icons.Default.ShoppingCart
        else -> Icons.AutoMirrored.Filled.ReceiptLong
    }

    val formattedDate = SimpleDateFormat("EEE MMM dd yyyy, h:mm a", Locale.getDefault()).format(Date(expense.timestamp))
    val cardBackground = Color(0xFF0F140F)
    val brightGold = Color(0xFFFFD700)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = cardBackground)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(categoryIcon, contentDescription = null, modifier = Modifier.size(40.dp), tint = Color.White)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(expense.merchant, fontWeight = FontWeight.Bold, color = Color.White)
                Text(formattedDate, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
            }
            Text("Rs. ${String.format(Locale.getDefault(), "%.2f", expense.amount)}", fontWeight = FontWeight.Bold, color = brightGold)
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
            }
        }
    }
}
