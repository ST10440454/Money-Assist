package com.moneyassist.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.moneyassist.app.data.entity.Transaction

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(tx: Transaction): Long

    @Update
    suspend fun update(tx: Transaction)

    @Delete
    suspend fun delete(tx: Transaction)

    @Query("SELECT * FROM transactions ORDER BY date DESC, id DESC")
    fun getAll(): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :start AND :end ORDER BY date DESC")
    fun getBetween(start: String, end: String): LiveData<List<Transaction>>

    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit")
    fun getRecent(limit: Int = 5): LiveData<List<Transaction>>

    @Query("SELECT SUM(CASE WHEN type='income' THEN amount ELSE -amount END) FROM transactions")
    fun getNetWorth(): LiveData<Double?>
}
