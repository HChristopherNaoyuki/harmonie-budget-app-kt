package com.example.harmonie_budget_app_kt.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.harmonie_budget_app_kt.models.Goal
import com.example.harmonie_budget_app_kt.utils.JsonHelper

/**
 * GoalViewModel manages data and logic for goal-related screens.
 * It calls JsonHelper methods to load and save goals.
 * This follows the MVVM pattern by separating UI logic from data access.
 * All calls to JsonHelper are now routed through this ViewModel.
 */
class GoalViewModel : ViewModel()
{
    private val jsonHelper = JsonHelper()

    /**
     * Returns the goal for the given user.
     * Context is required by JsonHelper.
     */
    fun getGoal(context: Context, username: String): Goal?
    {
        return jsonHelper.loadGoal(context, username)
    }

    /**
     * Saves the goal for the user.
     * Context is required by JsonHelper.
     */
    fun saveGoal(context: Context, username: String, goal: Goal)
    {
        jsonHelper.saveGoal(context, username, goal)
    }
}