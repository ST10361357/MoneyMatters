package com.example.moneymatters.uiActivities

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.moneymatters.R
import com.example.moneymatters.data.CategoryExpenseTotal
import com.example.moneymatters.database.AppDb
import com.example.moneymatters.databinding.ActivityGoalcategoryChartBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class GoalCategoryChartActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalcategoryChartBinding
    private lateinit var db: AppDb
    private val dailyGoal = 500.0 // Customize this goal as needed

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalcategoryChartBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDb.getDb(this)

        binding.startDateEditText.setOnClickListener { showDatePicker(binding.startDateEditText) }
        binding.endDateEditText.setOnClickListener { showDatePicker(binding.endDateEditText) }

        binding.searchButton.setOnClickListener {
            val start = binding.startDateEditText.text.toString()
            val end = binding.endDateEditText.text.toString()
            fetchAndDisplayData(start, end)
        }
    }

    private fun fetchAndDisplayData(startDate: String, endDate: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val categoryTotals = db.ExpenseDao().getCategoryTotals(startDate, endDate)
            val streak = calculateStreak(categoryTotals, dailyGoal)

            withContext(Dispatchers.Main) {
                setupGoalChart(categoryTotals)
                binding.streakTextView.text = "🔥 Current Streak: $streak day(s) under budget"
            }
        }
    }

    private fun setupGoalChart(data: List<CategoryExpenseTotal>) {
        val entries = ArrayList<BarEntry>()
        val labels = ArrayList<String>()

        data.forEachIndexed { index, total ->
            entries.add(BarEntry(index.toFloat(), total.totalAmount.toFloat()))
            labels.add(total.categoryName)
        }

        val dataSet = BarDataSet(entries, "Expenses")
        dataSet.color = ContextCompat.getColor(this, R.color.teal_700)

        val barData = BarData(dataSet)
        barData.barWidth = 0.9f

        binding.goalProgressChart.apply {
            this.data = barData
            xAxis.valueFormatter = IndexAxisValueFormatter(labels)
            xAxis.position = XAxis.XAxisPosition.BOTTOM
            xAxis.granularity = 1f
            xAxis.setDrawGridLines(false)
            axisLeft.setDrawGridLines(false)
            axisRight.isEnabled = false
            description.isEnabled = false
            setFitBars(true)
            invalidate()
        }
    }

    private fun calculateStreak(data: List<CategoryExpenseTotal>, goal: Double): Int {
        // Since no date is available, we assume each entry is a day
        var streak = 0
        for (entry in data) {
            if (entry.totalAmount <= goal) streak++
            else break
        }
        return streak
    }

    private fun showDatePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            editText.setText(String.format("%02d/%02d/%04d", day, month + 1, year))
        },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}
