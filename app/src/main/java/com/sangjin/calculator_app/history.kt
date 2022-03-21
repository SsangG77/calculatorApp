package com.sangjin.calculator_app

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class history(
    @PrimaryKey val id: Int?,
    @ColumnInfo(name = "expression") val expression: String?,
    @ColumnInfo(name = "result") val result: String?,
)