// app/kotlin+java/com.example.harmonie_budget_app_kt/models/Category.kt
package com.example.harmonie_budget_app_kt.models

/**
 * Category model for expense grouping (e.g., Groceries, Transport).
 * ID is auto-incremented for uniqueness.
 */
data class Category(
    val id: Int,
    val name: String
)