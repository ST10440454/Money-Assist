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

class ExpenseViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AppRepository(app)
    private val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // ── Date range filter ──────────────────────────────────────
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

    // ── Entries list ───────────────────────────────────────────
    val entries: LiveData<List<ExpenseEntry>> = _dateRange.switchMap { (s, e) ->
        repo.getEntriesBetween(s, e)
    }

    // ── Category spending summary ──────────────────────────────
    val categorySpending: LiveData<List<CategorySpending>> = _dateRange.switchMap { (s, e) ->
        repo.getCategorySpendingBetween(s, e)
    }

    // ── CRUD ───────────────────────────────────────────────────
    fun addEntry(entry: ExpenseEntry) {
        viewModelScope.launch { repo.insertEntry(entry) }
    }

    fun updateEntry(entry: ExpenseEntry) {
        viewModelScope.launch { repo.updateEntry(entry) }
    }

    fun deleteEntry(entry: ExpenseEntry) {
        viewModelScope.launch { repo.deleteEntry(entry) }
    }

    suspend fun getEntryById(id: Int): ExpenseEntry? = repo.getEntryById(id)
}
