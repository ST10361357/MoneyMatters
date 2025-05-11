package com.example.moneymatters.data


import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "goals")
data class Goal (
    @PrimaryKey(autoGenerate = true) val goalId: Int = 0,
    val month: String,
    val minimumGoal: Double,
    val maximumGoal: Double
)
