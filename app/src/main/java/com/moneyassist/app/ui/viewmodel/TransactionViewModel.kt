package com.moneyassist.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import com.moneyassist.app.data.entity.Transaction
import com.moneyassist.app.data.repository.AppRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * ViewModel for viewing and managing transactions (income/expense) with date filtering.
 */
class TransactionViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AppRepository(app)
    private val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd")

    // Current date range for filtering transactions. Defaults to the current month.
    private val _dateRange = MutableLiveData(
        Pair(
            LocalDate.now().withDayOfMonth(1).format(fmt),
            LocalDate.now().format(fmt)
        )
    )
    val dateRange: LiveData<Pair<String, String>> = _dateRange

    /** Updates the date range filter. */
    fun setDateRange(start: String, end: String) {
        _dateRange.value = Pair(start, end)
    }

    // List of transactions within the selected date range.
    val transactions: LiveData<List<Transaction>> = _dateRange.switchMap { (s, e) ->
        repo.getTransactionsBetween(s, e)
    }

    /** Adds a new transaction. */
    fun addTransaction(tx: Transaction) {
        viewModelScope.launch { repo.insertTransaction(tx) }
    }

    /** Deletes a transaction. */
    fun deleteTransaction(tx: Transaction) {
        viewModelScope.launch { repo.deleteTransaction(tx) }
    }
}
