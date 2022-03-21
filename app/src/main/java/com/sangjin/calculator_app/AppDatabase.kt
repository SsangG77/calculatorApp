package com.sangjin.calculator_app

import androidx.room.Database
import androidx.room.Entity
import androidx.room.RoomDatabase


@Database(entities = [history::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao

}