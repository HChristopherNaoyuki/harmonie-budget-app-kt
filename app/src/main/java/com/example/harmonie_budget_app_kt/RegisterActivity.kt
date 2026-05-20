package com.example.harmonie_budget_app_kt

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.User
import com.example.harmonie_budget_app_kt.viewmodels.UserViewModel
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * RegisterActivity handles new user account creation.
 *
 * Part 3 Enhancement: Added back arrow navigation to return to the Landing Page.
 */
class RegisterActivity : AppCompatActivity()
{
    companion object
    {
        private const val TAG = "RegisterActivity"
    }

    private lateinit var etName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnGenerateUserId: Button
    private lateinit var btnReturnHome: Button
    private lateinit var ivBackArrow: TextView
    private lateinit var tvGeneratedUserId: TextView
    private lateinit var tvAlreadyRegistered: TextView
    private val userViewModel = UserViewModel()
    private var generatedUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        etName = findViewById(R.id.et_name)
        etUsername = findViewById(R.id.et_username)
        etPassword = findViewById(R.id.et_password)
        etConfirmPassword = findViewById(R.id.et_confirm_password)
        btnRegister = findViewById(R.id.btn_register)
        btnGenerateUserId = findViewById(R.id.btn_generate_user_id)
        btnReturnHome = findViewById(R.id.btn_return_home)
        ivBackArrow = findViewById(R.id.iv_back_arrow)
        tvGeneratedUserId = findViewById(R.id.tv_generated_user_id)
        tvAlreadyRegistered = findViewById(R.id.tv_already_registered)

        // Back arrow navigation to return to the Landing Page (MainActivity)
        ivBackArrow.setOnClickListener {
            navigateToLandingPage()
        }

        btnReturnHome.setOnClickListener {
            navigateToLandingPage()
        }

        // Navigate to Login screen if user already has an account
        tvAlreadyRegistered.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Generate a unique User ID based on username and current timestamp
        btnGenerateUserId.setOnClickListener {
            val username = etUsername.text.toString().trim()

            if (username.isNotEmpty())
            {
                generatedUserId = generateUserId(username)
                tvGeneratedUserId.text = generatedUserId

                val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("User ID", generatedUserId)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, "User ID generated and copied to clipboard", Toast.LENGTH_SHORT).show()
            }
            else
            {
                Toast.makeText(this, "Please enter a username first", Toast.LENGTH_SHORT).show()
            }
        }

        // Create new user account with validation
        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            Log.d(TAG, "Attempting to create account for username: $username")

            if (name.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty())
            {
                Log.w(TAG, "Account creation failed: Empty fields")
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
            else if (password != confirmPassword)
            {
                Log.w(TAG, "Account creation failed: Password mismatch")
                Toast.makeText(this, getString(R.string.error_password_mismatch), Toast.LENGTH_SHORT).show()
            }
            else if (!isValidFullName(name))
            {
                Log.w(TAG, "Account creation failed: Invalid full name: $name")
                Toast.makeText(this, "Please enter your full name (first name and surname)", Toast.LENGTH_SHORT).show()
            }
            else
            {
                val passwordRegex = Regex("""^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$""")

                if (!passwordRegex.matches(password))
                {
                    Log.w(TAG, "Account creation failed: Weak password")
                    Toast.makeText(this, getString(R.string.error_password_requirements), Toast.LENGTH_SHORT).show()
                }
                else
                {
                    val existingUser = userViewModel.loadUser(this, username)

                    if (existingUser != null)
                    {
                        Log.w(TAG, "Account creation failed: Username already taken: $username")
                        Toast.makeText(this, getString(R.string.error_username_taken), Toast.LENGTH_SHORT).show()
                    }
                    else if (generatedUserId.isEmpty())
                    {
                        Log.w(TAG, "Account creation failed: User ID not generated")
                        Toast.makeText(this, "Please generate a User ID first", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        val user = User(
                            name = name,
                            surname = "",
                            username = username,
                            password = password,
                            userId = generatedUserId
                        )

                        Log.d(TAG, "User object created. Username: $username, UserId: $generatedUserId")

                        try
                        {
                            userViewModel.saveUser(this, user)
                            Log.i(TAG, "Account created successfully for username: $username")
                            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        catch (exception: Exception)
                        {
                            Log.e(TAG, "Account creation failed for username: $username", exception)
                            val errorMessage = when (exception.message)
                            {
                                null -> "Account creation failed. Please try again."
                                else -> "Account creation failed: ${exception.message}"
                            }
                            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    /**
     * Navigates back to the Landing Page (MainActivity).
     * Clears the back stack to prevent returning to the register screen.
     */
    private fun navigateToLandingPage()
    {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun isValidFullName(fullName: String): Boolean
    {
        val trimmed = fullName.trim().replace(Regex("\\s+"), " ")

        if (trimmed.isEmpty())
        {
            return false
        }

        val nameParts = trimmed.split(" ").filter { it.isNotEmpty() }

        return nameParts.size >= 2
    }

    private fun generateUserId(username: String): String
    {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePart = String.format(Locale.US, "%04d%02d%02d", year, month, day)
        val prefix = username.take(4).uppercase(Locale.US).padEnd(4, 'X')
        val counter = String.format(Locale.US, "%04d", System.currentTimeMillis() % 10000)
        return prefix + datePart + counter
    }
}