package com.moneyassist.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.moneyassist.app.data.entity.BudgetCategory

@Dao
interface BudgetCategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBudgetCategory(bc: BudgetCategory): Long

    @Update
    suspend fun update(bc: BudgetCategory)

    @Delete
    suspend fun delete(bc: BudgetCategory)

    @Query("SELECT * FROM budget_categories ORDER BY name ASC")
    fun getAllBudgetCategories(): LiveData<List<BudgetCategory>>

    @Query("UPDATE budget_categories SET spent = :spent WHERE id = :id")
    suspend fun updateSpent(id: Int, spent: Double)
}
