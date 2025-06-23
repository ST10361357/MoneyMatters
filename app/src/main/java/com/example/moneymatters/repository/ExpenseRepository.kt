package com.example.moneymatters.repository

object ExpenseRepository {

    private val firestore = Firebase.firestore
    private val collectionRef = firestore.collection("expenses")

    fun addExpense(expense: Expense, roomDb: AppDb) {
        // Save to Room
        CoroutineScope(Dispatchers.IO).launch {
            roomDb.ExpenseDao().insert(expense)
        }

        // Save to Firestore
        collectionRef.add(expense)
    }

    fun getLocalExpenses(roomDb: AppDb, callback: (List<Expense>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val local = roomDb.ExpenseDao().getAll() // Must be suspend function
            withContext(Dispatchers.Main) {
                callback(local)
            }
        }
    }

    fun getCloudExpenses(callback: (List<Expense>) -> Unit) {
        collectionRef.get().addOnSuccessListener { result ->
            val expenses = result.map { it.toObject(Expense::class.java) }
            callback(expenses)
        }
    }
}
