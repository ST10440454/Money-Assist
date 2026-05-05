package com.moneyassist.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a detailed expense entry, potentially linked to a category and containing time info.
 */
@Entity(
    tableName = "expense_entries",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("categoryId")]
)
data class ExpenseEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,           // Format: yyyy-MM-dd
    val startTime: String,      // Format: HH:mm
    val endTime: String,        // Format: HH:mm
    val description: String,
    val amount: Double,
    val categoryId: Int,
    val photoPath: String? = null   // Optional path to an image of the receipt
)
