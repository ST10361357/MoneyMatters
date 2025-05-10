package com.example.moneymatters.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.RawQuery
import androidx.room.Update
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.moneymatters.data.Goal

@Dao
interface GoalDao {
    @Insert
    suspend fun setUserGoal(goal: Goal)

    // get monthly goals
    @Query("SELECT * FROM goals WHERE month = :month")
    suspend fun getMonthlyGoal(month: String): Goal?

    //get all goals
    @Query("SELECT * FROM goals ORDER BY month ASC")
    suspend fun getAllGoals(): List<Goal>
    @RawQuery
    fun checkIfGoalTableExists(query: SimpleSQLiteQuery): Int
}
