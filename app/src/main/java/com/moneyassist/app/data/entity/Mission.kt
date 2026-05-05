package com.moneyassist.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a financial goal or mission (e.g., "Save for a car", "Pay off loan").
 */
@Entity(tableName = "missions")
data class Mission(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val target: Double,
    val current: Double,
    val deadline: String,       // Format: yyyy-MM-dd
    val type: String,           // Mission type: "savings" or "debt"
    val icon: String = "🎯",
    val monthlyContrib: Double = 0.0
)
