package com.example.harmonie_budget_app_kt.utils

import android.content.Context
import android.util.Base64
import android.util.Log
import com.example.harmonie_budget_app_kt.models.Badge
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.models.Goal
import com.example.harmonie_budget_app_kt.models.StreakData
import com.example.harmonie_budget_app_kt.models.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.io.IOException
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * JsonHelper handles all JSON file operations for the application.
 *
 * Data is stored in the budget_data folder within the app's internal storage.
 * Each user has isolated files identified by their username.
 *
 * Security Enhancement:
 * Passwords are hashed using a two-tier approach:
 * 1. Primary: PBKDF2 with HMAC-SHA1 (compatible with all Android API 23+ devices)
 * 2. Fallback: SHA-256 with salt if PBKDF2 is unavailable
 *
 * The hash is stored as a Base64 string in "salt:hash" format.
 *
 * Part 3 Enhancement:
 * Added full name validation in saveUser method requiring at least two names.
 */
class JsonHelper
{
    companion object
    {
        private const val TAG = "JsonHelper"
        private const val ITERATION_COUNT = 10000
        private const val KEY_LENGTH = 256
        private const val SALT_LENGTH_BYTES = 16

        /**
         * Hashes a plaintext password using PBKDF2 with a random salt.
         * Falls back to SHA-256 if PBKDF2 is not available.
         *
         * @param password The plaintext password to hash
         * @return Base64-encoded salt and hash separated by a colon
         */
        fun hashPassword(password: String): String
        {
            val salt = ByteArray(SALT_LENGTH_BYTES)
            SecureRandom().nextBytes(salt)

            // Try PBKDF2 first (more secure).
            val hash = try
            {
                // Using "PBKDF2WithHmacSHA1" for maximum Android compatibility.
                val spec = PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH)
                val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                factory.generateSecret(spec).encoded
            }
            catch (exception: Exception)
            {
                Log.w(TAG, "PBKDF2 not available, falling back to SHA-256", exception)

                // Fallback: Use SHA-256 with the salt.
                val messageDigest = MessageDigest.getInstance("SHA-256")
                val saltedPassword = salt + password.toByteArray()
                messageDigest.digest(saltedPassword)
            }

            val saltBase64 = Base64.encodeToString(salt, Base64.NO_WRAP)
            val hashBase64 = Base64.encodeToString(hash, Base64.NO_WRAP)
            return "$saltBase64:$hashBase64"
        }

