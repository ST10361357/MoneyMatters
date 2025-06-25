package com.example.moneymatters.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "frozen_categories")
data class FrozenCategory(
    @PrimaryKey val categoryName: String,
    val startDate: String,
    val endDate: String
)
