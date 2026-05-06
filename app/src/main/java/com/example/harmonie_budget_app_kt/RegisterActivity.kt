package com.example.harmonie_budget_app_kt

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
 * Security Enhancement:
 * Passwords are passed to UserViewModel.saveUser() which hashes them via JsonHelper.
 * The plaintext password is never stored permanently.
 *
 * Crash Fixes:
 * Added try-catch blocks around file operations to prevent crashes.
 * Added validation for all fields and password strength requirements.
 *
 * Code Style:
 * Lambda expressions have opening braces on the same line as the function call,
 * as required by Kotlin syntax. Method bodies use Allman style (braces on new lines).
 */
class RegisterActivity : AppCompatActivity()
{
    private lateinit var etName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnGenerateUserId: Button
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
        tvGeneratedUserId = findViewById(R.id.tv_generated_user_id)
        tvAlreadyRegistered = findViewById(R.id.tv_already_registered)

        // Navigate to Login screen if user already has an account.
        // Note: Opening brace on same line as setOnClickListener is correct Kotlin lambda syntax.
        tvAlreadyRegistered.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Generate a unique User ID based on username and current timestamp.
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

        // Create new user account with validation.
        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            // Validate all fields are filled.
            if (name.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty())
            {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
            // Validate password match.
            else if (password != confirmPassword)
            {
                Toast.makeText(this, getString(R.string.error_password_mismatch), Toast.LENGTH_SHORT).show()
            }
            // Validate password strength (at least 8 characters with letter, number, special char).
            else
            {
                val passwordRegex = Regex("""^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$""")

                if (!passwordRegex.matches(password))
                {
                    Toast.makeText(this, getString(R.string.error_password_requirements), Toast.LENGTH_SHORT).show()
                }
                // Check if username already exists.
                else
                {
                    val existingUser = userViewModel.loadUser(this, username)

                    if (existingUser != null)
                    {
                        Toast.makeText(this, getString(R.string.error_username_taken), Toast.LENGTH_SHORT).show()
                    }
                    // Ensure User ID has been generated.
                    else if (generatedUserId.isEmpty())
                    {
                        Toast.makeText(this, "Please generate a User ID first", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        // Create User object with all required fields.
                        // Note: The surname field is set to an empty string as it is not used in this version.
                        val user = User(
                            name = name,
                            surname = "",
                            username = username,
                            password = password,
                            userId = generatedUserId
                        )

                        // Save the user with try-catch to prevent crashes.
                        try
                        {
                            userViewModel.saveUser(this, user)
                            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                        catch (exception: Exception)
                        {
                            // The underscore prefix indicates the parameter is intentionally unused.
                            // In a production app, this exception would be logged.
                            Toast.makeText(this, "Account creation failed. Please try again.", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }
        }
    }

    /**
     * Generates a unique User ID.
     * Format: PREFIX(4) + DATE(8) + COUNTER(4) = 16 characters total.
     *
     * The prefix is derived from the first 4 characters of the username,
     * uppercased and padded with 'X' if shorter than 4 characters.
     * The date part uses the current UTC date in yyyyMMdd format.
     * The counter uses the last 4 digits of current time in milliseconds.
     *
     * @param username The username entered by the user
     * @return A 16-character unique User ID
     */
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