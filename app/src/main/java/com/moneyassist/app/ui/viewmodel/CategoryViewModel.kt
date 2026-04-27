package com.moneyassist.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moneyassist.app.data.entity.Category
import com.moneyassist.app.data.repository.AppRepository
import kotlinx.coroutines.launch

class CategoryViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AppRepository(app)

    val categories = repo.getAllCategories()

    fun addCategory(name: String, minGoal: Double, maxGoal: Double) {
        if (name.isBlank()) return
        viewModelScope.launch {
            repo.insertCategory(Category(name = name.trim(), minimumGoal = minGoal, maximumGoal = maxGoal))
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch { repo.updateCategory(category) }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch { repo.deleteCategory(category) }
    }

    /** Suspend lookup used by PhotoViewerFragment to resolve a category name. */
    suspend fun getCategoryById(id: Int): Category? = repo.getCategoryById(id)
}
