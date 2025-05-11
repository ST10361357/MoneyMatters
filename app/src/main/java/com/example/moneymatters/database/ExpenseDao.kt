package com.example.moneymatters.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.moneymatters.data.CategoryExpenseTotal
import com.example.moneymatters.data.Expense

@Dao
interface ExpenseDao {
    @Insert
    suspend fun addExpense(expense: Expense)//insert

    @Update
    suspend fun updateExpense(expense: Expense)

    @Delete
    suspend fun deleteExpense(expense: Expense)

    //fetch expenses
    @Query("SELECT * FROM expenses")
    suspend fun getAllExpenses(): List<Expense>


    //fetch by expense Id
    @Query("SELECT * FROM expenses WHERE expenseId = :expenseId")
    suspend fun getExpenseById(expenseId: Int): Expense

    //search date range
    @Query("SELECT * FROM expenses WHERE date >= :startDate AND date <= :endDate")
    suspend fun getExpensesByDateRangeSync( startDate: String, endDate: String): List<Expense>

    @Query("""
    SELECT c.categoryName, SUM(e.amount) as totalAmount
    FROM expenses e
    INNER JOIN categories c ON e.categoryId = c.categoryId
    WHERE e.date BETWEEN :startDate AND :endDate
    GROUP BY e.categoryId
""")
    fun getCategoryTotals(startDate: String, endDate: String): List<CategoryExpenseTotal>








}