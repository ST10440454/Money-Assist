package com.moneyassist.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moneyassist.app.data.entity.BudgetCategory
import com.moneyassist.app.data.entity.Mission
import com.moneyassist.app.data.repository.AppRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for managing financial goals (missions) and budget category limits.
 */
class MissionsViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AppRepository(app)

    // Observable streams for goals and budget limits
    val missions = repo.getAllMissions()
    val budgetCategories = repo.getAllBudgetCategories()

    /** Adds a new financial mission. */
    fun addMission(mission: Mission) {
        viewModelScope.launch { repo.insertMission(mission) }
    }

    /** Deletes a financial mission. */
    fun deleteMission(mission: Mission) {
        viewModelScope.launch { repo.deleteMission(mission) }
    }

    /** Updates a budget category's information, such as its spending limit. */
    fun updateBudgetCategory(bc: BudgetCategory) {
        viewModelScope.launch { repo.updateBudgetCategory(bc) }
    }
}
