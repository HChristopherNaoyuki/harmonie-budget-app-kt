package com.example.harmonie_budget_app_kt.auth

import android.content.Context
import android.util.Log
import com.example.harmonie_budget_app_kt.db.DatabaseRepository
import com.example.harmonie_budget_app_kt.models.User
import com.example.harmonie_budget_app_kt.utils.JsonHelper
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * SsoManager implements Single Sign-On (SSO) and Google Authentication workflow.
 *
 * It provides methods to authenticate users via SSO identity providers (Google / Federated SSO),
 * automatically provisioning user accounts into the database repository with secure credentials.
 */
class SsoManager(private val context: Context)
{
    companion object
    {
        private const val TAG = "SsoManager"
        const val PROVIDER_GOOGLE = "Google"
        const val PROVIDER_FEDERATED = "FederatedSSO"
    }

    private val repository = DatabaseRepository(context)

    /**
     * Authenticates or provisions a user using Single Sign-On (SSO) credentials.
     *
     * @param email Email address returned by the SSO provider
     * @param displayName Display name returned by the SSO provider
     * @param provider Provider identifier (e.g. Google)
     * @return User object on successful authentication or account creation
     */
    fun authenticateWithSso(
        email: String,
        displayName: String,
        provider: String = PROVIDER_GOOGLE
    ): User?
    {
        val username = email.trim().lowercase(Locale.US)
        Log.d(TAG, "Initiating SSO authentication for provider: $provider, username: $username")

        val existingUser = repository.loadUser(username)
        if (existingUser != null)
        {
            Log.i(TAG, "SSO login successful for existing account: $username")
            return existingUser
        }

        // Generate synthetic full name if display name is single word
        val fullName = if (displayName.trim().split(" ").size >= 2)
        {
            displayName.trim()
        }
        else
        {
            "${displayName.trim()} User"
        }

        val generatedUserId = generateSsoUserId(username, provider)
        val defaultPasswordHash = JsonHelper.hashPassword("SSO_Auth_${System.currentTimeMillis()}")

        val newUser = User(
            name = fullName,
            surname = "",
            username = username,
            password = defaultPasswordHash,
            userId = generatedUserId
        )

        val saved = repository.saveUser(newUser, ssoProvider = provider)
        if (saved)
        {
            Log.i(TAG, "New SSO account provisioned successfully: $username")
            return newUser
        }
        else
        {
            Log.e(TAG, "Failed to save new SSO account: $username")
            return null
        }
    }

    /**
     * Generates a unique user identifier formatted for SSO accounts.
     */
    private fun generateSsoUserId(username: String, provider: String): String
    {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePart = String.format(Locale.US, "%04d%02d%02d", year, month, day)
        val prefix = provider.take(3).uppercase(Locale.US) + username.take(3).uppercase(Locale.US)
        val counter = String.format(Locale.US, "%04d", System.currentTimeMillis() % 10000)
        return prefix + datePart + counter
    }
}