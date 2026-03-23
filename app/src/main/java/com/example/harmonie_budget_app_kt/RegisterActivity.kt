package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import com.example.harmonie_budget_app_kt.models.User
import com.example.harmonie_budget_app_kt.utils.JsonHelper

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etName: TextInputEditText = findViewById(R.id.et_name)
        val etSurname: TextInputEditText = findViewById(R.id.et_surname)
        val etUsername: TextInputEditText = findViewById(R.id.et_username)
        val etPassword: TextInputEditText = findViewById(R.id.et_password)
        val etConfirmPassword: TextInputEditText = findViewById(R.id.et_confirm_password)
        val btnRegister: Button = findViewById(R.id.btn_register)

        // Password validation logic (enforced exactly as required by the assignment)
        // At least 8 characters, at least one letter, one number, one special character
        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val surname = etSurname.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (name.isEmpty() || surname.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Password requirements check
            if (password.length < 8) {
                Toast.makeText(this, "Password must be at least 8 characters", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!password.matches(".*[a-zA-Z].*".toRegex())) {
                Toast.makeText(this, "Password must contain at least one letter", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!password.matches(".*[0-9].*".toRegex())) {
                Toast.makeText(this, "Password must contain at least one number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*".toRegex())) {
                Toast.makeText(this, "Password must contain at least one special character", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Save user data using the corrected JsonHelper method
            // Data is now isolated in username.json inside budget_data folder
            val user = User(name, surname, username, password)
            JsonHelper.saveUser(this, user)

            Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}