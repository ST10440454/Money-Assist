package com.moneyassist.app.data.repository

import android.content.Context
import androidx.lifecycle.LiveData
import com.moneyassist.app.data.db.AppDatabase
import com.moneyassist.app.data.entity.*
import com.moneyassist.app.data.model.CategorySpending
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate

/**
 * Single source of truth for all data operations.
 * ViewModels talk to this layer; this talks to DAOs.
 *
 * Note: Use-case classes (Domain layer) can be added here as the app grows.
 */
class AppRepository(context: Context) {

    private val db = AppDatabase.getInstance(context)

    // ── Transactions / Expenses ───────────────────────────────────
    val allEntries: LiveData<List<ExpenseEntry>> = db.expenseEntryDao().getAllEntries()
    val recentEntries: LiveData<List<ExpenseEntry>> = db.expenseEntryDao().getRecentEntries()
    val totalExpenses: LiveData<Double> = db.expenseEntryDao().getTotalExpenses()
    val totalIncome: LiveData<Double> = db.expenseEntryDao().getTotalIncome()

    fun getEntriesBetween(start: String, end: String): LiveData<List<ExpenseEntry>> =
        db.expenseEntryDao().getEntriesBetween(start, end)

    fun getCategorySpendingBetween(start: String, end: String): LiveData<List<CategorySpending>> =
        db.expenseEntryDao().getCategorySpendingBetween(start, end)

    suspend fun getEntryById(id: Int): ExpenseEntry? =
        withContext(Dispatchers.IO) { db.expenseEntryDao().getById(id) }

    suspend fun saveEntry(entry: ExpenseEntry): Long =
        withContext(Dispatchers.IO) { db.expenseEntryDao().insertEntry(entry) }

    suspend fun updateEntry(entry: ExpenseEntry) =
        withContext(Dispatchers.IO) { db.expenseEntryDao().updateEntry(entry) }

    suspend fun deleteEntry(entry: ExpenseEntry) =
        withContext(Dispatchers.IO) { db.expenseEntryDao().deleteEntry(entry) }

    // Legacy Transactions
    fun getTransactionsBetween(start: String, end: String): LiveData<List<Transaction>> =
        db.transactionDao().getBetween(start, end)

    suspend fun insertTransaction(tx: Transaction) =
        withContext(Dispatchers.IO) { db.transactionDao().insert(tx) }

    suspend fun deleteTransaction(tx: Transaction) =
        withContext(Dispatchers.IO) { db.transactionDao().delete(tx) }

    // ── Bills ─────────────────────────────────────────────────────
    val upcomingBills: LiveData<List<Bill>> = db.billDao().getTop3Upcoming()
    val allBills: LiveData<List<Bill>> = db.billDao().getAll()
    val totalUpcoming: LiveData<Double> = db.billDao().getTotalUpcoming()

    fun getUpcomingBillsList(): LiveData<List<Bill>> = db.billDao().getUpcoming()
    fun getPaidBills(): LiveData<List<Bill>> = db.billDao().getPaid()

    suspend fun insertBill(bill: Bill) =
        withContext(Dispatchers.IO) { db.billDao().insertBill(bill) }

    suspend fun saveBill(bill: Bill) =
        withContext(Dispatchers.IO) { db.billDao().insertBill(bill) }

    suspend fun updateBill(bill: Bill) =
        withContext(Dispatchers.IO) { db.billDao().update(bill) }

    suspend fun deleteBill(bill: Bill) =
        withContext(Dispatchers.IO) { db.billDao().delete(bill) }

    suspend fun markBillPaid(billId: Int) =
        withContext(Dispatchers.IO) { db.billDao().markPaid(billId, LocalDate.now().toString()) }

    // ── Missions ──────────────────────────────────────────────────
    val activeMissions: LiveData<List<Mission>> = db.missionDao().getActive()
    val completedMissions: LiveData<List<Mission>> = db.missionDao().getCompleted()

    suspend fun saveMission(mission: Mission): Long =
        withContext(Dispatchers.IO) { db.missionDao().insertMission(mission) }

    suspend fun updateMission(mission: Mission) =
        withContext(Dispatchers.IO) { db.missionDao().update(mission) }

    // ── Budget categories ─────────────────────────────────────────
    val budgetCategories: LiveData<List<BudgetCategory>> = db.budgetCategoryDao().getAllBudgetCategories()

    suspend fun saveBudgetCategory(category: BudgetCategory) =
        withContext(Dispatchers.IO) { db.budgetCategoryDao().insertBudgetCategory(category) }

    suspend fun getSpendForCategory(categoryId: Int): Double =
        withContext(Dispatchers.IO) {
            val startOfMonth = LocalDate.now().withDayOfMonth(1).toString()
            db.expenseEntryDao().getSpendForCategory(categoryId, startOfMonth)
        }

    // ── Hub ───────────────────────────────────────────────────────
    val placedHubItems: LiveData<List<HubItem>> = db.hubItemDao().getAll()
    val totalPoints: LiveData<Int> = db.pointsLedgerDao().getTotalPoints()

    // ── Categories ────────────────────────────────────────────────
    val allCategories: LiveData<List<Category>> = db.categoryDao().getAllCategories()

    suspend fun insertCategory(category: Category) =
        withContext(Dispatchers.IO) { db.categoryDao().insertCategory(category) }

    suspend fun updateCategory(category: Category) =
        withContext(Dispatchers.IO) { db.categoryDao().updateCategory(category) }

    suspend fun deleteCategory(category: Category) =
        withContext(Dispatchers.IO) { db.categoryDao().deleteCategory(category) }

    suspend fun getCategoryById(id: Int): Category? =
        withContext(Dispatchers.IO) { db.categoryDao().getCategoryById(id) }

    companion object {
        @Volatile private var INSTANCE: AppRepository? = null
        fun getInstance(context: Context): AppRepository =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: AppRepository(context.applicationContext).also { INSTANCE = it }
            }
    }
}
