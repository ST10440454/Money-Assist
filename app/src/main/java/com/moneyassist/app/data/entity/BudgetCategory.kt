package com.moneyassist.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a spending category with a set budget limit.
 */
@Entity(tableName = "budget_categories")
data class BudgetCategory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val limitAmount: Double,
    val spent: Double = 0.0,
    val icon: String = "💰"
)
