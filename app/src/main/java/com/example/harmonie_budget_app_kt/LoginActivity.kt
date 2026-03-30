package com.example.harmonie_budget_app_kt

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.utils.JsonHelper

class LoginActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etUsername: EditText = findViewById(R.id.et_username)
        val etPassword: EditText = findViewById(R.id.et_password)
        val btnLogIn: Button = findViewById(R.id.btn_log_in)

        btnLogIn.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty())
            {
                val user = JsonHelper.loadUser(this, username)
                if (user != null && user.password == password)
                {
                    val intent = Intent(this, DashboardActivity::class.java)
                    intent.putExtra("username", username)
                    startActivity(intent)
                    finish()
                }
                else
                {
                    Toast.makeText(this, "Invalid username or password", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            }
        }
    }
}