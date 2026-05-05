// app/kotlin+java/com/example/harmonie_budget_app_kt/viewmodels/GamificationViewModel.kt
package com.example.harmonie_budget_app_kt.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.harmonie_budget_app_kt.models.Badge
import com.example.harmonie_budget_app_kt.models.StreakData
import com.example.harmonie_budget_app_kt.utils.JsonHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * GamificationViewModel manages badge and streak data for the application.
 * It calls JsonHelper methods to load and save badges and streak information.
 * This follows the MVVM pattern by separating UI logic from data access.
 *
 * Part 3 requirement: Gamification elements such as rewards or badges
 * for meeting budget goals or consistent expense logging.
 */
class GamificationViewModel : ViewModel()
{
    private val jsonHelper = JsonHelper()
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    /**
     * Retrieves all badges earned by the user.
     *
     * @param context Application context for file access
     * @param username User identifier for data isolation
     * @return List of Badge objects earned by the user
     */
    fun getBadges(context: Context, username: String): List<Badge>
    {
        return jsonHelper.loadBadges(context, username)
    }

    /**
     * Saves a new badge for the user.
     * Prevents duplicate badges of the same type.
     *
     * @param context Application context for file access
     * @param username User identifier for data isolation
     * @param badge Badge to save
     * @return true if badge was saved (new), false if already exists
     */
    fun saveBadge(context: Context, username: String, badge: Badge): Boolean
    {
        val existingBadges = jsonHelper.loadBadges(context, username)
        val alreadyHasBadge = existingBadges.any { it.id == badge.id }

        if (alreadyHasBadge)
        {
            return false
        }

        jsonHelper.saveBadge(context, username, badge)
        return true
    }

    /**
     * Retrieves the user's current streak data.
     *
     * @param context Application context for file access
     * @param username User identifier for data isolation
     * @return StreakData object, or null if no streak data exists
     */
    fun getStreakData(context: Context, username: String): StreakData?
    {
        return jsonHelper.loadStreakData(context, username)
    }

    /**
     * Updates the user's expense logging streak based on today's date.
     * Called after each expense is saved.
     *
     * @param context Application context for file access
     * @param username User identifier for data isolation
     * @return Updated StreakData object
     */
    fun updateStreak(context: Context, username: String): StreakData
    {
        val today = dateFormat.format(Date())
        val existingStreak = jsonHelper.loadStreakData(context, username)

        val updatedStreak = if (existingStreak == null)
        {
            // First expense ever recorded
            StreakData(currentStreak = 1, longestStreak = 1, lastExpenseDate = today)
        }
        else
        {
            val lastDate = parseDate(existingStreak.lastExpenseDate)
            val currentDate = parseDate(today)
            val daysDifference = daysBetween(lastDate, currentDate)

            val newStreak = if (daysDifference == 1)
            {
                // Consecutive day: increment streak
                existingStreak.currentStreak + 1
            }
            else if (daysDifference == 0)
            {
                // Same day: no change to streak
                existingStreak.currentStreak
            }
            else
            {
                // Gap of more than one day: reset streak to 1
                1
            }

            val newLongestStreak = maxOf(newStreak, existingStreak.longestStreak)

            StreakData(
                currentStreak = newStreak,
                longestStreak = newLongestStreak,
                lastExpenseDate = today
            )
        }

        jsonHelper.saveStreakData(context, username, updatedStreak)

        // Check and award streak-based badges
        checkAndAwardStreakBadges(context, username, updatedStreak.currentStreak)

        return updatedStreak
    }

    /**
     * Awards badges when the user achieves specific streak milestones.
     * Milestones: 3 days, 7 days, 14 days, 30 days.
     *
     * @param context Application context for file access
     * @param username User identifier for data isolation
     * @param currentStreak The user's current consecutive day streak
     */
    private fun checkAndAwardStreakBadges(context: Context, username: String, currentStreak: Int)
    {
        val today = dateFormat.format(Date())

        val streakBadges = listOf(
            Badge(1, "Consistent Starter", "Logged expenses for 3 days in a row", today),
            Badge(2, "Weekly Warrior", "Logged expenses for 7 days in a row", today),
            Badge(3, "Discipline Master", "Logged expenses for 14 days in a row", today),
            Badge(4, "Monthly Master", "Logged expenses for 30 days in a row", today)
        )

        val milestones = listOf(3, 7, 14, 30)

        for (i in milestones.indices)
        {
            if (currentStreak >= milestones[i])
            {
                saveBadge(context, username, streakBadges[i])
            }
        }
    }

    /**
     * Awards budget goal badges when the user successfully stays within their monthly budget.
     *
     * @param context Application context for file access
     * @param username User identifier for data isolation
     * @param totalSpent Total amount spent in the current month
     * @param maxGoal Maximum monthly budget goal
     */
    fun checkAndAwardBudgetBadge(context: Context, username: String, totalSpent: Double, maxGoal: Double)
    {
        val today = dateFormat.format(Date())

        // Budget Goal Achieved badge: spent less than or equal to maxGoal
        if (totalSpent <= maxGoal && maxGoal > 0)
        {
            val budgetBadge = Badge(5, "Budget Champion", "Stayed within monthly budget", today)
            saveBadge(context, username, budgetBadge)
        }
    }

    /**
     * Parses a date string into a Date object.
     *
     * @param dateString Date in yyyy-MM-dd format
     * @return Date object, or null if parsing fails
     */
    private fun parseDate(dateString: String): Date?
    {
        return try
        {
            dateFormat.parse(dateString)
        }
        catch (e: Exception)
        {
            null
        }
    }

    /**
     * Calculates the number of days between two dates.
     *
     * @param start Starting date
     * @param end Ending date
     * @return Number of days between the two dates
     */
    private fun daysBetween(start: Date?, end: Date?): Int
    {
        if (start == null || end == null)
        {
            return 0
        }

        val diffMillis = end.time - start.time
        return (diffMillis / (1000 * 60 * 60 * 24)).toInt()
    }
}