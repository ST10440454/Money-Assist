package com.moneyassist.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a financial transaction (income or expense).
 */
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val date: String,           // Format: yyyy-MM-dd
    val amount: Double,
    val type: String,           // Transaction type: "income" or "expense"
    val category: String,
    val icon: String = "💳"
)
