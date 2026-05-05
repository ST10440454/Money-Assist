package com.moneyassist.app.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a user in the system for authentication.
 */
@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val password: String  //  store hashed passwords
)
