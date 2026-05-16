package com.example.harmonie_budget_app_kt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.harmonie_budget_app_kt.utils.JsonHelper
import com.example.harmonie_budget_app_kt.viewmodels.UserViewModel

class LoginActivity : AppCompatActivity()
{
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button
    private lateinit var btnReturnHome: Button

    private val userViewModel = UserViewModel()

    companion object
    {
        private const val PREFS_NAME = "harmonie_prefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_LOGGED_IN_USERNAME = "logged_in_username"
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val sharedPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        if (sharedPrefs.getBoolean(KEY_IS_LOGGED_IN, false))
        {
            val savedUsername = sharedPrefs.getString(KEY_LOGGED_IN_USERNAME, "") ?: ""
            if (savedUsername.isNotEmpty())
            {
                val intent = Intent(this, DashboardActivity::class.java)
                intent.putExtra("username", savedUsername)
                startActivity(intent)
                finish()
                return
            }
        }

        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        btnLogin = findViewById(R.id.btn_log_in)
        btnRegister = findViewById(R.id.btn_register)
        btnReturnHome = findViewById(R.id.btn_return_home)

        btnReturnHome.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        btnLogin.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()

            if (username.isNotEmpty() && password.isNotEmpty())
            {
                val user = userViewModel.loadUser(this, username)

                if (user != null)
                {
                    val isPasswordValid = JsonHelper.verifyPassword(password, user.password)

                    if (isPasswordValid)
                    {
                        sharedPrefs.edit {
                            putBoolean(KEY_IS_LOGGED_IN, true)
                            putString(KEY_LOGGED_IN_USERNAME, username)
                        }

                        val intent = Intent(this, DashboardActivity::class.java)
                        intent.putExtra("username", username)
                        startActivity(intent)
                        finish()
                    }
                    else
                    {
                        Toast.makeText(this, getString(R.string.error_invalid_credentials), Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {
                    Toast.makeText(this, getString(R.string.error_invalid_credentials), Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_SHORT).show()
            }
        }

        btnRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}