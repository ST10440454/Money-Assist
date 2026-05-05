package com.moneyassist.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a budget category with minimum and maximum spending goals.
 */
@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val minimumGoal: Double = 0.0,
    val maximumGoal: Double = 0.0
)
