package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.User
import com.example.harmonie_budget_app_kt.utils.JsonHelper

class RegisterActivity : AppCompatActivity()
{
    private lateinit var etName: EditText
    private lateinit var etSurname: EditText
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnHome: Button
    private lateinit var btnLogIn: Button

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etName = findViewById(R.id.et_name)
        etSurname = findViewById(R.id.et_surname)
        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)
        btnRegister = findViewById(R.id.btn_register)
        btnHome = findViewById(R.id.btn_home)
        btnLogIn = findViewById(R.id.btn_log_in)

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val surname = etSurname.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (name.isEmpty() || surname.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty())
            {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword)
            {
                Toast.makeText(this, getString(R.string.error_password_mismatch), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check for duplicate username before saving
            // (fixes L-22)
            if (JsonHelper.loadUser(this, username) != null)
            {
                Toast.makeText(this, getString(R.string.error_username_taken), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = User(name, surname, username, password)
            JsonHelper.saveUser(this, user)

            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnHome.setOnClickListener {
            finish()
        }

        btnLogIn.setOnClickListener {
            val intent = android.content.Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}