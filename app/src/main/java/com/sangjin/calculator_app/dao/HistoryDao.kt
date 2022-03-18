package com.sangjin.calculator_app.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sangjin.calculator_app.model.history

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history")
    fun getAll() : List<history>

    @Insert
    fun insertHistory(history: history)


}