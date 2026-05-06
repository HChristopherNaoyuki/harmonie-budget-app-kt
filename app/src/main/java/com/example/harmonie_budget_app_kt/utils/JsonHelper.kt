package com.example.harmonie_budget_app_kt.utils

import android.content.Context
import android.util.Base64
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
 * Passwords are hashed using PBKDF2 with a random 128-bit salt and 10,000 iterations.
 * The hash is stored as a Base64 string in "salt:hash" format.
 */
class JsonHelper
{
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

        if (!folder.exists())
        {
            val created = folder.mkdirs()
            if (!created)
            {
                throw IOException("Failed to create budget_data directory")
            }
        }

        return File(folder, fileName)
    }

    // ==================== Security Helper Methods ====================

    companion object
    {
        private const val ITERATION_COUNT = 10000
        private const val KEY_LENGTH = 256
        private const val SALT_LENGTH_BYTES = 16

        /**
         * Hashes a plaintext password using PBKDF2 with a random salt.
         *
         * @param password The plaintext password to hash
         * @return Base64-encoded salt and hash separated by a colon
         */
        fun hashPassword(password: String): String
        {
            val salt = ByteArray(SALT_LENGTH_BYTES)
            SecureRandom().nextBytes(salt)

            val spec = PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH)
            val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
            val hash = factory.generateSecret(spec).encoded

            val saltBase64 = Base64.encodeToString(salt, Base64.NO_WRAP)
            val hashBase64 = Base64.encodeToString(hash, Base64.NO_WRAP)
            return "$saltBase64:$hashBase64"
        }

        /**
         * Verifies a plaintext password against a stored hash.
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
                    return false
                }

                val salt = Base64.decode(parts[0], Base64.NO_WRAP)
                val expectedHash = Base64.decode(parts[1], Base64.NO_WRAP)

                val spec = PBEKeySpec(password.toCharArray(), salt, ITERATION_COUNT, KEY_LENGTH)
                val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                val actualHash = factory.generateSecret(spec).encoded

                actualHash.contentEquals(expectedHash)
            }
            catch (exception: Exception)
            {
                // The underscore prefix indicates the parameter is intentionally unused.
                false
            }
        }
    }

    // ==================== User Methods ====================

    /**
     * Saves a user to the JSON file. The password is hashed before storage.
     *
     * @param context Application context
     * @param user User object containing plaintext password (will be hashed)
     * @throws IOException If file operations fail
     * @throws RuntimeException If hashing fails
     */
    fun saveUser(context: Context, user: User)
    {
        try
        {
            val file = getFile(context, "${user.username}.json")

            val hashedPassword = hashPassword(user.password)

            val secureUser = User(
                name = user.name,
                surname = user.surname,
                username = user.username,
                password = hashedPassword,
                userId = user.userId
            )

            val json = gson.toJson(secureUser)
            file.writeText(json)
        }
        catch (exception: IOException)
        {
            // The underscore prefix indicates the parameter is intentionally unused.
            throw IOException("Failed to write user file", exception)
        }
        catch (exception: Exception)
        {
            // The underscore prefix indicates the parameter is intentionally unused.
            throw RuntimeException("Failed to hash password or serialize user", exception)
        }
    }

    /**
     * Loads a user from the JSON file.
     *
     * @param context Application context
     * @param username Username of the user to load
     * @return User object with hashed password, or null if the file does not exist
     * @throws IOException If file reading fails
     */
    fun loadUser(context: Context, username: String): User?
    {
        val file = getFile(context, "$username.json")
        if (!file.exists())
        {
            return null
        }

        val json = file.readText()
        return gson.fromJson(json, User::class.java)
    }

    // ==================== Category Methods ====================

    /**
     * Saves a new category for the user.
     *
     * @param context Application context
     * @param username User identifier
     * @param category Category to save
     * @throws IOException If file operations fail
     */
    fun saveCategory(context: Context, username: String, category: Category)
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
        val file = getFile(context, "${username}_categories.json")
        if (!file.exists())
        {
            return emptyList()
        }

        val listType = object : TypeToken<List<Category>>() {}.type
        return gson.fromJson(file.readText(), listType)
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
        val file = getFile(context, "${username}_expenses.json")
        if (!file.exists())
        {
            return emptyList()
        }

        val listType = object : TypeToken<List<Expense>>() {}.type
        return gson.fromJson(file.readText(), listType)
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
        val file = getFile(context, "${username}_goals.json")
        val json = gson.toJson(goal)
        file.writeText(json)
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
        val file = getFile(context, "${username}_goals.json")
        if (!file.exists())
        {
            return null
        }

        val json = file.readText()
        return gson.fromJson(json, Goal::class.java)
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
        val file = getFile(context, "${username}_badges.json")
        if (!file.exists())
        {
            return emptyList()
        }

        val listType = object : TypeToken<List<Badge>>() {}.type
        return gson.fromJson(file.readText(), listType)
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
        val file = getFile(context, "${username}_streak.json")
        val json = gson.toJson(streakData)
        file.writeText(json)
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
        val file = getFile(context, "${username}_streak.json")
        if (!file.exists())
        {
            return null
        }

        val json = file.readText()
        return gson.fromJson(json, StreakData::class.java)
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
                exportFolder.mkdirs()
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
            true
        }
        catch (exception: Exception)
        {
            // The underscore prefix indicates the parameter is intentionally unused.
            false
        }
    }

    /**
     * Resets the user's progress by deleting all data files except the user account file.
     *
     * @param context Application context
     * @param username User identifier
     * @return True always
     */
    fun resetProgress(context: Context, username: String): Boolean
    {
        try
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
                    file.delete()
                }
            }
        }
        catch (exception: Exception)
        {
            // The underscore prefix indicates the parameter is intentionally unused.
            return false
        }
        return true
    }
}