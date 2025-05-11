package com.example.moneymatters.uiActivities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.moneymatters.databinding.ActivityNavmenuBinding



class NavMenuActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNavmenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNavmenuBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.buttonAddExpense.setOnClickListener {
            startActivity(Intent(this, CategoryExpenseActivity::class.java))
        }

        binding.buttonSetGoal.setOnClickListener {
            startActivity(Intent(this, GoalActivity::class.java))
        }

        binding.buttonViewExpense.setOnClickListener {
            startActivity(Intent(this, ViewExpensesActivity::class.java))
        }

        binding.buttonViewTotals.setOnClickListener {
            startActivity(Intent(this, ViewCategoryExpenseTotalActivity::class.java))
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
