package com.example.harmonie_budget_app_kt.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.harmonie_budget_app_kt.models.Goal
import com.example.harmonie_budget_app_kt.utils.JsonHelper

/**
 * GoalViewModel manages data and business logic for goal operations.
 *
 * It serves as an intermediary between the user interface and the local JSON data layer.
 * All operations are delegated to JsonHelper to perform atomic operations on internal storage.
 */
class GoalViewModel : ViewModel()
{
    private val jsonHelper = JsonHelper()

    /**
     * Retrieves the spending goals for the specified user.
     *
     * @param context Application context required for file access
     * @param username Unique identifier of the logged-in user
     * @return Goal model instance, or null if no goal exists
     */
    fun getGoal(context: Context, username: String): Goal?
    {
        return jsonHelper.loadGoal(context, username)
    }

    /**
     * Saves the spending goals for the user.
     *
     * @param context Application context required for file access
     * @param username Unique identifier of the logged-in user
     * @param goal Goal model instance to persist
     * @return True if the goal was saved successfully, false otherwise
     */
    fun saveGoal(context: Context, username: String, goal: Goal): Boolean
    {
        return jsonHelper.saveGoal(context, username, goal)
    }
}