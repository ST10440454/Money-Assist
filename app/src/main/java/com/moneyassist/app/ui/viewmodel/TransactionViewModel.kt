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

class TransactionViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AppRepository(app)
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

    val transactions: LiveData<List<Transaction>> = _dateRange.switchMap { (s, e) ->
        repo.getTransactionsBetween(s, e)
    }

    fun addTransaction(tx: Transaction) {
        viewModelScope.launch { repo.insertTransaction(tx) }
    }

    fun deleteTransaction(tx: Transaction) {
        viewModelScope.launch { repo.deleteTransaction(tx) }
    }
}
