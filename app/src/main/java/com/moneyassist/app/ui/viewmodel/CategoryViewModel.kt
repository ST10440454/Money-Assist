package com.moneyassist.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moneyassist.app.data.entity.Category
import com.moneyassist.app.data.repository.AppRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for managing spending categories and their goals.
 */
class CategoryViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AppRepository(app)

    // Observable list of all categories
    val categories = repo.getAllCategories()

    /** Adds a new category with optional spending goals. */
    fun addCategory(name: String, minGoal: Double, maxGoal: Double) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repo.insertCategory(Category(name = name.trim(), minimumGoal = minGoal, maximumGoal = maxGoal))
        }
    }

    /** Updates an existing category. */
    fun updateCategory(category: Category) {
        viewModelScope.launch { repo.updateCategory(category) }
    }

    /** Deletes a category. */
    fun deleteCategory(category: Category) {
        viewModelScope.launch { repo.deleteCategory(category) }
    }

    /** Retrieves a specific category by its ID. */
    suspend fun getCategoryById(id: Int): Category? = repo.getCategoryById(id)
}
