package com.example.harmonie_budget_app_kt.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.harmonie_budget_app_kt.models.User
import com.example.harmonie_budget_app_kt.utils.JsonHelper

/**
 * UserViewModel manages data and logic for user-related screens (login, register, forgot password).
 * It calls JsonHelper methods to load and save user data.
 * This follows the MVVM pattern by separating UI logic from data access.
 * All calls to JsonHelper are now routed through this ViewModel.
 */
class UserViewModel : ViewModel()
{
    private val jsonHelper = JsonHelper()

    /**
     * Saves a new user.
     * Context is required by JsonHelper.
     */
    fun saveUser(context: Context, user: User)
    {
        jsonHelper.saveUser(context, user)
    }

    /**
     * Loads a user by username.
     * Context is required by JsonHelper.
     */
    fun loadUser(context: Context, username: String): User?
    {
        return jsonHelper.loadUser(context, username)
    }
}