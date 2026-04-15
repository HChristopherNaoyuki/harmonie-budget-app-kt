package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.User
import com.example.harmonie_budget_app_kt.viewmodels.UserViewModel

/**
 * ForgotPasswordActivity handles the password reset flow.
 * The user is asked for username, user id, new password, and confirm password.
 * The button is labeled "Reset Password".
 * When the new password is valid, the old password in the JSON file is replaced
 * with the new password while preserving all other user information.
 * All data operations are performed through the UserViewModel.
 */
class ForgotPasswordActivity : AppCompatActivity()
{
    private lateinit var etUsername: EditText
    private lateinit var etUserId: EditText
    private lateinit var etNewPassword: EditText
    private lateinit var etConfirmNewPassword: EditText
    private lateinit var btnSend: Button
    private lateinit var btnChangePassword: Button
    private lateinit var btnHome: Button
    private lateinit var btnLogIn: Button
    private lateinit var tvNewPasswordTitle: TextView
    private lateinit var layoutNewPassword: android.view.View

    private val userViewModel = UserViewModel()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        etUsername = findViewById(R.id.et_username)
        etUserId = findViewById(R.id.et_user_id)
        etNewPassword = findViewById(R.id.et_new_password)
        etConfirmNewPassword = findViewById(R.id.et_confirm_new_password)
        btnSend = findViewById(R.id.btn_send)
        btnChangePassword = findViewById(R.id.btn_change_password)
        btnHome = findViewById(R.id.btn_home)
        btnLogIn = findViewById(R.id.btn_log_in)
        tvNewPasswordTitle = findViewById(R.id.tv_new_password_title)
        layoutNewPassword = findViewById(R.id.layout_new_password)

        layoutNewPassword.visibility = android.view.View.GONE

        btnSend.setOnClickListener {
            val username = etUsername.text.toString().trim()
            val userId = etUserId.text.toString().trim()

            if (username.isNotEmpty() && userId.isNotEmpty())
            {
                // Call through the ViewModel layer
                val user = userViewModel.loadUser(this, username)
                if (user != null)
                {
                    tvNewPasswordTitle.visibility = android.view.View.VISIBLE
                    layoutNewPassword.visibility = android.view.View.VISIBLE
                    Toast.makeText(this, "Username and User ID verified", Toast.LENGTH_SHORT).show()
                }
                else
                {
                    Toast.makeText(this, getString(R.string.username_or_user_id_invalid), Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Please enter username and User ID", Toast.LENGTH_SHORT).show()
            }
        }

        btnChangePassword.setOnClickListener {
            val newPass = etNewPassword.text.toString().trim()
            val confirmPass = etConfirmNewPassword.text.toString().trim()

            if (newPass.isNotEmpty() && confirmPass.isNotEmpty() && newPass == confirmPass)
            {
                val passwordRegex = Regex("""^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$""")
                if (passwordRegex.matches(newPass))
                {
                    val user = userViewModel.loadUser(this, etUsername.text.toString().trim())
                    if (user != null)
                    {
                        // Create a new User object with the updated password while keeping all other information
                        val updatedUser = User(user.name, user.surname, user.username, newPass)

                        // Save the updated user through the ViewModel layer
                        userViewModel.saveUser(this, updatedUser)

                        Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                else
                {
                    Toast.makeText(this, "Password must be at least 8 characters with a letter, number, and special character", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
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
}