package com.example.harmonie_budget_app_kt

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.User
import com.example.harmonie_budget_app_kt.viewmodels.UserViewModel

class ForgotPasswordActivity : AppCompatActivity()
{
    private lateinit var etUsername: EditText
    private lateinit var etUserId: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnResetPassword: Button
    private lateinit var btnLogin: Button
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

        /*
            The Login button returns the user to the login screen.
            This matches the button label and position in the mock-up.
        */
        btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        /*
            The Reset Password button performs the following steps:
            1. Validates that all fields are filled.
            2. Checks that the new passwords match.
            3. Loads the user by username.
            4. Verifies that the entered User ID matches the stored User ID.
            5. Updates the password in the User object and saves it to JSON.
            This ensures secure password reset using the existing data model.
        */
        btnResetPassword.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val userId = etUserId.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (username.isEmpty() || userId.isEmpty() || password.isEmpty() || confirmPassword.isEmpty())
            {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword)
            {
                Toast.makeText(this, getString(R.string.error_password_mismatch), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = userViewModel.loadUser(this, username)
            if (user == null || user.userId != userId)
            {
                Toast.makeText(this, getString(R.string.username_or_user_id_invalid), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

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