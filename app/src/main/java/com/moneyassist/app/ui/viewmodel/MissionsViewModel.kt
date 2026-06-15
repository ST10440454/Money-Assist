package com.moneyassist.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.moneyassist.app.data.db.AppDatabase
import com.moneyassist.app.data.entity.Mission
import com.moneyassist.app.engine.PointsManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.temporal.ChronoUnit

class MissionsViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getInstance(application)
    val activeMissions: LiveData<List<Mission>> = db.missionDao().getActive()
    val completedMissions: LiveData<List<Mission>> = db.missionDao().getCompleted()

    private val _saveResult = MutableLiveData<String?>()
    val saveResult: LiveData<String?> = _saveResult

    fun createMission(
        name: String,
        targetAmount: Double,
        currentAmount: Double = 0.0,
        deadline: String = "",
        budgetMode: String = "flexible"
    ) {
        if (name.isBlank()) { _saveResult.value = "Please enter a mission name."; return }
        if (targetAmount <= 0) { _saveResult.value = "Target must be greater than zero."; return }

        val monthsRemaining = if (deadline.isNotBlank()) {
            try {
                val dueDate = LocalDate.parse(deadline)
                ChronoUnit.MONTHS.between(LocalDate.now(), dueDate).coerceAtLeast(1).toInt()
            } catch (e: Exception) { 12 }
        } else { 12 }

        val remaining = (targetAmount - currentAmount).coerceAtLeast(0.0)
        val contrib = remaining / monthsRemaining

        viewModelScope.launch(Dispatchers.IO) {
            db.missionDao().insertMission(
                Mission(
                    name = name,
                    targetAmount = targetAmount,
                    currentAmount = currentAmount,
                    deadline = deadline,
                    monthlyContrib = contrib,
                    budgetMode = budgetMode
                )
            )
            _saveResult.postValue("Mission \"$name\" created! 🎯")
        }
    }

    fun addProgress(mission: Mission, amount: Double) {
        viewModelScope.launch {
            val newAmount = (mission.currentAmount + amount).coerceAtMost(mission.targetAmount)
            val prevPercent = (mission.currentAmount / mission.targetAmount) * 100
            val newPercent  = (newAmount / mission.targetAmount) * 100

            val deadline = mission.deadline
            val monthsLeft = if (deadline.isNotBlank()) {
                try {
                    ChronoUnit.MONTHS.between(LocalDate.now(), LocalDate.parse(deadline)).coerceAtLeast(1).toInt()
                } catch (e: Exception) { 1 }
            } else { 1 }
            val newContrib = (mission.targetAmount - newAmount).coerceAtLeast(0.0) / monthsLeft

            // BUG FIX: original code called both `updateProgress(mission.id, newAmount)`
            // AND `update(mission.copy(...))`. These two writes raced against each other —
            // the second `update()` used a stale copy of `mission` which could overwrite
            // `currentAmount` back to its old value. Fixed by using a single `update()` call
            // that atomically writes both the new currentAmount and recalculated monthlyContrib.
            db.missionDao().update(mission.copy(currentAmount = newAmount, monthlyContrib = newContrib))

            val ctx = getApplication<Application>()
            if (prevPercent < 50 && newPercent >= 50) {
                PointsManager.onMissionHalfway(ctx)
                _saveResult.postValue("+50 pts! You're halfway to your mission! 🌟")
            }
            if (newAmount >= mission.targetAmount) {
                db.missionDao().markCompleted(mission.id)
                PointsManager.onMissionCompleted(ctx)
                _saveResult.postValue("+100 pts! Mission complete! 🎉")
            }
        }
    }

    fun calcContribution(targetAmount: Double, currentAmount: Double, deadline: String): String {
        val months = if (deadline.isNotBlank()) {
            try {
                ChronoUnit.MONTHS.between(LocalDate.now(), LocalDate.parse(deadline)).coerceAtLeast(1)
            } catch (e: Exception) { 12L }
        } else { 12L }
        val contrib = (targetAmount - currentAmount).coerceAtLeast(0.0) / months
        return "R${"%.2f".format(contrib)}/month over $months months"
    }

    fun deleteMission(mission: Mission) {
        viewModelScope.launch(Dispatchers.IO) { db.missionDao().delete(mission) }
    }

    fun clearResult() { _saveResult.value = null }
}