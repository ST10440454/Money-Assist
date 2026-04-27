package com.moneyassist.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.moneyassist.app.data.entity.ExpenseEntry
import com.moneyassist.app.data.model.CategorySpending

@Dao
interface ExpenseEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: ExpenseEntry): Long

    @Update
    suspend fun updateEntry(entry: ExpenseEntry)

    @Delete
    suspend fun deleteEntry(entry: ExpenseEntry)

    /** All entries within a date range, newest first. */
    @Query("""
        SELECT * FROM expense_entries
        WHERE date BETWEEN :startDate AND :endDate
        ORDER BY date DESC, startTime DESC
    """)
    fun getEntriesBetween(startDate: String, endDate: String): LiveData<List<ExpenseEntry>>

    /** Total spent per category within a date range. */
    @Query("""
        SELECT e.categoryId, c.name AS categoryName, SUM(e.amount) AS totalAmount
        FROM expense_entries e
        INNER JOIN categories c ON e.categoryId = c.id
        WHERE e.date BETWEEN :startDate AND :endDate
        GROUP BY e.categoryId
        ORDER BY totalAmount DESC
    """)
    fun getCategorySpendingBetween(startDate: String, endDate: String): LiveData<List<CategorySpending>>

    @Query("SELECT * FROM expense_entries WHERE id = :id LIMIT 1")
    suspend fun getEntryById(id: Int): ExpenseEntry?
}
