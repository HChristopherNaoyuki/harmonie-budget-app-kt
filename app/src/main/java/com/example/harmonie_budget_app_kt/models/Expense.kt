// app/kotlin+java/com.example.harmonie_budget_app_kt/models/Expense.kt
package com.example.harmonie_budget_app_kt.models

/**
 * Expense model for budget tracking.
 * Supports optional photo URI for receipt.
 * Stored in expenses.json inside the budget_data folder.
 */
data class Expense(
    val id: Int,
    val amount: Double,
    val date: String,
    val description: String,
    val categoryId: Int,
    val photoUri: String? = null
)