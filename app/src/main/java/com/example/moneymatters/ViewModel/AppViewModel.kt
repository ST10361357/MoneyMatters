package com.example.moneymatters.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.moneymatters.data.Category
import com.example.moneymatters.data.Expense
import com.example.moneymatters.data.User
import com.example.moneymatters.database.AppDb
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDb.getDb(application)
    private val userDao = database.UserDao()
    private val categoryDao = database.CategoryDao()
    private val expenseDao = database.ExpenseDao()

    fun registerUser(
        username: String, password: String,
        onResult: (User?) -> Unit
    ) {
        viewModelScope.launch {
            onResult(userDao.logIn(username, password))
        }
    }

    fun addCategory(name: String) {
        viewModelScope.launch {
            categoryDao.addCategory(Category(categoryName = name))
        }
    }

    fun addExpense(
        name: String,
        description: String,
        amount: Double,
        date: String,
        startTime: String,
        endTime: String,
        photo: String? = null,//opptional
        categoryId: Int
    ) {
        viewModelScope.launch {
            expenseDao.addExpense(
                Expense(
                    expenseName = name,
                    description = description, amount = amount, date = date,
                    startTime = startTime, endTime = endTime, photo = photo,//opptional
                    categoryId = categoryId
                )
            )
        }

        //login or register users
        fun validateLogin(username: String, password: String, onResult: (Boolean) -> Unit) {
            viewModelScope.launch {
                val user = userDao.logIn(username, password)
                onResult(user != null) // Returns true if user exists
            }
        }

    }
}
