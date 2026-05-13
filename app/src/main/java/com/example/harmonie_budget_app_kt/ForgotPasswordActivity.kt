package com.example.harmonie_budget_app_kt

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.User
import com.example.harmonie_budget_app_kt.viewmodels.UserViewModel

/**
 * ForgotPasswordActivity allows a user to reset their password.
 *
 * Security Enhancement:
 * The new password is passed to JsonHelper.saveUser(), which hashes it using PBKDF2.
 *
 * Part 3 Enhancement:
 * Added RETURN HOME button that navigates back to the Landing Page (MainActivity).
 * All user-facing text uses string resources.
 */
class ForgotPasswordActivity : AppCompatActivity()
{
    private lateinit var etUsername: EditText
    private lateinit var etUserId: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnResetPassword: Button
    private lateinit var btnLogin: Button
    private lateinit var btnReturnHome: Button
    private val userViewModel = UserViewModel()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        etUsername = findViewById(R.id.et_username)
        etUserId = findViewById(R.id.et_user_id)
        etPassword = findViewById(R.id.et_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)
        btnResetPassword = findViewById(R.id.btn_reset_password)
        btnLogin = findViewById(R.id.btn_login)
        btnReturnHome = findViewById(R.id.btn_return_home)

        // RETURN HOME button handler.
        // Navigates back to the Landing Page (MainActivity).
        btnReturnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        // Login button returns to login screen.
        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Reset Password button validates and updates the password.
        btnResetPassword.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val userId = etUserId.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // Validate all fields are filled.
            if (username.isEmpty() || userId.isEmpty() || password.isEmpty() || confirmPassword.isEmpty())
            {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate passwords match using string resource.
            if (password != confirmPassword)
            {
                Toast.makeText(this, getString(R.string.error_password_mismatch), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Load existing user and verify User ID.
            val user = userViewModel.loadUser(this, username)

            if (user == null || user.userId != userId)
            {
                Toast.makeText(this, getString(R.string.username_or_user_id_invalid), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create updated user with new password (will be hashed by saveUser).
            val updatedUser = User(
                name = user.name,
                surname = user.surname,
                username = user.username,
                password = password,
                userId = user.userId
            )

            userViewModel.saveUser(this, updatedUser)
            Toast.makeText(this, "Password reset successfully", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}