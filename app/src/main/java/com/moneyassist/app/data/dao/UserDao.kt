package com.moneyassist.app.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.moneyassist.app.data.entity.User

/**
 * Data Access Object for user authentication and management.
 */
@Dao
interface UserDao {

    /** Inserts a new user. Aborts if the user already exists. */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long

    /** Attempts to find a user with the matching credentials. */
    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun login(username: String, password: String): User?

    /** Retrieves a user by their username. */
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?
}
