// app/kotlin+java/com.example.harmonie_budget_app_kt/models/Category.kt
package com.example.harmonie_budget_app_kt.models

/**
 * Category model for expense grouping.
 * id is auto-generated.
 * name is user-entered.
 * Stored in categories.json inside the budget_data folder.
 */
data class Category(
    val id: Int,
    val name: String
)