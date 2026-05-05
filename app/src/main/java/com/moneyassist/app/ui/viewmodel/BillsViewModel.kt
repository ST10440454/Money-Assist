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

    private val repo = AppRepository(app)

    // Observable data streams for the UI
    val upcomingBills = repo.getUpcomingBills()
    val paidBills = repo.getPaidBills()
    val totalUpcoming = repo.getTotalUpcoming()

    /**
     * Marks a bill as paid and records the current date as the payment date.
     */
    fun markPaid(bill: Bill) {
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        viewModelScope.launch {
            repo.updateBill(bill.copy(isPaid = true, paidOn = today))
        }
    }

    /**
     * Adds a new bill to the database.
     */
    fun addBill(bill: Bill) {
        viewModelScope.launch { repo.insertBill(bill) }
    }

    /**
     * Deletes a bill from the database.
     */
    fun deleteBill(bill: Bill) {
        viewModelScope.launch { repo.deleteBill(bill) }
    }
}
