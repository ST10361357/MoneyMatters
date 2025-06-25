package com.example.moneymatters.uiActivities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.moneymatters.data.User
import android.util.Patterns
import androidx.lifecycle.lifecycleScope
import com.example.moneymatters.database.AppDb
import com.example.moneymatters.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.buttonRegister.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            // Validate email format
            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showToast("Invalid email format!")
                return@setOnClickListener
            }

            // Validate password
            if (password.length < 6) {
                showToast("Password must be at least 6 characters!")
                return@setOnClickListener
            }

            // Create user in Firebase first
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Now store user in local Room DB
                        lifecycleScope.launch(Dispatchers.IO) {
                            val db = AppDb.getDb(applicationContext)
                            db.UserDao().registerUser(User(0, email, password))

                            withContext(Dispatchers.Main) {
                                showToast("Registration successful!")
                                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                                finish()
                            }
                        }
                    } else {
                        showToast("Firebase registration failed: ${task.exception?.message}")
                    }
                }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
