package com.moneyassist.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.moneyassist.app.data.entity.Transaction

/**
 * Data Access Object for financial transactions.
 */
@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tx: Transaction): Long

    @Update
    suspend fun update(tx: Transaction)

    @Delete
    suspend fun delete(tx: Transaction)

    /** Retrieves all transactions sorted by date descending. */
    @Query("SELECT * FROM transactions ORDER BY date DESC, id DESC")
    fun getAll(): LiveData<List<Transaction>>

    /** Retrieves transactions within a specific date range. */
    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getBetween(start: String, end: String): LiveData<List<Transaction>>

    /** Retrieves a limited number of recent transactions. */
    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit")
    fun getRecent(limit: Int = 5): LiveData<List<Transaction>>

    /** Calculates total net worth (Income - Expenses). */
    @Query("SELECT SUM(CASE WHEN type='income' THEN amount ELSE -amount END) FROM transactions")
    fun getNetWorth(): LiveData<Double?>
}
