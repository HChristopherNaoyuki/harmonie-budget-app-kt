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
 * MainActivity - Login screen with two buttons: Login and Register.
 * Login uses username and password.
 * Register button opens RegisterActivity with full form (name, surname, etc.).
 * Default demo user is created on first launch if none exists.
 * All data saved to users.json in budget_data folder.
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

        // Create default demo user on first run
        val users = JsonHelper.loadUsers(this)
        if (users.isEmpty()) {
            JsonHelper.saveUsers(this, listOf(User("Demo", "User", "demo", "demo123")))
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
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}