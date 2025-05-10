package com.example.moneymatters.uiActivities


import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymatters.adapter.ExpenseAdapter
import com.example.moneymatters.data.Expense
import com.example.moneymatters.database.AppDb
import com.example.moneymatters.databinding.ActivityViewExpensesBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ViewExpensesActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewExpensesBinding
    private lateinit var db: AppDb
    private lateinit var adapter: ExpenseAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityViewExpensesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Room Database
        db = AppDb.getDb(this)

        // Set up RecyclerView
        setupRecyclerView()

        // Fetch all expenses initially
        fetchExpenses()

        // Date Picker Listeners
        binding.startDateEditText.setOnClickListener { showDatePicker(binding.startDateEditText) }
        binding.endDateEditText.setOnClickListener { showDatePicker(binding.endDateEditText) }

        // Search Button Listener with changed format
        binding.searchButton.setOnClickListener {

            if (binding.startDateEditText.text.toString().trim().isEmpty() || binding.endDateEditText.text.toString().trim().isEmpty()) {
                showToast("Please enter both start and end dates")
            }
            searchExpenses(binding.startDateEditText.text.toString(), binding.endDateEditText.text.toString())
        }
    }

    private fun setupRecyclerView() {
        adapter = ExpenseAdapter(emptyList())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchExpenses() {
        lifecycleScope.launch(Dispatchers.IO) {
            val expenses = db.ExpenseDao().getAllExpenses()
            Log.d("SearchDebug", "All Expenses in Database: $expenses")
            withContext(Dispatchers.Main) {
                adapter.updateExpenses(expenses)
                binding.recyclerView.visibility = View.VISIBLE
            }
        }

    }

    private fun searchExpenses(startDate: String, endDate: String) {
        lifecycleScope.launch(Dispatchers.IO) {
          //testing code
            val testExpenses = db.ExpenseDao().getExpensesByDateRangeSync("2025-05-01", "2025-05-10")
            Log.d("SearchDebug", "Start Date Input: $startDate, End Date Input: $endDate")

            val filteredExpenses = db.ExpenseDao().getExpensesByDateRangeSync(startDate, endDate)
            Log.d("SearchDebug", "Filtered Expenses: $filteredExpenses")
            withContext(Dispatchers.Main) {
                adapter.updateExpenses(filteredExpenses)
            }
        }
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            editText.setText(String.format("%d/%d/%d", day, month + 1, year))
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }



    fun updateExpense(expense: Expense) {
        lifecycleScope.launch {
            db.ExpenseDao().updateExpense(expense)
            showToast("Expense updated successfully!")
            fetchExpenses() // Refresh list
        }
    }


    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}

