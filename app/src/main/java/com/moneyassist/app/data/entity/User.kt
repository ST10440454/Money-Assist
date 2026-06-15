package com.moneyassist.app.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Represents a registered user account.
 * passwordHash stores the SHA-256 salt:hash — never plain text.
 * Email is unique-indexed for fast lookup and uniqueness enforcement.
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val email: String,
    val passwordHash: String,  // format: "salt:sha256hash"
    val avatarIndex: Int = 0,
    val createdAt: String = java.time.LocalDate.now().toString()
)
