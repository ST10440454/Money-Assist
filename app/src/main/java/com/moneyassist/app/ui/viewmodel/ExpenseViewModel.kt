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

    private val repo = AppRepository(app)
    private val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Date range filter for entries and reports. Default is current month to today.
    private val _dateRange = MutableLiveData(
        Pair(
            LocalDate.now().withDayOfMonth(1).format(fmt),
            LocalDate.now().format(fmt)
        )
    )
    val dateRange: LiveData<Pair<String, String>> = _dateRange

    /** Updates the current filter date range. */
    fun setDateRange(start: String, end: String) {
        _dateRange.value = Pair(start, end)
    }

    // List of expense entries filtered by the current date range.
    val entries: LiveData<List<ExpenseEntry>> = _dateRange.switchMap { (s, e) ->
        repo.getEntriesBetween(s, e)
    }

    // Summary of spending per category for the current date range.
    val categorySpending: LiveData<List<CategorySpending>> = _dateRange.switchMap { (s, e) ->
        repo.getCategorySpendingBetween(s, e)
    }

    /** Adds a new expense entry. */
    fun addEntry(entry: ExpenseEntry) {
        viewModelScope.launch { repo.insertEntry(entry) }
    }

    /** Updates an existing expense entry. */
    fun updateEntry(entry: ExpenseEntry) {
        viewModelScope.launch { repo.updateEntry(entry) }
    }

    /** Deletes an expense entry. */
    fun deleteEntry(entry: ExpenseEntry) {
        viewModelScope.launch { repo.deleteEntry(entry) }
    }

    /** Fetches a specific entry by its ID. */
    suspend fun getEntryById(id: Int): ExpenseEntry? = repo.getEntryById(id)
}
