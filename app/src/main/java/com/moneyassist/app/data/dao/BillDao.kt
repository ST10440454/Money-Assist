package com.moneyassist.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.moneyassist.app.data.entity.Bill

@Dao
interface BillDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bill: Bill): Long

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

    @Query("SELECT SUM(amount) FROM bills WHERE isPaid = 0")
    fun getTotalUpcoming(): LiveData<Double?>
}
