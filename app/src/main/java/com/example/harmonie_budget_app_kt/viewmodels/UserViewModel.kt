package com.example.harmonie_budget_app_kt.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.harmonie_budget_app_kt.models.User
import com.example.harmonie_budget_app_kt.utils.JsonHelper
import java.io.IOException

/**
 * UserViewModel manages data and logic for user-related screens (login, register, forgot password).
 * It calls JsonHelper methods to load and save user data.
 * This follows the MVVM pattern by separating UI logic from data access.
 *
 * Error Handling:
 * Added detailed logging and specific exception types for better error diagnosis.
 */
class UserViewModel : ViewModel()
{
    companion object
    {
        private const val TAG = "UserViewModel"
    }

    private val jsonHelper = JsonHelper()

    /**
     * Saves a new user. The password is hashed by JsonHelper.
     *
     * @param context Application context for file access
     * @param user User object to save (plaintext password will be hashed)
     * @throws IOException If file operations fail
     * @throws RuntimeException If hashing or serialization fails
     */
    @Throws(IOException::class, RuntimeException::class)
    fun saveUser(context: Context, user: User)
    {
        Log.d(TAG, "saveUser called for username: ${user.username}")

        try
        {
            jsonHelper.saveUser(context, user)
            Log.d(TAG, "saveUser completed successfully for username: ${user.username}")
        }
        catch (exception: IOException)
        {
            Log.e(TAG, "IO Exception while saving user: ${user.username}", exception)
            throw exception
        }
        catch (exception: RuntimeException)
        {
            Log.e(TAG, "Runtime Exception while saving user: ${user.username}", exception)
            throw exception
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Unexpected exception while saving user: ${user.username}", exception)
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
            Log.e(TAG, "Exception while loading user: $username", exception)
            null
        }
    }
}