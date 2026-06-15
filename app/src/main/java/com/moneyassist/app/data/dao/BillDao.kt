package com.moneyassist.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.moneyassist.app.data.entity.Bill

@Dao
interface BillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBill(bill: Bill): Long

    @Update
    suspend fun update(bill: Bill)

    @Delete
    suspend fun delete(bill: Bill)

    @Query("SELECT * FROM bills WHERE isPaid = 0 ORDER BY dueDate ASC")
    fun getUpcoming(): LiveData<List<Bill>>

    @Query("SELECT * FROM bills WHERE isPaid = 1 ORDER BY paidOn DESC")
    fun getPaid(): LiveData<List<Bill>>

    @Query("SELECT * FROM bills ORDER BY dueDate ASC")
    fun getAll(): LiveData<List<Bill>>

    /** Suspend version for WorkManager / coroutine-based callers. */
    @Query("SELECT * FROM bills ORDER BY dueDate ASC")
    suspend fun getAllBillsList(): List<Bill>

    @Query("SELECT * FROM bills WHERE isPaid = 0 ORDER BY dueDate ASC LIMIT 3")
    fun getTop3Upcoming(): LiveData<List<Bill>>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM bills WHERE isPaid = 0")
    fun getTotalUpcoming(): LiveData<Double>

    @Query("UPDATE bills SET isPaid = 1, paidOn = :date WHERE id = :id")
    suspend fun markPaid(id: Int, date: String)
}
