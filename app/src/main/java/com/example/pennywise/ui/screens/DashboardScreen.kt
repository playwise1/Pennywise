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

    val darkBackground = Color(0xFF1E1E1E)
    val cardBlue = Color(0xFF007BFF)
    val textWhite = Color.White

    Scaffold(
        containerColor = darkBackground,
        topBar = {
            TopAppBar(
                title = { Text("Pennywise", color = textWhite, fontWeight = FontWeight.Bold) },
                actions = {
                    IconButton(onClick = onNavigateToStats) {
                        Icon(Icons.Default.Analytics, contentDescription = "Analysis", tint = textWhite)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = darkBackground)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showDialog = true },
                containerColor = cardBlue,
                contentColor = Color.White
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
                colors = CardDefaults.cardColors(containerColor = cardBlue)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = "Total Spent This Month", color = textWhite.copy(alpha = 0.8f))
                    val formattedTotal = String.format(Locale.getDefault(), "%.2f", totalSpent ?: 0.0)
                    Text(text = "Rs. $formattedTotal", color = textWhite, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Recent Transactions", color = textWhite, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF2A2A2A))
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
            Text("Rs. ${String.format(Locale.getDefault(), "%.2f", expense.amount)}", fontWeight = FontWeight.Bold, color = Color.White)
            IconButton(onClick = onDeleteClick) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
            }
        }
    }
}