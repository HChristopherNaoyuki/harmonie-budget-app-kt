package com.example.harmonie_budget_app_kt.models

/**
 * User data class.
 * This class holds the details entered during registration.
 * Data is saved per username in a separate JSON file inside the budget_data folder.
 * This ensures isolation: only the logged-in user can see their own budget data.
 * The admin account is the only hardcoded exception for testing.
 */
data class User(
    val name: String,
    val surname: String,
    val username: String,
    val password: String
)