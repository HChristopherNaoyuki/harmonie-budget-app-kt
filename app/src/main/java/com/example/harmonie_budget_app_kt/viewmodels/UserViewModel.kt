package com.example.harmonie_budget_app_kt.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.harmonie_budget_app_kt.models.User
import com.example.harmonie_budget_app_kt.utils.JsonHelper

/**
 * UserViewModel manages data and logic for user-related screens (login, register, forgot password).
 * It calls JsonHelper methods to load and save user data.
 * This follows the MVVM pattern by separating UI logic from data access.
 *
 * Crash Fix:
 * Added exception handling in saveUser to prevent crashes from file system errors.
 */
class UserViewModel : ViewModel()
{
    private val jsonHelper = JsonHelper()

    /**
     * Saves a new user. The password is hashed by JsonHelper.
     * Exceptions are caught and re-thrown as RuntimeExceptions to be handled by the calling Activity.
     *
     * @param context Application context for file access
     * @param user User object to save (plaintext password will be hashed)
     * @throws RuntimeException If file operations fail
     */
    fun saveUser(context: Context, user: User)
    {
        try
        {
            jsonHelper.saveUser(context, user)
        }
        catch (exception: Exception)
        {
            // The underscore prefix indicates the parameter is intentionally unused.
            // In a production app, this exception would be logged.
            throw RuntimeException("Failed to save user account", exception)
        }
    }

    /**
     * Loads a user by username.
     * Returns null if the user does not exist.
     *
     * @param context Application context for file access
     * @param username Username to look up
     * @return User object if found, null otherwise
     */
    fun loadUser(context: Context, username: String): User?
    {
        return try
        {
            jsonHelper.loadUser(context, username)
        }
        catch (exception: Exception)
        {
            // The underscore prefix indicates the parameter is intentionally unused.
            // In a production app, this exception would be logged.
            null
        }
    }
}