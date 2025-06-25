package com.example.moneymatters.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.moneymatters.data.Category
import com.example.moneymatters.data.Expense
import com.example.moneymatters.data.FrozenCategory
import com.example.moneymatters.data.Goal
import com.example.moneymatters.data.User

@Database(entities = [User::class, Category::class, Expense::class, Goal::class, FrozenCategory::class], version = 1)
abstract class AppDb: RoomDatabase() {
    abstract fun UserDao(): UserDao
    abstract fun CategoryDao(): CategoryDao
    abstract fun ExpenseDao(): ExpenseDao

    abstract fun GoalDao(): GoalDao
    abstract fun FrozenCategoryDao(): FrozenCategoryDao

    //db instance
    companion object {

        @Volatile
        private var INSTANCE: AppDb? = null

        fun getDb(context: Context): AppDb {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDb::class.java,
                    "mm_db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}