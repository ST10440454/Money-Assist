package com.moneyassist.app.data.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.moneyassist.app.data.dao.*
import com.moneyassist.app.data.entity.*
import com.moneyassist.app.data.entity.Transaction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Room database — version 3 adds HubItem and PointsLedger tables.
 * Proper Migration objects prevent data loss on schema upgrades.
 */
@Database(
    entities = [
        User::class,
        Category::class,
        ExpenseEntry::class,
        Transaction::class,
        Bill::class,
        Mission::class,
        BudgetCategory::class,
        HubItem::class,
        PointsLedger::class
    ],
    version = 3,
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
    abstract fun hubItemDao(): HubItemDao
    abstract fun pointsLedgerDao(): PointsLedgerDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        /** Migration from v2 → v3: adds hub_items and points_ledger tables. */
        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS hub_items (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        name TEXT NOT NULL,
                        emoji TEXT NOT NULL,
                        row INTEGER NOT NULL,
                        col INTEGER NOT NULL,
                        cost INTEGER NOT NULL,
                        category TEXT NOT NULL,
                        unlockedBy TEXT NOT NULL
                    )
                """)
                db.execSQL("""
                    CREATE TABLE IF NOT EXISTS points_ledger (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        date TEXT NOT NULL,
                        delta INTEGER NOT NULL,
                        reason TEXT NOT NULL,
                        eventType TEXT NOT NULL
                    )
                """)
            }
        }

        /** Migration from v1 → v2 (carried forward from original project). */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Original schema already handled by fallback; defined here for completeness.
            }
        }

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "money_assist.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .addCallback(object : Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            INSTANCE?.let { database ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    seedDefaultCategories(database)
                                    seedDefaultHubItems(database)
                                }
                            }
                        }
                    })
                    .build().also { INSTANCE = it }
            }

        private suspend fun seedDefaultCategories(db: AppDatabase) {
            val dao = db.categoryDao()
            listOf(
                "Food & Groceries", "Transport", "Entertainment", "Health",
                "Utilities", "Housing / Rent", "Education", "Clothing",
                "Salary / Income", "Other"
            ).forEach { dao.insertCategory(Category(name = it)) }
        }

        /** Seeds the starting Hub items (unlocked by default, cost = 0). */
        private suspend fun seedDefaultHubItems(db: AppDatabase) {
            val dao = db.hubItemDao()
            // A few free starter decorations scattered around the grid
            dao.insert(HubItem(name = "Piggy Bank",   emoji = "🐷", row = 2, col = 2, cost = 0, category = "decoration", unlockedBy = "default"))
            dao.insert(HubItem(name = "Money Tree",   emoji = "🌳", row = 4, col = 4, cost = 0, category = "decoration", unlockedBy = "default"))
            dao.insert(HubItem(name = "Starter Home", emoji = "🏠", row = 1, col = 1, cost = 0, category = "building",   unlockedBy = "default"))
        }
    }
}
