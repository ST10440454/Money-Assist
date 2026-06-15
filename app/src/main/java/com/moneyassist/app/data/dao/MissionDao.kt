package com.moneyassist.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.moneyassist.app.data.entity.Mission

@Dao
interface MissionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMission(mission: Mission): Long

    @Update
    suspend fun update(mission: Mission)

    @Delete
    suspend fun delete(mission: Mission)

    @Query("SELECT * FROM missions WHERE isCompleted = 0 ORDER BY id DESC")
    fun getActive(): LiveData<List<Mission>>

    @Query("SELECT * FROM missions WHERE isCompleted = 1 ORDER BY id DESC")
    fun getCompleted(): LiveData<List<Mission>>

    @Query("SELECT * FROM missions ORDER BY id DESC")
    fun getAll(): LiveData<List<Mission>>

    @Query("SELECT * FROM missions WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): Mission?

    @Query("UPDATE missions SET currentAmount = :amount WHERE id = :id")
    suspend fun updateProgress(id: Int, amount: Double)

    @Query("UPDATE missions SET isCompleted = 1 WHERE id = :id")
    suspend fun markCompleted(id: Int)
}
