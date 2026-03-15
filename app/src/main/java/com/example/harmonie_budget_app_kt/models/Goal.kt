// app/kotlin+java/com.example.harmonie_budget_app_kt/models/Goal.kt
package com.example.harmonie_budget_app_kt.models

/**
 * Monthly goal model.
 * Stored as single object in goal.json inside the budget_data folder.
 */
data class Goal(
    val minMonthly: Double,
    val maxMonthly: Double
)