package com.moneyassist.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "missions")
data class Mission(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val target: Double,
    val current: Double,
    val deadline: String,       // yyyy-MM-dd
    val type: String,           // "savings" or "debt"
    val icon: String = "🎯",
    val monthlyContrib: Double = 0.0
)
