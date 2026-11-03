package com.example.harmonie_budget_app_kt.viewmodels

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.harmonie_budget_app_kt.auth.SsoManager
import com.example.harmonie_budget_app_kt.db.DatabaseRepository
import com.example.harmonie_budget_app_kt.models.User
import java.io.IOException

/**
 * UserViewModel manages data and business logic for user authentication, registration, and SSO operations.
 * It integrates DatabaseRepository and SsoManager for direct database read/write connectivity and single sign-on.
 */
class UserViewModel : ViewModel()
{
    companion object
    {
        private const val TAG = "UserViewModel"
    }

    /**
     * Validates that the full name contains at least two names (first name and surname).
     */
    fun isValidFullName(fullName: String): Boolean
    {
        val trimmed = fullName.trim().replace(Regex("\\s+"), " ")

        if (trimmed.isEmpty())
        {
            return false
        }

        val nameParts = trimmed.split(" ").filter { it.isNotEmpty() }
        return nameParts.size >= 2
    }

    /**
     * Saves a user record to the database repository and JSON backup.
     */
    @Throws(IllegalArgumentException::class, IOException::class, RuntimeException::class)
    fun saveUser(context: Context, user: User)
    {
        Log.d(TAG, "saveUser called for username: ${user.username}")

        if (!isValidFullName(user.name))
        {
            Log.e(TAG, "saveUser failed: Invalid full name for username: ${user.username}")
            throw IllegalArgumentException("Please enter your full name (first name and surname)")
        }

        try
        {
            val repository = DatabaseRepository(context)
            val success = repository.saveUser(user)
            if (success)
            {
                Log.d(TAG, "saveUser completed successfully for username: ${user.username}")
            }
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
     * Loads a user record from the SQLite database repository by username.
     */
    fun loadUser(context: Context, username: String): User?
    {
        return try
        {
            val repository = DatabaseRepository(context)
            repository.loadUser(username)
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception while loading user: $username", exception)
            null
        }
    }

    /**
     * Authenticates or provisions a user using Single Sign-On (SSO).
     */
    fun authenticateWithSso(
        context: Context,
        email: String,
        displayName: String,
        provider: String = SsoManager.PROVIDER_GOOGLE
    ): User?
    {
        return try
        {
            val ssoManager = SsoManager(context)
            ssoManager.authenticateWithSso(email, displayName, provider)
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "SSO authentication failed for email: $email", exception)
            null
        }
    }
}