// app/kotlin+java/com/example/harmonie_budget_app_kt/models/StreakData.kt
package com.example.harmonie_budget_app_kt.models

/**
 * StreakData model for tracking consecutive days of expense logging.
 * Used for gamification badge awarding and progress display.
 *
 * @param currentStreak Number of consecutive days with at least one expense
 * @param longestStreak Highest streak achieved so far
 * @param lastExpenseDate Date of the most recent expense (yyyy-MM-dd)
 */
data class StreakData
    (
    val currentStreak: Int,
    val longestStreak: Int,
    val lastExpenseDate: String
)