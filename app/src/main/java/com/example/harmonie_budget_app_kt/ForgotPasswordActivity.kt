package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ForgotPasswordActivity : AppCompatActivity()
{
    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        val etUsername: EditText = findViewById(R.id.et_username)
        val btnSend: Button = findViewById(R.id.btn_send)

        btnSend.setOnClickListener {
            val username = etUsername.text.toString().trim()
            if (username.isNotEmpty())
            {
                Toast.makeText(this, "Password reset link sent (demo)", Toast.LENGTH_SHORT).show()
                finish()
            }
            else
            {
                Toast.makeText(this, "Please enter username", Toast.LENGTH_SHORT).show()
            }
        }
    }
}