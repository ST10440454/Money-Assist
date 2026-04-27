package com.moneyassist.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val date: String,           // yyyy-MM-dd
    val amount: Double,
    val type: String,           // "income" or "expense"
    val category: String,
    val icon: String = "💳"
)
