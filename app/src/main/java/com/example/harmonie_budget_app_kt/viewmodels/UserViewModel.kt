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
 * Part 3 Enhancement:
 * Added backend validation for full name requiring at least two names.
 */
class UserViewModel : ViewModel()
{
    companion object
    {
        private const val TAG = "UserViewModel"
    }

    private val jsonHelper = JsonHelper()

    /**
     * Validates that the full name contains at least two names (first name and surname).
     *
     * @param fullName The full name string to validate
     * @return True if the name contains at least two non-empty parts after trimming
     */
    fun isValidFullName(fullName: String): Boolean
    {
        // Trim the input and collapse multiple spaces into single spaces
        val trimmed = fullName.trim().replace(Regex("\\s+"), " ")

        // Check if the trimmed string is empty
        if (trimmed.isEmpty())
        {
            return false
        }

        // Split by space and filter out empty parts
        val nameParts = trimmed.split(" ").filter { it.isNotEmpty() }

        // Require at least two name parts (first name and surname)
        return nameParts.size >= 2
    }

    /**
     * Saves a new user. The password is hashed by JsonHelper.
     * Performs backend validation on the full name before saving.
     *
     * @param context Application context for file access
     * @param user User object to save (plaintext password will be hashed)
     * @throws IllegalArgumentException If the full name is invalid
     * @throws IOException If file operations fail
     * @throws RuntimeException If hashing or serialization fails
     */
    @Throws(IllegalArgumentException::class, IOException::class, RuntimeException::class)
    fun saveUser(context: Context, user: User)
    {
        Log.d(TAG, "saveUser called for username: ${user.username}")

        // Part 3 Enhancement: Backend validation for full name.
        if (!isValidFullName(user.name))
        {
            Log.e(TAG, "saveUser failed: Invalid full name for username: ${user.username}")
            throw IllegalArgumentException("Please enter your full name (first name and surname)")
        }

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