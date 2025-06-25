package com.example.moneymatters.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moneymatters.data.FrozenCategory

@Dao
interface FrozenCategoryDao {
    @Query("SELECT * FROM frozen_categories WHERE categoryName = :category AND :date BETWEEN startDate AND endDate")
    suspend fun isFrozen(category: String, date: String): FrozenCategory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun freezeCategory(freeze: FrozenCategory)

    @Delete
    suspend fun unfreezeCategory(freeze: FrozenCategory)
}
