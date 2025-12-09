package com.example.pennywise.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val amount: Double,
    val merchant: String,
    val timestamp: Long, // Epoch millis
    val category: String = "Unknown",
    val rawMessage: String? = null
)
