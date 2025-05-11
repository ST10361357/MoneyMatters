package com.example.moneymatters.uiActivities

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.moneymatters.adapter.GoalAdapter
import com.example.moneymatters.data.Goal
import com.example.moneymatters.database.AppDb
import com.example.moneymatters.database.GoalDao
import com.example.moneymatters.databinding.ActivityGoalBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class GoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalBinding
    private lateinit var goalDao: GoalDao
    private lateinit var adapter: GoalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // initialize RecyclerView
        adapter = GoalAdapter(emptyList())
        binding.recyclerViewGoals.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewGoals.adapter = adapter

        // initialize Database
        val db = AppDb.getDb(this)
        goalDao = db.GoalDao()

        //  button click listeners
        binding.monthSelectionEditText.setOnClickListener { showMonthPicker() }
        binding.buttonSaveGoal.setOnClickListener { saveUserGoal() }
        binding.buttonViewGoals.setOnClickListener { toggleGoalList() }

        //  Load all goals when activity starts
        fetchGoalsFromDb()
    }

    // toggle the goal list visibility
    private fun toggleGoalList() {
        if (adapter.getGoals().isNotEmpty()) {
            binding.recyclerViewGoals.visibility =
                if (binding.recyclerViewGoals.visibility == View.VISIBLE) View.GONE else View.VISIBLE
        } else {
            showToast("No goals available!")
        }
    }

    // fetch goals from database
    private fun fetchGoalsFromDb() {
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val goals = goalDao.getAllGoals()
                withContext(Dispatchers.Main) {
                    adapter.updateData(goals)
                    binding.recyclerViewGoals.visibility = if (goals.isEmpty()) View.GONE else View.VISIBLE
                }
            } catch (e: Exception) {
                Log.e("GoalActivity", "Error fetching goals", e)
            }
        }
    }
    //select month
    private fun showMonthPicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)

        val datePicker = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, _ ->
                val formattedMonth = String.format("%04d-%02d", selectedYear, selectedMonth + 1)
                binding.monthSelectionEditText.setText(formattedMonth)
            },
            year, month, 1
        )

        datePicker.datePicker.apply {
            calendar.set(year, month, 1)
            minDate = calendar.timeInMillis
        }

        datePicker.show()
    }



    // save a user goal
    private fun saveUserGoal() {
        val month = binding.monthSelectionEditText.text.toString()
        val minGoal = binding.minGoal.text.toString().toDoubleOrNull()
        val maxGoal = binding.maxGoal.text.toString().toDoubleOrNull()

        if (month.isEmpty() || minGoal == null || maxGoal == null || minGoal >= maxGoal) {
            showToast("Invalid input! Ensure the month is selected and max goal is greater than min goal.")
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val existingGoal = goalDao.getMonthlyGoal(month)

                if (existingGoal == null) {
                    val monthlyGoal = Goal(month = month, minimumGoal = minGoal, maximumGoal = maxGoal)
                    goalDao.setUserGoal(monthlyGoal)
                    val intent = Intent(this@GoalActivity, NavMenuActivity::class.java)
                    startActivity(intent)
                } else {
                    withContext(Dispatchers.Main) { showToast("Goal for $month already exists!") }
                    return@launch
                }

                withContext(Dispatchers.Main) {
                    showToast("Goal saved successfully!")
                    fetchGoalsFromDb() //  Update goallist after saving
                }
            } catch (e: Exception) {
                Log.e("GoalActivity", "Error saving goal: ${e.message}", e)
                withContext(Dispatchers.Main) { showToast("Failed to save goal.") }
            }
        }
    }


    // display a toast message
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}




