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
/*
class GoalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGoalBinding
    private lateinit var goalDao: GoalDao
    private lateinit var adapter: GoalAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGoalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //part of test code
        //Set up RecyclerView
        //adapter = GoalAdapter(emptyList())
        adapter = GoalAdapter(emptyList()) { selectedGoal -> showGoalDetails(selectedGoal) } // ✅ Initialize here
        binding.recyclerViewGoals.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewGoals.adapter = adapter

        // Initialize Database
        val db = AppDb.getDb(this)
        goalDao = db.GoalDao()

        // Set up RecyclerView
        //setupRecyclerView() coded out during testing

        // Month Picker for Goal Selection
        binding.monthSelectionEditText.setOnClickListener { showMonthPicker() }

        // Save Goal Button
        binding.buttonSaveGoal.setOnClickListener { saveUserGoal() }

        // View Goals Button
        binding.buttonViewGoals.setOnClickListener { toggleGoalList() }

        // Load All Goals on Start
        //fetchAllGoals()
        fetchGoalsFromDb()
        //test code for view goal button
        binding.buttonViewGoals.setOnClickListener {
            if (adapter.goalList.isNotEmpty()) { // Check if goals exist
                binding.recyclerViewGoals.visibility = View.VISIBLE //  Show the full list
            } else {
                showToast("No goals available!") //  Prevent empty list crash
            }
        }


    }


    /*private fun setupRecyclerView() {
        adapter = GoalAdapter(emptyList())
        binding.recyclerViewGoals.adapter = adapter
        binding.recyclerViewGoals.layoutManager = LinearLayoutManager(this)
    }*/
    //testing code
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



/*
    private fun saveUserGoal() {
        val month = binding.monthSelectionEditText.text.toString()
        val minGoal = binding.minGoal.text.toString().toDoubleOrNull()
        val maxGoal = binding.maxGoal.text.toString().toDoubleOrNull()

        if (month.isEmpty() || minGoal == null || maxGoal == null || minGoal >= maxGoal) {
            showToast("Invalid input! Ensure the month is selected and max goal is greater than min goal.")
            return
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val existingGoal = goalDao.getMonthlyGoal(month) // Pass the month string, NOT a Goal object

            if (existingGoal == null) {
                val monthlyGoal = Goal(month = month, minimumGoal = minGoal, maximumGoal = maxGoal)
                goalDao.setUserGoal(monthlyGoal) // Insert a new goal since none exists
            } else {
                showToast("Goal for $month already exists!")
            }

            withContext(Dispatchers.Main) {
                fetchAllGoals()
            }
        }
        // Navigate to View Nav screen
        //val intent = Intent(this@GoalActivity, NavMenuActivity::class.java)
        //startActivity(intent)

    }*/
    //test code

private fun saveUserGoal() {
    val month = binding.monthSelectionEditText.text.toString()
    val minGoal = binding.minGoal.text.toString().toDoubleOrNull()
    val maxGoal = binding.maxGoal.text.toString().toDoubleOrNull()

    // Validate user input
    if (month.isEmpty() || minGoal == null || maxGoal == null || minGoal >= maxGoal) {
        showToast("Invalid input! Ensure the month is selected and max goal is greater than min goal.")
        return
    }

    lifecycleScope.launch(Dispatchers.IO) {
        try {
            val existingGoal = goalDao.getMonthlyGoal(month)

            if (existingGoal == null) {
                val monthlyGoal = Goal(month = month, minimumGoal = minGoal, maximumGoal = maxGoal)
                goalDao.setUserGoal(monthlyGoal) // insert new goal
                showToast("Goal Successfully Captured!")

            } else {
                withContext(Dispatchers.Main) { showToast("Goal for $month already exists!") }
            }

            withContext(Dispatchers.Main) { fetchAllGoals() } // update UI
        } catch (e: Exception) {
            Log.e("GoalActivity", "Error saving goal: ${e.message}", e)
            withContext(Dispatchers.Main) { showToast("Failed to save goal.") }
        }
    }
}


    private fun fetchAllGoals() {
        lifecycleScope.launch(Dispatchers.IO) {
            val allGoals = goalDao.getAllGoals()

            withContext(Dispatchers.Main) {
                adapter.updateData(allGoals)
                binding.recyclerViewGoals.visibility = if (allGoals.isEmpty()) View.GONE else View.VISIBLE
            }
        }
    }

    private fun toggleGoalList() {
        binding.recyclerViewGoals.visibility =
            if (binding.recyclerViewGoals.visibility == View.VISIBLE) View.GONE else View.VISIBLE
    }

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
    //test data
    private fun showGoalDetails(goal: Goal) {
        binding.detailView.visibility = View.VISIBLE
        binding.textGoalMonth.text = "Month: ${goal.month}"
        binding.textGoalRange.text = "Goal Range: ${goal.minimumGoal} - ${goal.maximumGoal}"
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}*/
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

    //details


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




