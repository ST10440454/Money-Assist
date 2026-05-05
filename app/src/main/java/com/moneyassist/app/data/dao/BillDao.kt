package com.moneyassist.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.moneyassist.app.data.entity.Bill

/**
 * Data Access Object for the bills table.
 */
@Dao
interface BillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bill: Bill): Long

    @Update
    suspend fun update(bill: Bill)

    @Delete
    suspend fun delete(bill: Bill)

    /** Retrieves all unpaid bills sorted by due date. */
    @Query("SELECT * FROM bills WHERE isPaid = 0 ORDER BY dueDate ASC")
    fun getUpcoming(): LiveData<List<Bill>>

    /** Retrieves all paid bills sorted by payment date. */
    @Query("SELECT * FROM bills WHERE isPaid = 1 ORDER BY paidOn DESC")
    fun getPaid(): LiveData<List<Bill>>

    /** Retrieves all bills. */
    @Query("SELECT * FROM bills ORDER BY dueDate ASC")
    fun getAll(): LiveData<List<Bill>>

    /** Calculates the total amount of unpaid bills. */
    @Query("SELECT SUM(amount) FROM bills WHERE isPaid = 0")
    fun getTotalUpcoming(): LiveData<Double?>
}
