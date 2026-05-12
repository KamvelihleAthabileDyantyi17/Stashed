package com.example.stashed.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.stashed.MainActivity
import com.example.stashed.R
import com.example.stashed.data.AppDatabase // FIXED IMPORT
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvError: TextView
    private lateinit var tvGoToRegister: TextView
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        sharedPrefs = getSharedPreferences("StashedSession", MODE_PRIVATE)

        // Check if user is already logged in — skip to main if so
        if (sharedPrefs.getInt("userId", -1) != -1) {
            goToMain()
            return
        }

        etUsername    = findViewById(R.id.etUsername)
        etPassword    = findViewById(R.id.etPassword)
        btnLogin      = findViewById(R.id.btnLogin)
        tvError       = findViewById(R.id.tvError)
        tvGoToRegister = findViewById(R.id.tvGoToRegister)

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                showError("Please fill in all fields")
                return@setOnClickListener
            }

            lifecycleScope.launch {
                // Simplified call to AppDatabase
                val db   = AppDatabase.getDatabase(applicationContext)
                val user = db.userDao().loginUser(username, password)

                runOnUiThread {
                    if (user != null) {
                        sharedPrefs.edit()
                            .putInt("userId", user.id)
                            .putString("username", user.username)
                            .putString("fullName", user.fullName)
                            .apply()
                        goToMain()
                    } else {
                        showError("Incorrect username or password")
                    }
                }
            }
        }

        tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }

    private fun goToMain() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        })
        finish()
    }
}