package com.moneyassist.app.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.moneyassist.app.data.dao.*
import com.moneyassist.app.data.entity.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        User::class,
        Category::class,
        ExpenseEntry::class,
        Transaction::class,
        Bill::class,
        Mission::class,
        BudgetCategory::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun expenseEntryDao(): ExpenseEntryDao
    abstract fun transactionDao(): TransactionDao
    abstract fun billDao(): BillDao
    abstract fun missionDao(): MissionDao
    abstract fun budgetCategoryDao(): BudgetCategoryDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "money_assist.db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Seed only the default category names needed for the expense spinner.
                            // No transactions, bills, missions or budget amounts are pre-filled —
                            // the user enters all their own data.
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    seedDefaultCategories(database)
                                }
                            }
                        }
                    })
                    .build().also { INSTANCE = it }
            }

        private suspend fun seedDefaultCategories(db: AppDatabase) {
            val catDao = db.categoryDao()
            catDao.insertCategory(Category(name = "Food & Groceries", minimumGoal = 0.0, maximumGoal = 0.0))
            catDao.insertCategory(Category(name = "Transport",        minimumGoal = 0.0, maximumGoal = 0.0))
            catDao.insertCategory(Category(name = "Entertainment",    minimumGoal = 0.0, maximumGoal = 0.0))
            catDao.insertCategory(Category(name = "Health",           minimumGoal = 0.0, maximumGoal = 0.0))
            catDao.insertCategory(Category(name = "Utilities",        minimumGoal = 0.0, maximumGoal = 0.0))
            catDao.insertCategory(Category(name = "Housing / Rent",   minimumGoal = 0.0, maximumGoal = 0.0))
            catDao.insertCategory(Category(name = "Education",        minimumGoal = 0.0, maximumGoal = 0.0))
            catDao.insertCategory(Category(name = "Clothing",         minimumGoal = 0.0, maximumGoal = 0.0))
            catDao.insertCategory(Category(name = "Salary / Income",  minimumGoal = 0.0, maximumGoal = 0.0))
            catDao.insertCategory(Category(name = "Other",            minimumGoal = 0.0, maximumGoal = 0.0))
        }
    }
}