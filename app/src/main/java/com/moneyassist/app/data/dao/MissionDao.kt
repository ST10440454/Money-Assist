package com.moneyassist.app.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.moneyassist.app.data.entity.Mission

@Dao
interface MissionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(mission: Mission): Long

    @Update
    suspend fun update(mission: Mission)

    @Delete
    suspend fun delete(mission: Mission)

    @Query("SELECT * FROM missions ORDER BY deadline ASC")
    fun getAll(): LiveData<List<Mission>>
}
