package com.example.harmonie_budget_app_kt.models

/**
 * User data class.
 * This class holds the details entered during registration plus the
 * auto-generated User ID.
 * The userId field is now included so that the exact ID shown during
 * registration is stored in the JSON file and can be displayed on the
 * Home dashboard.
 * Gson automatically serializes and deserializes this field.
 * Default value is empty string to maintain backward compatibility with
 * any previously saved users during development.
 */
data class User(
    val name: String,
    val surname: String,
    val username: String,
    val password: String,
    val userId: String = ""
)