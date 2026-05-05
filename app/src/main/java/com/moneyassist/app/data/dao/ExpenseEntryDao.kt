package com.moneyassist.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.moneyassist.app.data.entity.ExpenseEntry
import com.moneyassist.app.data.model.CategorySpending

/**
 * Data Access Object for detailed expense entries and spending analysis.
 */
@Dao
interface ExpenseEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: ExpenseEntry): Long

    @Update
    suspend fun updateEntry(entry: ExpenseEntry)

    @Delete
    suspend fun deleteEntry(entry: ExpenseEntry)

    /** Retrieves all entries within a date range, newest first. */
    @Query("""
        SELECT * FROM expense_entries
        WHERE date BETWEEN :startDate AND :endDate
        ORDER BY date DESC, startTime DESC
    """)
    fun getEntriesBetween(startDate: String, endDate: String): LiveData<List<ExpenseEntry>>

    /** Calculates total spent per category within a specific date range. */
    @Query("""
        SELECT e.categoryId, c.name AS categoryName, SUM(e.amount) AS totalAmount
        FROM expense_entries e
        INNER JOIN categories c ON e.categoryId = c.id
        WHERE e.date BETWEEN :startDate AND :endDate
        GROUP BY e.categoryId
        ORDER BY totalAmount DESC
    """)
    fun getCategorySpendingBetween(startDate: String, endDate: String): LiveData<List<CategorySpending>>

    /** Finds a specific expense entry by its ID. */
    @Query("SELECT * FROM expense_entries WHERE id = :id LIMIT 1")
    suspend fun getEntryById(id: Int): ExpenseEntry?
}
