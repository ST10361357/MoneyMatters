package com.example.moneymatters.uiActivities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.moneymatters.database.AppDb
import com.example.moneymatters.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Login button click listener
        binding.buttonLogin.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            // Validate inputs
            if (email.isEmpty() || password.isEmpty()) {
                showToast("Please fill in all fields!")
                return@setOnClickListener
            }

            // Authenticate User
            lifecycleScope.launch(Dispatchers.IO) {
                val db = AppDb.getDb(applicationContext)
                val user = db.UserDao().logIn(email, password)

                withContext(Dispatchers.Main) {
                    if (user != null && user.password == password) {
                        showToast("Login Successful!")
                        startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                        finish()
                    } else {
                        showToast("Invalid email or password!")
                    }
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
