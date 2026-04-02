package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.utils.JsonHelper

class ForgotPasswordActivity : AppCompatActivity()
{
    private lateinit var etUsername: android.widget.EditText
    private lateinit var btnSend: Button
    private lateinit var layoutNewPassword: LinearLayout
    private lateinit var etNewPassword: android.widget.EditText
    private lateinit var etConfirmNewPassword: android.widget.EditText
    private lateinit var btnChangePassword: Button
    private lateinit var btnHome: Button
    private lateinit var btnLogIn: Button

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        etUsername = findViewById(R.id.et_username)
        btnSend = findViewById(R.id.btn_send)
        layoutNewPassword = findViewById(R.id.layout_new_password)
        etNewPassword = findViewById(R.id.et_new_password)
        etConfirmNewPassword = findViewById(R.id.et_confirm_new_password)
        btnChangePassword = findViewById(R.id.btn_change_password)
        btnHome = findViewById(R.id.btn_home)
        btnLogIn = findViewById(R.id.btn_log_in)

        btnSend.setOnClickListener {
            val username = etUsername.text.toString().trim()
            if (username.isNotEmpty())
            {
                val user = JsonHelper.loadUser(this, username)
                if (user != null)
                {
                    layoutNewPassword.visibility = android.view.View.VISIBLE
                    Toast.makeText(this, "Username found. Enter new password.", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(this, "Username not found", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Please enter your username", Toast.LENGTH_SHORT).show()
            }
        }

        btnChangePassword.setOnClickListener {
            val newPass = etNewPassword.text.toString().trim()
            val confirmPass = etConfirmNewPassword.text.toString().trim()

            if (newPass.isNotEmpty() && confirmPass.isNotEmpty())
            {
                if (newPass == confirmPass)
                {
                    if (newPass.length >= 8 &&
                        newPass.matches(Regex(".*[a-zA-Z].*")) &&
                        newPass.matches(Regex(".*[0-9].*")) &&
                        newPass.matches(Regex(".*[!@#\$%^&*].*")))
                    {
                        Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    else
                    {
                        Toast.makeText(this, "Password must meet the requirements", Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {
                    Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Please enter and confirm the new password", Toast.LENGTH_SHORT).show()
            }
        }

        btnHome.setOnClickListener {
            finish()
        }

        btnLogIn.setOnClickListener {
            finish()
        }
    }
}