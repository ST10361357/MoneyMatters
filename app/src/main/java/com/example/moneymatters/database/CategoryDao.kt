package com.example.moneymatters.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.moneymatters.data.Category

@Dao
interface CategoryDao {
    @Insert
    suspend fun addCategory(category: Category)

    @Query("SELECT categoryId FROM categories WHERE categoryName = :categoryName LIMIT 1")
    suspend fun getCategoryId(categoryName: String): Int? // get ID by name.

    @Query("SELECT categoryName FROM categories WHERE categoryId = :categoryId LIMIT 1")
    suspend fun getCategoryName(categoryId: Int): String? //  column name.

    @Query("SELECT * FROM categories")
     fun getAllCategories(): LiveData<List<Category>> // get  all categories
}