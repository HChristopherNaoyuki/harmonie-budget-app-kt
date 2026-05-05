// app/kotlin+java/com/example/harmonie_budget_app_kt/models/Badge.kt
package com.example.harmonie_budget_app_kt.models

/**
 * Badge model for gamification elements.
 * Tracks achievements earned by the user.
 * Stored in badges.json inside the budget_data folder.
 *
 * Part 3 requirement: Gamification elements such as rewards or badges
 * for meeting budget goals or consistent expense logging.
 *
 * @param id Unique identifier for the badge type
 * @param name Display name of the badge
 * @param description Detailed description of how the badge was earned
 * @param earnedDate ISO date string when the badge was earned (yyyy-MM-dd)
 * @param iconResource Optional resource identifier for future icon display
 */
data class Badge
    (
    val id: Int,
    val name: String,
    val description: String,
    val earnedDate: String,
    val iconResource: Int = 0
)