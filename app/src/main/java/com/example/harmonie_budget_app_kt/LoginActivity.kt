package com.example.harmonie_budget_app_kt

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.utils.JsonHelper
import com.example.harmonie_budget_app_kt.viewmodels.UserViewModel

/**
 * LoginActivity handles user authentication.
 *
 * Security Enhancement:
 * Passwords are verified using PBKDF2 hashing via JsonHelper.verifyPassword().
 * The plaintext password entered by the user is never stored or compared directly.
 *
 * Part 3 Enhancement:
 * Added RETURN HOME button that navigates back to the Landing Page (MainActivity).
 */
class LoginActivity : AppCompatActivity()
{
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var btnReturnHome: Button

    private val userViewModel = UserViewModel()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_log_in)
        btnRegister = findViewById(R.id.btn_register)
        btnReturnHome = findViewById(R.id.btn_return_home)

        // Part 3 Enhancement: RETURN HOME button handler.
        // Navigates back to the Landing Page (MainActivity).
        // Uses FLAG_ACTIVITY_CLEAR_TOP to clear the back stack and prevent duplicates.
        btnReturnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        // Login button click listener.
        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty())
            {
                // Load the user object from storage.
                val user = userViewModel.loadUser(this, username)

                if (user != null)
                {
                    // Verify the entered password against the stored hash.
                    val isPasswordValid = JsonHelper.verifyPassword(password, user.password)

                    if (isPasswordValid)
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
                    Toast.makeText(this, getString(R.string.error_invalid_credentials), Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            }
        }

        // Register button click listener.
        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}