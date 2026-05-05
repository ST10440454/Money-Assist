package com.moneyassist.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.moneyassist.app.data.entity.BudgetCategory

/**
 * Data Access Object for budget categories and their limits.
 */
@Dao
interface BudgetCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bc: BudgetCategory): Long

    @Update
    suspend fun update(bc: BudgetCategory)

    @Delete
    suspend fun delete(bc: BudgetCategory)

    /** Retrieves all budget categories sorted by name. */
    @Query("SELECT * FROM budget_categories ORDER BY name ASC")
    fun getAll(): LiveData<List<BudgetCategory>>
}
