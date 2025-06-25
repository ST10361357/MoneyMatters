package com.example.moneymatters.repository

import com.example.moneymatters.data.Expense
import com.example.moneymatters.database.AppDb
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

//expense data operations across Room and Firestore
object ExpenseRepository {

    //instance of Firestore from Firebase
    private val firestore = Firebase.firestore
    private val collectionRef = firestore.collection("expenses")

    // Add an expense to both Room database and Firestore
    fun addExpense(expense: Expense, roomDb: AppDb) {
        // Save to Room
        CoroutineScope(Dispatchers.IO).launch {
            roomDb.ExpenseDao().addExpense(expense)
        }

        // Save to Firestore
        collectionRef.add(expense)
    }

    // gets all local expenses from Room and returns result on callback on the main thread
    fun getLocalExpenses(roomDb: AppDb, callback: (List<Expense>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val local = roomDb.ExpenseDao().getAllExpenses() // Must be suspend function
            withContext(Dispatchers.Main) {
                callback(local)
            }
        }
    }

    // gets all cloud-based expenses from Firestore and returns them via callback
    fun getCloudExpenses(callback: (List<Expense>) -> Unit) {
        collectionRef.get().addOnSuccessListener { result ->
            val expenses = result.map { it.toObject(Expense::class.java) } // Convert Firestore documents to Expense objects
            callback(expenses) // Return list via callback
        }
    }
}
