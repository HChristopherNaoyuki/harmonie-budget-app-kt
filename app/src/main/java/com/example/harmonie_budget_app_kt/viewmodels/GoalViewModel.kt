package com.example.harmonie_budget_app_kt.viewmodels

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
     * This method is called from GoalActivity to load existing goals.
     */
    fun getGoal(username: String): Goal?
    {
        return jsonHelper.loadGoal(/* context not needed in this call pattern */)
    }

    /**
     * Saves the goal for the user.
     * This method is called from GoalActivity when the user saves goals.
     */
    fun saveGoal(username: String, goal: Goal)
    {
        jsonHelper.saveGoal(/* context not needed in this call pattern */, username, goal)
    }
}