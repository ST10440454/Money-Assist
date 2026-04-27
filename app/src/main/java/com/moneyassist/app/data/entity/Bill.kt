package com.moneyassist.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bills")
data class Bill(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val dueDate: String,
    val amount: Double,
    val recurring: String = "Monthly",
    val isPaid: Boolean = false,
    val paidOn: String? = null,
    val icon: String = "📅",
    val isUrgent: Boolean = false
)
