package com.moneyassist.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moneyassist.app.data.entity.Transaction
import com.moneyassist.app.data.repository.AppRepository
import kotlinx.coroutines.launch

class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AppRepository(app)

    val recentTransactions = repo.getRecentTransactions(5)
    val upcomingBills = repo.getUpcomingBills()
    val missions = repo.getAllMissions()
    val netWorth = repo.getNetWorth()

    fun addTransaction(tx: Transaction) {
        viewModelScope.launch { repo.insertTransaction(tx) }
    }
}
