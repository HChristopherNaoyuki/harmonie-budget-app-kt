// app/kotlin+java/com.example.harmonie_budget_app_kt/models/Goal.kt
package com.example.harmonie_budget_app_kt.models

/**
 * Monthly spending goal model (min and max total).
 * For prototype, global monthly goal; per-category limits can be extended later.
 */
data class Goal(
    val minMonthly: Double,
    val maxMonthly: Double
)