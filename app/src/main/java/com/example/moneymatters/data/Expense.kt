package com.example.moneymatters.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.Companion.CASCADE
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    foreignKeys = [ForeignKey(
        entity = Category::class,
        parentColumns = ["categoryId"],
        childColumns = ["categoryId"],
        onDelete = CASCADE
    )]
)
data class Expense(
    @PrimaryKey(autoGenerate = true) val expenseId: Int = 0,
    val expenseName: String,
    val description: String,
    val amount: Double,
    val date: String,
    val startTime: String,
    val endTime : String,
    val photo: String?,//optional
    val categoryId: Int//references the categories
)

