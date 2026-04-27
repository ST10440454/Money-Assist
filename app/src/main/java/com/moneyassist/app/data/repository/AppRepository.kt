package com.moneyassist.app.data.repository

import android.content.Context
import com.moneyassist.app.data.db.AppDatabase
import com.moneyassist.app.data.entity.*

class AppRepository(context: Context) {

    private val db = AppDatabase.getInstance(context)
    private val userDao = db.userDao()
    private val categoryDao = db.categoryDao()
    private val expenseDao = db.expenseEntryDao()
    private val transactionDao = db.transactionDao()
    private val billDao = db.billDao()
    private val missionDao = db.missionDao()
    private val budgetDao = db.budgetCategoryDao()

    // ── Users ──────────────────────────────────────────────────
    suspend fun registerUser(user: User) = userDao.insertUser(user)
    suspend fun login(username: String, password: String) = userDao.login(username, password)
    suspend fun getUserByUsername(username: String) = userDao.getUserByUsername(username)

    // ── Legacy Categories ──────────────────────────────────────
    fun getAllCategories() = categoryDao.getAllCategories()
    suspend fun insertCategory(category: Category) = categoryDao.insertCategory(category)
    suspend fun updateCategory(category: Category) = categoryDao.updateCategory(category)
    suspend fun deleteCategory(category: Category) = categoryDao.deleteCategory(category)
    suspend fun getCategoryById(id: Int) = categoryDao.getCategoryById(id)

    // ── Legacy Expense Entries ─────────────────────────────────
    fun getEntriesBetween(start: String, end: String) = expenseDao.getEntriesBetween(start, end)
    fun getCategorySpendingBetween(start: String, end: String) = expenseDao.getCategorySpendingBetween(start, end)
    suspend fun insertEntry(entry: ExpenseEntry) = expenseDao.insertEntry(entry)
    suspend fun updateEntry(entry: ExpenseEntry) = expenseDao.updateEntry(entry)
    suspend fun deleteEntry(entry: ExpenseEntry) = expenseDao.deleteEntry(entry)
    suspend fun getEntryById(id: Int) = expenseDao.getEntryById(id)

    // ── Transactions ───────────────────────────────────────────
    fun getRecentTransactions(limit: Int = 5) = transactionDao.getRecent(limit)
    fun getAllTransactions() = transactionDao.getAll()
    fun getTransactionsBetween(start: String, end: String) = transactionDao.getBetween(start, end)
    fun getNetWorth() = transactionDao.getNetWorth()
    suspend fun insertTransaction(tx: Transaction) = transactionDao.insert(tx)
    suspend fun updateTransaction(tx: Transaction) = transactionDao.update(tx)
    suspend fun deleteTransaction(tx: Transaction) = transactionDao.delete(tx)

    // ── Bills ──────────────────────────────────────────────────
    fun getUpcomingBills() = billDao.getUpcoming()
    fun getPaidBills() = billDao.getPaid()
    fun getAllBills() = billDao.getAll()
    fun getTotalUpcoming() = billDao.getTotalUpcoming()
    suspend fun insertBill(bill: Bill) = billDao.insert(bill)
    suspend fun updateBill(bill: Bill) = billDao.update(bill)
    suspend fun deleteBill(bill: Bill) = billDao.delete(bill)

    // ── Missions ───────────────────────────────────────────────
    fun getAllMissions() = missionDao.getAll()
    suspend fun insertMission(mission: Mission) = missionDao.insert(mission)
    suspend fun updateMission(mission: Mission) = missionDao.update(mission)
    suspend fun deleteMission(mission: Mission) = missionDao.delete(mission)

    // ── Budget Categories ──────────────────────────────────────
    fun getAllBudgetCategories() = budgetDao.getAll()
    suspend fun insertBudgetCategory(bc: BudgetCategory) = budgetDao.insert(bc)
    suspend fun updateBudgetCategory(bc: BudgetCategory) = budgetDao.update(bc)
    suspend fun deleteBudgetCategory(bc: BudgetCategory) = budgetDao.delete(bc)
}
