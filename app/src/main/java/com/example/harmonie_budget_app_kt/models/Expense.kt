// app/kotlin+java/com.example.harmonie_budget_app_kt/models/Expense.kt
package com.example.harmonie_budget_app_kt.models

/**
 * Expense entry model.
 * Includes amount, date (yyyy-MM-dd), description, category link, and optional photo URI string.
 * Start/end times from assignment were interpreted as optional duration notes; simplified to date for prototype.
 */
data class Expense(
    val id: Int,
    val amount: Double,
    val date: String,
    val description: String,
    val categoryId: Int,
    val photoUri: String? = null
)