// app/kotlin+java/com.example.harmonie_budget_app_kt/models/Expense.kt
package com.example.harmonie_budget_app_kt.models

/**
 * Expense model updated to include startTime and endTime as required.
 * All fields are saved to expenses.json in the budget_data folder.
 * Gson handles serialization automatically.
 */
data class Expense(
    val id: Int,
    val amount: Double,
    val date: String,
    val startTime: String,
    val endTime: String,
    val description: String,
    val categoryId: Int,
    val photoUri: String? = null
)