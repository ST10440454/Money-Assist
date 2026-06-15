package com.moneyassist.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.moneyassist.app.data.entity.ExpenseEntry
import com.moneyassist.app.data.model.CategorySpending
import com.moneyassist.app.data.repository.AppRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * ViewModel for tracking detailed expenses and providing spending analysis by date range.
 */
class ExpenseViewModel(app: Application) : AndroidViewModel(app) {

    // BUG FIX: was `AppRepository(app)` which creates a second repository instance,
    // breaking the single-source-of-truth and causing LiveData observers on the Home
    // screen to not see writes made through this ViewModel.
    private val repo = AppRepository.getInstance(app)
    private val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    private val _dateRange = MutableLiveData(
        Pair(
            LocalDate.now().withDayOfMonth(1).format(fmt),
            LocalDate.now().format(fmt)
        )
    )
    val dateRange: LiveData<Pair<String, String>> = _dateRange

    fun setDateRange(start: String, end: String) {
        _dateRange.value = Pair(start, end)
    }

    val entries: LiveData<List<ExpenseEntry>> = _dateRange.switchMap { (s, e) ->
        repo.getEntriesBetween(s, e)
    }

    val categorySpending: LiveData<List<CategorySpending>> = _dateRange.switchMap { (s, e) ->
        repo.getCategorySpendingBetween(s, e)
    }

    fun addEntry(entry: ExpenseEntry) {
        viewModelScope.launch { repo.saveEntry(entry) }
    }

    fun updateEntry(entry: ExpenseEntry) {
        viewModelScope.launch { repo.updateEntry(entry) }
    }

    fun deleteEntry(entry: ExpenseEntry) {
        viewModelScope.launch { repo.deleteEntry(entry) }
    }

    suspend fun getEntryById(id: Int): ExpenseEntry? = repo.getEntryById(id)
}