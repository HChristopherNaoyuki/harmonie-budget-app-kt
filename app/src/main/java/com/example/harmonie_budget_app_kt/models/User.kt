// app/kotlin+java/com.example.harmonie_budget_app_kt/models/User.kt
package com.example.harmonie_budget_app_kt.models

/**
 * Simple User model for login.
 * In a production app, password would be hashed (e.g., bcrypt).
 * For this prototype, plain text is used as per assignment scope.
 */
data class User(
    val username: String,
    val password: String
)