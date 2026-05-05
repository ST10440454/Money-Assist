package com.moneyassist.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moneyassist.app.data.entity.Transaction
import com.moneyassist.app.data.repository.AppRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for the Home screen, providing a summary of transactions, bills, and missions.
 */
class HomeViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AppRepository(app)

    // Data streams for the home summary
    val recentTransactions = repo.getRecentTransactions(5)
    val upcomingBills = repo.getUpcomingBills()
    val missions = repo.getAllMissions()
    val netWorth = repo.getNetWorth()

    /**
     * Records a new transaction (e.g., income or expense).
     */
    fun addTransaction(tx: Transaction) {
        viewModelScope.launch { repo.insertTransaction(tx) }
    }
}
