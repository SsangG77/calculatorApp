package com.sangjin.calculator_app

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class history(
    @PrimaryKey var uid:Int?,
    @ColumnInfo(name = "ExpressionText") var expressionText:String?,
    @ColumnInfo(name = "ResultText") var resultText:String?
    )