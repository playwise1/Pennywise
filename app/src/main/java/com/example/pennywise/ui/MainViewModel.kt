package com.example.pennywise.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.pennywise.data.AppDatabase
import com.example.pennywise.data.Expense
import com.example.pennywise.data.ExpenseRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: ExpenseRepository

    private val _monthOffset = MutableStateFlow(0)

    val currentMonthName: StateFlow<String>
    val allExpenses: StateFlow<List<Expense>>
    val totalSpentThisMonth: StateFlow<Double?>
    val expenseLabel: StateFlow<String>
    val canGoNext: StateFlow<Boolean>
    val isCurrentMonth: StateFlow<Boolean>

    init {
        val expenseDao = AppDatabase.getDatabase(application).expenseDao()
        repository = ExpenseRepository(expenseDao)

        currentMonthName = _monthOffset.map {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, it)
            SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(calendar.time)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

        allExpenses = _monthOffset.flatMapLatest {
            val (start, end) = getMonthStartAndEnd(it)
            repository.getExpensesByDate(start, end)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

        totalSpentThisMonth = _monthOffset.flatMapLatest {
            val (start, end) = getMonthStartAndEnd(it)
            repository.getTotalSpent(start, end)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0.0)

        expenseLabel = _monthOffset.map { offset ->
            if (offset == 0) "Total Spent This Month" else "Total Monthly Spent"
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "Total Spent This Month")

        canGoNext = _monthOffset.map { it < 0 }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

        isCurrentMonth = _monthOffset.map { it == 0 }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
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

    fun updateExpense(expense: Expense) {
        viewModelScope.launch {
            repository.updateExpense(expense)
        }
    }

    fun deleteExpense(expense: Expense) {
        viewModelScope.launch {
            repository.deleteExpense(expense)
        }
    }

    fun nextMonth() {
        if (_monthOffset.value < 0) {
            _monthOffset.value++
        }
    }

    fun previousMonth() {
        _monthOffset.value--
    }

    private fun getMonthStartAndEnd(offset: Int): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, offset)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfMonth = calendar.timeInMillis

        val endCalendar = calendar.clone() as Calendar
        endCalendar.add(Calendar.MONTH, 1)
        val endOfMonth = endCalendar.timeInMillis
        return Pair(startOfMonth, endOfMonth)
    }
}