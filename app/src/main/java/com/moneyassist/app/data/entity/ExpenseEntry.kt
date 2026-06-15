package com.moneyassist.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a detailed expense/income entry linked to a category.
 * [isSynced] is set to false on creation and flipped to true by [SyncWorker].
 */
@Entity(
    tableName = "expense_entries",
    foreignKeys = [
        ForeignKey(
            entity = Category::class,
            parentColumns = ["id"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_DEFAULT
        )
    ],
    indices = [Index("categoryId")]
)
data class ExpenseEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val date: String,
    val startTime: String,
    val endTime: String,
    val description: String,
    val amount: Double,
    val categoryId: Int = 1,
    val isIncome: Boolean = false,
    val notes: String? = null,
    val photoPath: String? = null,
    val isSynced: Boolean = false   // Managed by SyncWorker
)
