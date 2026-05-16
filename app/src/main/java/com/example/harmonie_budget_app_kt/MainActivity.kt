package com.example.harmonie_budget_app_kt

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

/**
 * MainActivity is the landing page of the application.
 * It provides buttons for Login, Register, and Forgot Password.
 *
 * Part 3 Enhancement (Session Persistence):
 * - Does not clear session on start; session is preserved across app launches.
 * - Session restoration is handled by LoginActivity.
 */
class MainActivity : AppCompatActivity()
{
    private lateinit var sharedPrefs: SharedPreferences

    companion object
    {
        private const val PREFS_NAME = "harmonie_prefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize SharedPreferences
        sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val btnLogin: Button = findViewById(R.id.btn_login)
        val btnRegister: Button = findViewById(R.id.btn_register)
        val btnForgotPassword: Button = findViewById(R.id.btn_forgot_password)

        // Login button opens the login form screen
        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Register button opens the registration screen
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

        // Forgot Password button opens the forgot password screen
        btnForgotPassword.setOnClickListener {
            val intent = Intent(this, ForgotPasswordActivity::class.java)
            startActivity(intent)
        }
    }
}