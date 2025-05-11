package com.example.moneymatters.uiActivities

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymatters.adapter.CategoryExpenseTotalAdapter
import com.example.moneymatters.data.CategoryExpenseTotal
import com.example.moneymatters.database.AppDb
import com.example.moneymatters.databinding.ActivityViewcategoryExpensetotalBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
class ViewCategoryExpenseTotalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewcategoryExpensetotalBinding
    private lateinit var db: AppDb
    private lateinit var adapter: CategoryExpenseTotalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityViewcategoryExpensetotalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Room Database
        db = AppDb.getDb(this)

        // Setup RecyclerView
        setupRecyclerView()

        // Date Picker Listeners
        binding.startDateEditText.setOnClickListener { showDatePicker(binding.startDateEditText) }
        binding.endDateEditText.setOnClickListener { showDatePicker(binding.endDateEditText) }

        // Search Button Listener
        binding.searchButton.setOnClickListener {
            val startDate = binding.startDateEditText.text.toString()
            val endDate = binding.endDateEditText.text.toString()
            fetchCategoryTotals(startDate, endDate)
        }
    }

    private fun setupRecyclerView() {
        adapter = CategoryExpenseTotalAdapter(mutableListOf())
        binding.recyclerView.adapter = adapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchCategoryTotals(startDate: String, endDate: String) {
        lifecycleScope.launch(Dispatchers.IO) {
           val categoryTotals = db.ExpenseDao().getCategoryTotals(startDate, endDate)

            Log.d("Debug", "Retrieved Category Totals: $categoryTotals") // Debugging log

            withContext(Dispatchers.Main) {
               adapter.updateData(categoryTotals)
            }
        }
    }



    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()

        DatePickerDialog(this, { _, year, month, day ->
            editText.setText(String.format("%d/%d/%d", day, month + 1, year))
        },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateCategoryTotalsUI(categoryTotals: List<CategoryExpenseTotal>) {
        if (categoryTotals.isEmpty()) {
            binding.recyclerView.visibility = View.GONE
        } else {
            adapter.updateData(categoryTotals)
            binding.recyclerView.visibility = View.VISIBLE
        }
    }

}

