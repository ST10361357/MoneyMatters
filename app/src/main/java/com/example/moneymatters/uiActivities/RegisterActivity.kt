package com.example.moneymatters.uiActivities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.moneymatters.data.User
import android.util.Patterns
import com.example.moneymatters.database.AppDb
import com.example.moneymatters.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize View Binding
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Registration button click listener
        binding.buttonRegister.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            // Validate email format
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Invalid email format!")
                return@setOnClickListener
            }

            // Validate password strength
            if (password.length < 6) {
                showToast("Password must be at least 6 characters!")
                return@setOnClickListener
            }

            // Create new user object
            val newUser = User(0, email, password) // Room auto-generates userId

            // Save to Room Database (Runs in a separate thread)
            Thread {
                val db = AppDb.getDb(applicationContext)
                db.UserDao().registerUser(newUser)

                // Ensure UI updates happen in main thread
                runOnUiThread {
                    showToast("Registration Successful!")
                    startActivity(Intent(this, LoginActivity::class.java))
                    finish()
                }
            }.start()
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
