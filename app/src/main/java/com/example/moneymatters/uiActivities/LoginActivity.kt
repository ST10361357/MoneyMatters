package com.example.moneymatters.uiActivities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.moneymatters.database.AppDb
import com.example.moneymatters.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        binding.buttonLogin.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                showToast("Please fill in all fields!")
                return@setOnClickListener
            }

            // Try Firebase first
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        showToast("Login successful!")
                        startActivity(Intent(this, NavMenuActivity::class.java))
                        finish()
                    } else {
                        //  Fallback: check Room database
                        lifecycleScope.launch(Dispatchers.IO) {
                            try {
                                val db = AppDb.getDb(applicationContext)
                                val user = db.UserDao().logIn(email, password)

                                withContext(Dispatchers.Main) {
                                    if (user != null && user.password == password) {
                                        showToast("Logged in (local DB)")
                                        startActivity(Intent(this@LoginActivity, NavMenuActivity::class.java))
                                        finish()
                                    } else {
                                        showToast("Invalid email or password!")
                                    }
                                }
                            } catch (e: Exception) {
                                withContext(Dispatchers.Main) {
                                    showToast("Error: ${e.message}")
                                }
                            }
                        }
                    }
                }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}

