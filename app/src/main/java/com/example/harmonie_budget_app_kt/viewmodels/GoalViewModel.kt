package com.example.harmonie_budget_app_kt.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.harmonie_budget_app_kt.db.DatabaseRepository
import com.example.harmonie_budget_app_kt.models.Goal

/**
 * GoalViewModel manages data and business logic for goal operations.
 *
 * It uses DatabaseRepository to perform direct SQLite database reads and writes.
 */
class GoalViewModel : ViewModel()
{
    /**
     * Retrieves the spending goals for the specified user from the database.
     */
    fun getGoal(context: Context, username: String): Goal?
    {
        val repository = DatabaseRepository(context)
        return repository.getGoal(username)
    }

    /**
     * Saves the spending goals for the user into the database repository.
     */
    fun saveGoal(context: Context, username: String, goal: Goal): Boolean
    {
        val repository = DatabaseRepository(context)
        return repository.saveGoal(username, goal)
    }
}