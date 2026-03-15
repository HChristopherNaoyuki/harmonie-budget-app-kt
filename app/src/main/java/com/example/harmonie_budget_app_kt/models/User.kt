// app/kotlin+java/com.example.harmonie_budget_app_kt/models/User.kt
package com.example.harmonie_budget_app_kt.models

/**
 * User model for login and registration.
 * Stored in users.json inside the budget_data folder.
 * Simple data class - Gson handles serialization.
 */
data class User(
    val username: String,
    val password: String
)