package com.example.moneymatters.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val expenseId: Int = 0,
    val expenseName: String,
    val description: String,
    val amount: Double,
    val date: Long,
    val startTime: Long,
    val endTime : Long,
    val photo: String? = null,//opptional
    val categoryId: Int//references the Category tables
)

