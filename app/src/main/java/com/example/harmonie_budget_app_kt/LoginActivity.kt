package com.example.harmonie_budget_app_kt

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.viewmodels.UserViewModel

/**
 * LoginActivity handles user authentication.
 * It provides fields for username and password entry,
 * validates credentials against stored user data,
 * and navigates to the Dashboard upon successful login.
 *
 * The Register button now correctly navigates to RegisterActivity
 * to allow new users to create an account.
 */
class LoginActivity : AppCompatActivity()
{
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    private val userViewModel = UserViewModel()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_log_in)
        btnRegister = findViewById(R.id.btn_register)

        // Login button validates credentials and navigates to Dashboard
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty())
            {
                // Call through the ViewModel layer to load user data
                val user = userViewModel.loadUser(this, username)
                if (user != null && user.password == password)
                {
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.putExtra("username", username)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    Toast.makeText(this, getString(R.string.error_invalid_credentials), Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            }
        }

        // Register button navigates to the registration screen
        // This fixes the previously non-functional navigation
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}