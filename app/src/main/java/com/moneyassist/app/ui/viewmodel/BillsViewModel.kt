package com.moneyassist.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moneyassist.app.data.entity.Bill
import com.moneyassist.app.data.repository.AppRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * ViewModel for managing bills, including tracking paid/unpaid status and totals.
 */
class BillsViewModel(app: Application) : AndroidViewModel(app) {

    // BUG FIX: was `AppRepository(app)` which creates a second repository instance,
    // breaking the single-source-of-truth and causing LiveData observers on the Home
    // screen to not see writes made through this ViewModel.
    private val repo = AppRepository.getInstance(app)

    val upcomingBills = repo.getUpcomingBillsList()
    val paidBills = repo.getPaidBills()
    val totalUpcoming = repo.totalUpcoming

    fun markPaid(bill: Bill) {
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        viewModelScope.launch {
            repo.updateBill(bill.copy(isPaid = true, paidOn = today))
        }
    }

    fun addBill(bill: Bill) {
        viewModelScope.launch { repo.insertBill(bill) }
    }

    fun deleteBill(bill: Bill) {
        viewModelScope.launch { repo.deleteBill(bill) }
    }
}