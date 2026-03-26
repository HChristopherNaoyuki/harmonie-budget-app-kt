package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.User
import com.example.harmonie_budget_app_kt.utils.JsonHelper

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        val etName: EditText = findViewById(R.id.et_name)
        val etSurname: EditText = findViewById(R.id.et_surname)
        val etUsername: EditText = findViewById(R.id.et_username)
        val etPassword: EditText = findViewById(R.id.et_password)
        val etConfirmPassword: EditText = findViewById(R.id.et_confirm_password)
        val btnRegister: Button = findViewById(R.id.btn_register)

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val surname = etSurname.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString()
            val confirmPassword = etConfirmPassword.text.toString()

            if (name.isEmpty() || surname.isEmpty() || username.isEmpty()) {
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!isPasswordValid(password)) {
                Toast.makeText(this, "Password must be at least 8 characters, with 1 letter, 1 number, and 1 special character", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User(name, surname, username, password)
            JsonHelper.saveUser(this, user)

            Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun isPasswordValid(password: String): Boolean {
        if (password.length < 8) return false
        if (!password.matches(Regex(".*[A-Za-z].*"))) return false
        if (!password.matches(Regex(".*[0-9].*"))) return false
        if (!password.matches(Regex(".*[@$!%*?&].*"))) return false
        return true
    }
}