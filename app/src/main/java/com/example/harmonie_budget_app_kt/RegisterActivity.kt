package com.example.harmonie_budget_app_kt

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.User
import com.example.harmonie_budget_app_kt.viewmodels.UserViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class RegisterActivity : AppCompatActivity()
{
    private lateinit var etName: EditText
    private lateinit var etUsername: EditText
    private lateinit var etPassword: EditText
    private lateinit var etConfirmPassword: EditText
    private lateinit var btnRegister: Button
    private lateinit var btnHome: Button
    private lateinit var btnLogIn: Button
    private lateinit var tvGeneratedUserId: TextView
    private lateinit var btnCopyUserId: Button

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
        btnHome = findViewById(R.id.btn_home)
        btnLogIn = findViewById(R.id.btn_log_in)
        tvGeneratedUserId = findViewById(R.id.tv_generated_user_id)
        btnCopyUserId = findViewById(R.id.btn_copy_user_id)

        btnRegister.setOnClickListener {
            val name = etName.text.toString().trim()
            val username = etUsername.text.toString().trim()
            val password = etPassword.text.toString().trim()
            val confirmPassword = etConfirmPassword.text.toString().trim()

            if (name.isEmpty() || username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty())
            {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword)
            {
                Toast.makeText(this, getString(R.string.error_password_mismatch), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (userViewModel.loadUser(this, username) != null)
            {
                Toast.makeText(this, getString(R.string.error_username_taken), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Generate User ID before saving
            generatedUserId = generateUserId(username)

            tvGeneratedUserId.text = generatedUserId

            val user = User(name, "", username, password)

            userViewModel.saveUser(this, user)

            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
            finish()
        }

        btnCopyUserId.setOnClickListener {
            if (generatedUserId.isNotEmpty())
            {
                val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val clip = ClipData.newPlainText("User ID", generatedUserId)
                clipboard.setPrimaryClip(clip)
                Toast.makeText(this, getString(R.string.user_id_copied), Toast.LENGTH_SHORT).show()
            }
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

    /**
     * Generates User ID in format AAAAYYYYMMDDNNNN
     * AAAA = first 4 characters of username (uppercase, padded with X)
     * YYYYMMDD = UTC date of creation
     * NNNN = daily increment counter (for prototype, use 0001)
     */
    private fun generateUserId(username: String): String
    {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePart = String.format(Locale.getDefault(), "%04d%02d%02d", year, month, day)

        val prefix = username.take(4).uppercase(Locale.getDefault()).padEnd(4, 'X')

        // For this prototype the daily counter is fixed at 0001
        val counter = "0001"

        return prefix + datePart + counter
    }
}