        /**
         * Verifies a plaintext password against a stored hash.
         * Supports both PBKDF2 and SHA-256 fallback hashes.
         *
         * @param password The plaintext password to verify
         * @param storedHash The stored hash string in "salt:hash" format
         * @return True if the password matches the hash, false otherwise
         */
        fun verifyPassword(password: String, storedHash: String): Boolean
        {
            return try
            {
                val parts = storedHash.split(":")
                if (parts.size != 2)
                {
                    Log.e(TAG, "Invalid stored hash format: expected salt:hash")
                    return false
                }

                val salt = Base64.decode(parts[0], Base64.NO_WRAP)
                val expectedHash = Base64.decode(parts[1], Base64.NO_WRAP)

                var actualHash: ByteArray? = null

                try
                {
                    val spec = PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH)
                    val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
                    actualHash = factory.generateSecret(spec).encoded
                }
                catch (exception: Exception)
                {
                    Log.w(TAG, "PBKDF2 verification failed, trying SHA-256 fallback", exception)
                    val messageDigest = MessageDigest.getInstance("SHA-256")
                    val saltedPassword = salt + password.toByteArray()
                    actualHash = messageDigest.digest(saltedPassword)
                }

                actualHash != null && actualHash.contentEquals(expectedHash)
            }
            catch (exception: Exception)
            {
                Log.e(TAG, "Password verification failed", exception)
                false
            }
        }
    }

    private val gson: Gson by lazy { Gson() }

    /**
     * Returns a File object pointing to the budget_data directory.
     * Creates the directory if it does not exist.
     *
     * @param context Application context for file access
     * @param fileName Name of the file to access
     * @return File object within the budget_data directory
     * @throws IOException If the directory cannot be created
     */
    private fun getFile(context: Context, fileName: String): File
    {
        val folder = File(context.filesDir, "budget_data")

        Log.d(TAG, "Storage path: ${context.filesDir.absolutePath}")

        if (!folder.exists())
        {
            Log.d(TAG, "budget_data folder does not exist. Attempting to create.")
            val created = folder.mkdirs()

            if (!created)
            {
                val errorMessage = "Failed to create budget_data directory at ${folder.absolutePath}"
                Log.e(TAG, errorMessage)
                throw IOException(errorMessage)
            }

            Log.d(TAG, "budget_data folder created successfully")
        }

        return File(folder, fileName)
    }

    // ==================== User Methods ====================

    /**
     * Validates that the full name contains at least two names (first name and surname).
     * This method is used internally by saveUser for backend validation.
     *
     * @param fullName The full name string to validate
     * @return True if the name contains at least two non-empty parts after trimming
     */
    private fun isValidFullName(fullName: String): Boolean
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
     * Saves a user to the JSON file. The password is hashed before storage.
     * Performs backend validation on the full name before saving.
     *
     * @param context Application context
     * @param user User object containing plaintext password (will be hashed)
     * @throws IllegalArgumentException If the full name is invalid
     * @throws IOException If file operations fail
     * @throws RuntimeException If hashing fails
     */
    @Throws(IllegalArgumentException::class, IOException::class, RuntimeException::class)
    fun saveUser(context: Context, user: User)
    {
        Log.d(TAG, "saveUser called for username: ${user.username}")

        // Part 3 Enhancement: Backend validation for full name.
        // This ensures that even if frontend validation is bypassed, the name is validated here.
        if (!isValidFullName(user.name))
        {
            Log.e(TAG, "saveUser failed: Invalid full name for username: ${user.username}")
            throw IllegalArgumentException("Please enter your full name (first name and surname)")
        }

        // Trim and clean the name for storage
        val cleanedName = user.name.trim().replace(Regex("\\s+"), " ")

        try
        {
            val file = getFile(context, "${user.username}.json")

            val hashedPassword = hashPassword(user.password)
            Log.d(TAG, "Password hashed successfully for username: ${user.username}")

            val secureUser = User(
                name = cleanedName,
                surname = user.surname,
                username = user.username,
                password = hashedPassword,
                userId = user.userId
            )

            val json = gson.toJson(secureUser)
            file.writeText(json)
            Log.d(TAG, "User file written successfully for username: ${user.username}")
        }
        catch (exception: IOException)
        {
            Log.e(TAG, "IO Exception in saveUser for ${user.username}", exception)
            throw IOException("Failed to write user file: ${exception.message}", exception)
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception in saveUser for ${user.username}", exception)
            throw RuntimeException("Failed to hash password or serialize user: ${exception.message}", exception)
        }
    }

    /**
     * Loads a user from the JSON file.
     *
     * @param context Application context
     * @param username Username of the user to load
     * @return User object with hashed password, or null if the file does not exist
     */
    fun loadUser(context: Context, username: String): User?
    {
        try
        {
            val file = getFile(context, "$username.json")

            if (!file.exists())
            {
                Log.d(TAG, "User file does not exist for username: $username")
                return null
            }

            val json = file.readText()
            return gson.fromJson(json, User::class.java)
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception in loadUser for $username", exception)
            return null
        }
    }

    // ==================== Category Methods ====================

    /**
     * Saves a new category for the user.
     *
     * @param context Application context
     * @param username User identifier
     * @param category Category to save
     */
    fun saveCategory(context: Context, username: String, category: Category)
    {
        try
        {
            val file = getFile(context, "${username}_categories.json")
            val listType = object : TypeToken<MutableList<Category>>() {}.type
            val list: MutableList<Category> = if (file.exists())
            {
                gson.fromJson(file.readText(), listType)
            }
            else
            {
                mutableListOf()
            }

            val nameExists = list.any { it.name.equals(category.name, ignoreCase = true) }

            if (!nameExists)
            {
                list.add(category)
                file.writeText(gson.toJson(list))
                Log.d(TAG, "Category saved for user $username: ${category.name}")
            }
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception in saveCategory for $username", exception)
        }
    }

    /**
     * Loads all categories for the user.
     *
     * @param context Application context
     * @param username User identifier
     * @return List of categories, empty if none exist
     */
    fun loadCategories(context: Context, username: String): List<Category>
    {
        try
        {
            val file = getFile(context, "${username}_categories.json")
            if (!file.exists())
            {
                return emptyList()
            }

            val listType = object : TypeToken<List<Category>>() {}.type
            return gson.fromJson(file.readText(), listType)
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception in loadCategories for $username", exception)
            return emptyList()
        }
    }

    // ==================== Expense Methods ====================

    /**
     * Saves a new expense for the user.
     *
     * @param context Application context
     * @param username User identifier
     * @param expense Expense to save
     */
    fun saveExpense(context: Context, username: String, expense: Expense)
    {
        try
        {
            val file = getFile(context, "${username}_expenses.json")
            val listType = object : TypeToken<MutableList<Expense>>() {}.type
            val list: MutableList<Expense> = if (file.exists())
            {
                gson.fromJson(file.readText(), listType)
            }
            else
            {
                mutableListOf()
            }

            val maxId = list.maxOfOrNull { it.id } ?: 0
            val newExpense = expense.copy(id = maxId + 1)
            list.add(newExpense)

            file.writeText(gson.toJson(list))
            Log.d(TAG, "Expense saved for user $username with ID ${newExpense.id}")
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception in saveExpense for $username", exception)
        }
    }

    /**
     * Loads all expenses for the user.
     *
     * @param context Application context
     * @param username User identifier
     * @return List of expenses, empty if none exist
     */
    fun loadExpenses(context: Context, username: String): List<Expense>
    {
        try
        {
            val file = getFile(context, "${username}_expenses.json")
            if (!file.exists())
            {
                return emptyList()
            }

            val listType = object : TypeToken<List<Expense>>() {}.type
            return gson.fromJson(file.readText(), listType)
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception in loadExpenses for $username", exception)
            return emptyList()
        }
    }

    // ==================== Goal Methods ====================

    /**
     * Saves the user's monthly spending goals.
     *
     * @param context Application context
     * @param username User identifier
     * @param goal Goal object
     */
    fun saveGoal(context: Context, username: String, goal: Goal)
    {
        try
        {
            val file = getFile(context, "${username}_goals.json")
            val json = gson.toJson(goal)
            file.writeText(json)
            Log.d(TAG, "Goal saved for user $username")
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception in saveGoal for $username", exception)
        }
    }

    /**
     * Loads the user's monthly spending goals.
     *
     * @param context Application context
     * @param username User identifier
     * @return Goal object, or null if no goal exists
     */
    fun loadGoal(context: Context, username: String): Goal?
    {
        try
        {
            val file = getFile(context, "${username}_goals.json")
            if (!file.exists())
            {
                return null
            }

            val json = file.readText()
            return gson.fromJson(json, Goal::class.java)
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception in loadGoal for $username", exception)
            return null
        }
    }

    // ==================== Gamification: Badge Methods ====================

    /**
     * Saves a badge for the user.
     *
     * @param context Application context
     * @param username User identifier
     * @param badge Badge to save
     */
    fun saveBadge(context: Context, username: String, badge: Badge)
    {
        try
        {
            val file = getFile(context, "${username}_badges.json")
            val listType = object : TypeToken<MutableList<Badge>>() {}.type
            val list: MutableList<Badge> = if (file.exists())
            {
                gson.fromJson(file.readText(), listType)
            }
            else
            {
                mutableListOf()
            }

            if (list.none { it.id == badge.id })
            {
                list.add(badge)
                file.writeText(gson.toJson(list))
                Log.d(TAG, "Badge saved for user $username: ${badge.name}")
            }
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception in saveBadge for $username", exception)
        }
    }

    /**
     * Loads all badges earned by the user.
     *
     * @param context Application context
     * @param username User identifier
     * @return List of badges, empty if none exist
     */
    fun loadBadges(context: Context, username: String): List<Badge>
    {
        try
        {
            val file = getFile(context, "${username}_badges.json")
            if (!file.exists())
            {
                return emptyList()
            }

            val listType = object : TypeToken<List<Badge>>() {}.type
            return gson.fromJson(file.readText(), listType)
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception in loadBadges for $username", exception)
            return emptyList()
        }
    }

    // ==================== Gamification: Streak Methods ====================

    /**
     * Saves streak data for the user.
     *
     * @param context Application context
     * @param username User identifier
     * @param streakData StreakData object
     */
    fun saveStreakData(context: Context, username: String, streakData: StreakData)
    {
        try
        {
            val file = getFile(context, "${username}_streak.json")
            val json = gson.toJson(streakData)
            file.writeText(json)
            Log.d(TAG, "Streak data saved for user $username")
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception in saveStreakData for $username", exception)
        }
    }

    /**
     * Loads streak data for the user.
     *
     * @param context Application context
     * @param username User identifier
     * @return StreakData object, or null if no streak data exists
     */
    fun loadStreakData(context: Context, username: String): StreakData?
    {
        try
        {
            val file = getFile(context, "${username}_streak.json")
            if (!file.exists())
            {
                return null
            }

            val json = file.readText()
            return gson.fromJson(json, StreakData::class.java)
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception in loadStreakData for $username", exception)
            return null
        }
    }

    // ==================== Export and Reset Methods ====================

    /**
     * Exports all user data files to an export folder.
     *
     * @param context Application context
     * @param username User identifier
     * @return True if export succeeded, false otherwise
     */
    fun exportData(context: Context, username: String): Boolean
    {
        return try
        {
            val exportFolder = File(context.filesDir, "budget_data/export_$username")
            if (!exportFolder.exists())
            {
                val created = exportFolder.mkdirs()
                if (!created)
                {
                    Log.e(TAG, "Failed to create export folder for user $username")
                    return false
                }
            }

            val files = listOf(
                "${username}.json",
                "${username}_categories.json",
                "${username}_expenses.json",
                "${username}_goals.json",
                "${username}_badges.json",
                "${username}_streak.json"
            )

            for (fileName in files)
            {
                val source = getFile(context, fileName)
                if (source.exists())
                {
                    val dest = File(exportFolder, fileName)
                    source.copyTo(dest, overwrite = true)
                }
            }

            Log.d(TAG, "Data exported successfully for user $username")
            true
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception in exportData for $username", exception)
            false
        }
    }

    /**
     * Resets the user's progress by deleting all data files except the user account file.
     *
     * @param context Application context
     * @param username User identifier
     * @return True if reset succeeded (or files did not exist), false if an exception occurred
     */
    fun resetProgress(context: Context, username: String): Boolean
    {
        return try
        {
            val files = listOf(
                "${username}_categories.json",
                "${username}_expenses.json",
                "${username}_goals.json",
                "${username}_badges.json",
                "${username}_streak.json"
            )

            for (fileName in files)
            {
                val file = getFile(context, fileName)
                if (file.exists())
                {
                    val deleted = file.delete()
                    if (!deleted)
                    {
                        Log.w(TAG, "Failed to delete file: $fileName for user $username")
                    }
                }
            }

            Log.d(TAG, "Progress reset successfully for user $username")
            true
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception in resetProgress for $username", exception)
            false
        }
    }
}