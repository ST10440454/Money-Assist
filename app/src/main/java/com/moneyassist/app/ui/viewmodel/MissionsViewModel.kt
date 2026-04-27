package com.moneyassist.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moneyassist.app.data.entity.BudgetCategory
import com.moneyassist.app.data.entity.Mission
import com.moneyassist.app.data.repository.AppRepository
import kotlinx.coroutines.launch

class MissionsViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AppRepository(app)

    val missions = repo.getAllMissions()
    val budgetCategories = repo.getAllBudgetCategories()

    fun addMission(mission: Mission) {
        viewModelScope.launch { repo.insertMission(mission) }
    }

    fun deleteMission(mission: Mission) {
        viewModelScope.launch { repo.deleteMission(mission) }
    }

    fun updateBudgetCategory(bc: BudgetCategory) {
        viewModelScope.launch { repo.updateBudgetCategory(bc) }
    }
}
