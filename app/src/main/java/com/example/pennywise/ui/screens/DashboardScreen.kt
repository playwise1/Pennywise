package com.example.pennywise.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    val currentMonthName by viewModel.currentMonthName.collectAsState()
    val expenseLabel by viewModel.expenseLabel.collectAsState()
    val canGoNext by viewModel.canGoNext.collectAsState()
    val isCurrentMonth by viewModel.isCurrentMonth.collectAsState()

    var showDialog by remember { mutableStateOf(false) }
    var expenseToDelete by remember { mutableStateOf<Expense?>(null) }
    var expenseToEdit by remember { mutableStateOf<Expense?>(null) }
    var showAboutDialog by remember { mutableStateOf(false) }

    val deepForestBlack = Color(0xFF050A05)
    val darkGreen = Color(0xFF1B5E20)
    val metallicGold = Color(0xFFD4AF37)
    val darkOlive = Color(0xFF1B2615)

    Scaffold(
        containerColor = deepForestBlack,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        text = "PennyWise",
                        color = Color(0xFFD4AF37),
                        fontFamily = FontFamily.Serif,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 22.sp,
                        letterSpacing = 1.sp
                    )
                 },
                navigationIcon = {
                    IconButton(onClick = { showAboutDialog = true }) {
                        Icon(
                            imageVector = Icons.Outlined.Info,
                            contentDescription = "About",
                            tint = metallicGold
                        )
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToStats) {
                        Icon(
                            imageVector = Icons.Default.Analytics,
                            contentDescription = "Analytics",
                            tint = metallicGold
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = deepForestBlack)
            )
        },
        floatingActionButton = {
            if (isCurrentMonth) {
                FloatingActionButton(
                    onClick = { showDialog = true },
                    containerColor = metallicGold,
                    contentColor = Color.Black
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Expense")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // About Dialog
            if (showAboutDialog) {
                AlertDialog(
                    onDismissRequest = { showAboutDialog = false },
                    containerColor = darkOlive,
                    title = { Text("PennyWise v1.0", color = Color.White) },
                    text = {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text("Developed by Team JAS™", fontWeight = FontWeight.Bold, color = metallicGold)
                            Text(
                                text = "Juweria Ashfaq Hussain, Sakina Khan\nSyed Ali Hussain",
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center,
                                color = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Structure Your Finance with Power and Precision",
                                fontStyle = FontStyle.Italic,
                                fontWeight = FontWeight.Medium,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = Color.White.copy(alpha = 0.2f))
                            Text(
                                text = "Automated SMS Expense Tracker built for precision.", 
                                color = Color.White,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "This app operates 100% locally. Your financial data is encrypted on your device and is never uploaded to any cloud server.",
                                fontStyle = FontStyle.Italic,
                                color = Color.Gray,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                            Text("Built with Jetpack Compose & Room.", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("Copyright © 2025 Team JAS. All Rights Reserved.", color = Color.Gray, fontSize = 10.sp)
                        }
                    },
                    confirmButton = {
                        TextButton(onClick = { showAboutDialog = false }) {
                            Text("Close", color = metallicGold)
                        }
                    }
                )
            }

            // Add Expense Dialog
            if (showDialog) {
                AddExpenseDialog(
                    onDismiss = { showDialog = false },
                    onConfirm = { amount, merchant, category ->
                        viewModel.addManualExpense(amount, merchant, category)
                        showDialog = false
                    }
                )
            }

            // Edit Expense Dialog
            expenseToEdit?.let { expense ->
                EditExpenseDialog(
                    expense = expense,
                    onDismiss = { expenseToEdit = null },
                    onConfirm = {
                        viewModel.updateExpense(it)
                        expenseToEdit = null
                    }
                )
            }

            // Delete Confirmation Dialog
            expenseToDelete?.let { expense ->
                val isAtmWithdrawal = expense.merchant == "ATM Withdrawal"
                AlertDialog(
                    onDismissRequest = { expenseToDelete = null },
                    containerColor = darkOlive,
                    title = { Text(if (isAtmWithdrawal) "Delete ATM Record?" else "Delete Transaction?", color = Color.White) },
                    text = { Text(if (isAtmWithdrawal) "This is a verified bank transaction. Are you sure you want to delete it?" else "Are you sure you want to remove this transaction?", color = Color.White.copy(alpha = 0.8f)) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.deleteExpense(expense)
                                expenseToDelete = null
                            }
                        ) {
                            Text("Delete", color = Color.Red)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { expenseToDelete = null }) {
                            Text("Cancel", color = metallicGold)
                        }
                    }
                )
            }

            // Main Content
            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = darkGreen)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp, bottom = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
                    ) {
                        IconButton(onClick = { viewModel.previousMonth() }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month", tint = Color.White)
                        }
                        Text(
                            text = currentMonthName.uppercase(),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        IconButton(onClick = { viewModel.nextMonth() }, enabled = canGoNext) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward, 
                                contentDescription = "Next Month", 
                                tint = if (canGoNext) metallicGold else Color.Gray.copy(alpha = 0.5f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = expenseLabel,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    val formattedTotal = String.format(Locale.getDefault(), "%.2f", totalSpent ?: 0.0)
                    Text(
                        text = "Rs. $formattedTotal",
                        color = metallicGold,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text("Recent Transactions", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(expenses) { expense ->
                    ExpenseItem(
                        expense = expense,
                        onEditClick = { expenseToEdit = expense },
                        onDeleteClick = { expenseToDelete = expense }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ExpenseItem(expense: Expense, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    val categoryIcon = when (expense.category) {
        "Food" -> Icons.Default.Fastfood
        "Transport" -> Icons.Default.DirectionsCar
        "Shopping" -> Icons.Default.ShoppingCart
        "Cash" -> Icons.Default.LocalAtm
        else -> Icons.AutoMirrored.Filled.ReceiptLong
    }

    val formattedDate = SimpleDateFormat("EEE MMM dd yyyy, h:mm a", Locale.getDefault()).format(Date(expense.timestamp))
    val cardBackground = Color(0xFF0F140F)
    val brightGold = Color(0xFFFFD700)
    val metallicGold = Color(0xFFD4AF37)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = { 
                    if (expense.merchant != "ATM Withdrawal") {
                        onEditClick()
                    }
                 },
                onLongClick = {
                    onDeleteClick()
                }
            ),
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
            
            if (expense.merchant != "ATM Withdrawal") {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = metallicGold)
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color.Gray)
                }
            }
        }
    }
}
