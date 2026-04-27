package com.moneyassist.app.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

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
    val date: String,           // Stored as "yyyy-MM-dd"
    val startTime: String,      // Stored as "HH:mm"
    val endTime: String,        // Stored as "HH:mm"
    val description: String,
    val amount: Double,
    val categoryId: Int,
    val photoPath: String? = null   // Nullable – photo is optional
)
