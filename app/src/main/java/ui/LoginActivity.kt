package ui


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.stashed.R
import com.example.stashed.ui.RegisterActivity
import data.AppDatabase
import kotlinx.coroutines.launch
import kotlin.jvm.java

class LoginActivity : AppCompatActivity() {

    // View references
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var tvError: TextView
    private lateinit var tvGoToRegister: TextView

    // SharedPreferences for session
    private lateinit var sharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Set up SharedPreferences
        sharedPrefs = getSharedPreferences("StashedSession", MODE_PRIVATE)

       /* // Check if user is already logged in — skip to main if so
        if (sharedPrefs.getInt("userId", -1) != -1) {
            goToMain()
            return
        }*/

        // Connect views to variables
        etUsername = findViewById(R.id.etUsername)
        etPassword = findViewById(R.id.etPassword)
        btnLogin = findViewById(R.id.btnLogin)
        tvError = findViewById(R.id.tvError)
        tvGoToRegister = findViewById(R.id.tvGoToRegister)

        // Login button click
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            // Basic validation
            if (username.isEmpty() || password.isEmpty()) {
                showError("Please fill in all fields")
                return@setOnClickListener
            }

            // Query database on background thread
            lifecycleScope.launch {
                val db = AppDatabase.getDatabase(applicationContext)
                val user = db.userDao().loginUser(username, password)

                runOnUiThread {
                    if (user != null) {
                        // Save session
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

        // Navigate to register screen
        tvGoToRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun showError(message: String) {
        tvError.text = message
        tvError.visibility = View.VISIBLE
    }

    private fun goToMain() {
        // For now, just a placeholder — Member 2/3 will create MainActivity
        // startActivity(Intent(this, MainActivity::class.java))
        // finish()
    }
}