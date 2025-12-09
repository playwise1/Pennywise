package com.example.pennywise.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pennywise.data.AppDatabase
import com.example.pennywise.data.Expense
import com.example.pennywise.data.ExpenseRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExpenseRepository
    val allExpenses: StateFlow<List<Expense>>
    val totalSpentThisMonth: StateFlow<Double?>

    init {
        // Initialize the Database and Repository immediately
        val expenseDao = AppDatabase.getDatabase(application).expenseDao()
        repository = ExpenseRepository(expenseDao)

        // 1. Get All Expenses
        allExpenses = repository.allExpenses.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        // 2. Calculate "Current Month" Total
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis

        val endCalendar = calendar.clone() as Calendar
        endCalendar.add(Calendar.MONTH, 1)
        val endOfMonth = endCalendar.timeInMillis

        totalSpentThisMonth = repository.getTotalSpent(startOfMonth, endOfMonth)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                0.0
            )
    }

    fun addManualExpense(amount: Double, merchant: String, category: String) {
        viewModelScope.launch {
            val expense = Expense(
                amount = amount,
                merchant = merchant,
                category = category,
                timestamp = System.currentTimeMillis()
            )
            repository.addExpense(expense)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }
}