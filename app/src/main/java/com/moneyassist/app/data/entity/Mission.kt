package com.moneyassist.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a savings goal or mission.
 *
 * Formula (PRD §1.4):
 *   recommendedMonthlyContrib = (targetAmount - currentAmount) / monthsRemaining
 */
@Entity(tableName = "missions")
data class Mission(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val targetAmount: Double,
    val currentAmount: Double = 0.0,
    val deadline: String = "",          // yyyy-MM-dd; empty = open-ended
    val monthlyContrib: Double = 0.0,   // Cached recommended contribution
    val isCompleted: Boolean = false,
    val icon: String = "🎯",
    val budgetMode: String = "flexible" // "flexible" | "strict"
)
