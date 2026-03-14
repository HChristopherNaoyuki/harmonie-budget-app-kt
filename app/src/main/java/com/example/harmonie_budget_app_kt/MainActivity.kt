// app/kotlin+java/com.example.harmonie_budget_app_kt/MainActivity.kt
package com.example.harmonie_budget_app_kt

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.User
import com.example.harmonie_budget_app_kt.utils.JsonHelper

/**
 * MainActivity - Login screen (Apple-like clean minimal UI).
 * Uses JSON for user persistence. Pre-populates a default user on first run.
 * Allman style brackets. Detailed comments for every step.
 */
class MainActivity : AppCompatActivity() {

    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_login)
        btnRegister = findViewById(R.id.btn_register)

        // Load or create default user on first launch
        val users = JsonHelper.loadUsers(this)
        if (users.isEmpty()) {
            JsonHelper.saveUsers(this, listOf(User("demo", "demo123")))
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val loadedUsers = JsonHelper.loadUsers(this)
            val user = loadedUsers.find { it.username == username && it.password == password }

            if (user != null) {
                Toast.makeText(this, "Login successful!", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, CategoryActivity::class.java))
                finish()
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegister.setOnClickListener {
            // Simple register for prototype - saves new user
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Enter details to register", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val currentUsers = JsonHelper.loadUsers(this).toMutableList()
            if (currentUsers.any { it.username == username }) {
                Toast.makeText(this, "Username already exists", Toast.LENGTH_SHORT).show()
            } else {
                currentUsers.add(User(username, password))
                JsonHelper.saveUsers(this, currentUsers)
                Toast.makeText(this, "Registered! Now login.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}