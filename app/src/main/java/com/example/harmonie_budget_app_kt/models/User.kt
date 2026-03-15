// app/kotlin+java/com.example.harmonie_budget_app_kt/models/User.kt
package com.example.harmonie_budget_app_kt.models

/**
 * User model for registration and login.
 * Now includes name and surname as required for the register form.
 * All user data is saved to users.json inside the budget_data folder.
 * Password is stored in plain text for this prototype only.
 */
data class User(
    val name: String,
    val surname: String,
    val username: String,
    val password: String
)