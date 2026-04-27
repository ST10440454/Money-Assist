package com.moneyassist.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moneyassist.app.data.entity.Bill
import com.moneyassist.app.data.repository.AppRepository
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BillsViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = AppRepository(app)

    val upcomingBills = repo.getUpcomingBills()
    val paidBills = repo.getPaidBills()
    val totalUpcoming = repo.getTotalUpcoming()

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
