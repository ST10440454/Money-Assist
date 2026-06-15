package com.moneyassist.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.moneyassist.app.data.entity.ExpenseEntry
import com.moneyassist.app.data.model.CategorySpending

@Dao
interface ExpenseEntryDao {

    @Query("SELECT * FROM expense_entries ORDER BY date DESC, id DESC")
    fun getAllEntries(): LiveData<List<ExpenseEntry>>

    @Query("SELECT * FROM expense_entries ORDER BY date DESC, id DESC LIMIT 5")
    fun getRecentEntries(): LiveData<List<ExpenseEntry>>

    @Query("SELECT * FROM expense_entries WHERE isSynced = 0")
    suspend fun getUnsyncedEntries(): List<ExpenseEntry>

    @Query("UPDATE expense_entries SET isSynced = 1 WHERE id = :id")
    suspend fun markSynced(id: Int)

    @Query("SELECT * FROM expense_entries WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): ExpenseEntry?

    @Query("SELECT * FROM expense_entries WHERE date BETWEEN :start AND :end ORDER BY date DESC, id DESC")
    fun getEntriesBetween(start: String, end: String): LiveData<List<ExpenseEntry>>

    @Query("""
        SELECT 
            c.id as categoryId, 
            c.name as categoryName, 
            COALESCE(SUM(e.amount), 0.0) as totalAmount 
        FROM categories c 
        LEFT JOIN expense_entries e ON c.id = e.categoryId 
            AND e.date BETWEEN :start AND :end 
            AND e.isIncome = 0 
        GROUP BY c.id
        HAVING totalAmount > 0
    """)
    fun getCategorySpendingBetween(start: String, end: String): LiveData<List<CategorySpending>>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM expense_entries WHERE isIncome = 0")
    fun getTotalExpenses(): LiveData<Double>

    @Query("SELECT COALESCE(SUM(amount), 0) FROM expense_entries WHERE isIncome = 1")
    fun getTotalIncome(): LiveData<Double>

    @Query("""
        SELECT COALESCE(SUM(amount), 0) FROM expense_entries
        WHERE categoryId = :categoryId AND isIncome = 0
        AND date >= :fromDate
    """)
    suspend fun getSpendForCategory(categoryId: Int, fromDate: String): Double

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: ExpenseEntry): Long

    @Update
    suspend fun updateEntry(entry: ExpenseEntry)

    @Delete
    suspend fun deleteEntry(entry: ExpenseEntry)

    @Query("DELETE FROM expense_entries WHERE id = :id")
    suspend fun deleteById(id: Int)
}
