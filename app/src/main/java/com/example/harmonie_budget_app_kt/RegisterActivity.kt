// app/kotlin+java/com.example.harmonie_budget_app_kt/RegisterActivity.kt
package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.User
import com.example.harmonie_budget_app_kt.utils.JsonHelper

/**
 * RegisterActivity - Handles new user registration with all required fields.
 * Fields: Name, Surname, Username, Password, Confirm Password.
 * Validates password match before saving to users.json in budget_data folder.
 * Returns to login screen on success.
 */
class RegisterActivity : AppCompatActivity() {
    private lateinit var etName: EditText
    private lateinit var etSurname: EditText
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etName = findViewById(R.id.et_name)
        etSurname = findViewById(R.id.et_surname)
        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)
        btnRegister = findViewById(R.id.btn_register)

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val surname = etSurname.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirm = etConfirmPassword.text.toString().trim()

            if (name.isEmpty() || surname.isEmpty() || username.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirm) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val currentUsers = JsonHelper.loadUsers(this).toMutableList()
            if (currentUsers.any { it.username == username }) {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
            } else {
                currentUsers.add(User(name, surname, username, password))
                JsonHelper.saveUsers(this, currentUsers)
                Toast.makeText(this, "Registration successful! Please login.", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}