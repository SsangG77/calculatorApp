package com.sangjin.calculator_app

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history")
    fun getAll():List<history>

    @Insert
    fun insertHistory(history: history)

    @Query("DELETE FROM history")
    fun deleteAll()

}