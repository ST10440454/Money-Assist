package com.moneyassist.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.moneyassist.app.data.repository.AppRepository
import com.moneyassist.app.util.PrefsManager

class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repo = AppRepository.getInstance(application)

    val recentEntries = repo.recentEntries
    val upcomingBills = repo.upcomingBills
    val activeMissions = repo.activeMissions
    val totalPoints = repo.totalPoints

    /** Net balance = total income - total expenses */
    val netBalance: LiveData<Double> = MediatorLiveData<Double>().apply {
        var income = 0.0
        var expenses = 0.0

        fun recalc() { value = income - expenses }

        addSource(repo.totalIncome)   { income = it ?: 0.0;   recalc() }
        addSource(repo.totalExpenses) { expenses = it ?: 0.0; recalc() }
    }

    val monthlyIncome: Double
        get() = PrefsManager.getMonthlyIncome(getApplication())

    val avatarIndex: Int
        get() = PrefsManager.getAvatarIndex(getApplication())
}
