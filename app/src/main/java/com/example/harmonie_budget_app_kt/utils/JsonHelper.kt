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
 * Data Layer Improvements:
 * - Atomic write logic via temporary files to prevent data corruption on write failures.
 * - Complete CRUD operations for Expense, Category, Goal, Badge, Streak, and User entities.
 * - Return values indicating operation success or failure for caller error handling.
 * - Validation rules for data integrity (e.g. positive expense amounts, non-duplicate categories).
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

    /**
     * Writes content to a destination file atomically using a temporary file.
     * This prevents file corruption in the event of an unexpected termination during write.
     *
     * @param destFile Target file where content should ultimately be written
     * @param content String data to write into the file
     * @return True if the atomic write succeeded, false otherwise
     */
    private fun atomicWrite(destFile: File, content: String): Boolean
    {
        val tempFile = File(destFile.parentFile, "${destFile.name}.tmp")
        return try
        {
            tempFile.writeText(content)
            if (tempFile.renameTo(destFile))
            {
                true
            }
            else
            {
                // In case direct rename fails, copy and delete temporary file
                tempFile.copyTo(destFile, overwrite = true)
                tempFile.delete()
                true
            }
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Atomic write failed for file ${destFile.absolutePath}", exception)
            if (tempFile.exists())
            {
                tempFile.delete()
            }
            false
        }
    }

    // ==================== User Methods ====================

    /**
     * Validates that the full name contains at least two names (first name and surname).
     *
     * @param fullName The full name string to validate
     * @return True if the name contains at least two non-empty parts after trimming
     */
    private fun isValidFullName(fullName: String): Boolean
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
     * Saves a user to the JSON file. The password is hashed before storage.
     * Performs backend validation on the full name and username existence before saving.
     *
     * @param context Application context
     * @param user User object containing plaintext password (will be hashed)
     * @return True if the user was saved successfully, false otherwise
     * @throws IllegalArgumentException If the full name is invalid or username already exists
     * @throws IOException If file operations fail
     * @throws RuntimeException If hashing fails
     */
    @Throws(IllegalArgumentException::class, IOException::class, RuntimeException::class)
    fun saveUser(context: Context, user: User): Boolean
    {
        Log.d(TAG, "saveUser called for username: ${user.username}")

        if (!isValidFullName(user.name))
        {
            Log.e(TAG, "saveUser failed: Invalid full name for username: ${user.username}")
            throw IllegalArgumentException("Please enter your full name (first name and surname)")
        }

        val cleanedName = user.name.trim().replace(Regex("\\s+"), " ")

        try
        {
            val file = getFile(context, "${user.username}.json")

            if (file.exists())
            {
                Log.e(TAG, "saveUser failed: Username already exists: ${user.username}")
                throw IllegalArgumentException("Username already exists")
            }

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
            val success = atomicWrite(file, json)
            if (success)
            {
                Log.d(TAG, "User file written successfully for username: ${user.username}")
            }
            return success
        }
        catch (exception: IllegalArgumentException)
        {
            throw exception
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
     * Saves a new category for the user after validation.
     *
     * @param context Application context
     * @param username User identifier
     * @param category Category to save
     * @return True if saved successfully, false if name is invalid, duplicate, or write fails
     */
    fun saveCategory(context: Context, username: String, category: Category): Boolean
    {
        val categoryName = category.name.trim()
        if (categoryName.isEmpty())
        {
            Log.e(TAG, "saveCategory failed: Category name cannot be empty")
            return false
        }

        return try
        {
            val file = getFile(context, "${username}_categories.json")
            val listType = object : TypeToken<MutableList<Category>>() {}.type
            val list: MutableList<Category> = if (file.exists())
            {
                gson.fromJson(file.readText(), listType) ?: mutableListOf()
            }
            else
            {
                mutableListOf()
            }

            val nameExists = list.any { it.name.equals(categoryName, ignoreCase = true) }

            if (nameExists)
            {
                Log.w(TAG, "saveCategory skipped: Duplicate category name ${category.name}")
                return false
            }

            val cleanedCategory = category.copy(name = categoryName)
            list.add(cleanedCategory)
            val success = atomicWrite(file, gson.toJson(list))
            if (success)
            {
                Log.d(TAG, "Category saved for user $username: ${cleanedCategory.name}")
            }
            success
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception in saveCategory for $username", exception)
            false
        }
    }

    /**
     * Updates an existing category for the user.
     *
     * @param context Application context
     * @param username User identifier
     * @param updatedCategory Category object with updated details
     * @return True if updated successfully, false if not found or write fails
     */
    fun updateCategory(context: Context, username: String, updatedCategory: Category): Boolean
    {
        val categoryName = updatedCategory.name.trim()
        if (categoryName.isEmpty())
        {
            Log.e(TAG, "updateCategory failed: Category name cannot be empty")
            return false
        }

        return try
        {
            val file = getFile(context, "${username}_categories.json")
            if (!file.exists())
            {
                return false
            }

            val listType = object : TypeToken<MutableList<Category>>() {}.type
            val list: MutableList<Category> = gson.fromJson(file.readText(), listType) ?: return false

            val index = list.indexOfFirst { it.id == updatedCategory.id }
            if (index == -1)
            {
                Log.w(TAG, "updateCategory failed: Category ID ${updatedCategory.id} not found")
                return false
            }

            // Check duplicate name on another category
            val duplicate = list.any { it.id != updatedCategory.id && it.name.equals(categoryName, ignoreCase = true) }
            if (duplicate)
            {
                Log.w(TAG, "updateCategory failed: Duplicate category name $categoryName")
                return false
            }

            list[index] = updatedCategory.copy(name = categoryName)
            val success = atomicWrite(file, gson.toJson(list))
            if (success)
            {
                Log.d(TAG, "Category updated for user $username: ID ${updatedCategory.id}")
            }
            success
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception in updateCategory for $username", exception)
            false
        }
    }

    /**
     * Deletes a category by ID for the user.
     *
     * @param context Application context
     * @param username User identifier
     * @param categoryId ID of the category to delete
     * @return True if deleted successfully, false otherwise
     */
    fun deleteCategory(context: Context, username: String, categoryId: Int): Boolean
    {
        return try
        {
            val file = getFile(context, "${username}_categories.json")
            if (!file.exists())
            {
                return false
            }

            val listType = object : TypeToken<MutableList<Category>>() {}.type
            val list: MutableList<Category> = gson.fromJson(file.readText(), listType) ?: return false

            val removed = list.removeAll { it.id == categoryId }
            if (!removed)
            {
                Log.w(TAG, "deleteCategory failed: Category ID $categoryId not found")
                return false
            }

            val success = atomicWrite(file, gson.toJson(list))
            if (success)
            {
                Log.d(TAG, "Category deleted for user $username: ID $categoryId")
            }
            success
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception in deleteCategory for $username", exception)
            false
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
            return gson.fromJson(file.readText(), listType) ?: emptyList()
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception in loadCategories for $username", exception)
            return emptyList()
        }
    }

    // ==================== Expense Methods ====================

    /**
     * Saves a new expense for the user after validating amount.
     *
     * @param context Application context
     * @param username User identifier
     * @param expense Expense to save
     * @return True if saved successfully, false if amount <= 0 or write fails
     */
    fun saveExpense(context: Context, username: String, expense: Expense): Boolean
    {
        if (expense.amount <= 0.0)
        {
            Log.e(TAG, "saveExpense failed: Expense amount must be positive")
            return false
        }

        return try
        {
            val file = getFile(context, "${username}_expenses.json")
            val listType = object : TypeToken<MutableList<Expense>>() {}.type
            val list: MutableList<Expense> = if (file.exists())
            {
                gson.fromJson(file.readText(), listType) ?: mutableListOf()
            }
            else
            {
                mutableListOf()
            }

            val maxId = list.maxOfOrNull { it.id } ?: 0
            val newExpense = expense.copy(id = maxId + 1)
            list.add(newExpense)

            val success = atomicWrite(file, gson.toJson(list))
            if (success)
            {
                Log.d(TAG, "Expense saved for user $username with ID ${newExpense.id}")
            }
            success
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception in saveExpense for $username", exception)
            false
        }
    }

    /**
     * Updates an existing expense for the user.
     *
     * @param context Application context
     * @param username User identifier
     * @param updatedExpense Expense object with updated details
     * @return True if updated successfully, false if not found, invalid amount, or write fails
     */
    fun updateExpense(context: Context, username: String, updatedExpense: Expense): Boolean
    {
        if (updatedExpense.amount <= 0.0)
        {
            Log.e(TAG, "updateExpense failed: Expense amount must be positive")
            return false
        }

        return try
        {
            val file = getFile(context, "${username}_expenses.json")
            if (!file.exists())
            {
                return false
            }

            val listType = object : TypeToken<MutableList<Expense>>() {}.type
            val list: MutableList<Expense> = gson.fromJson(file.readText(), listType) ?: return false

            val index = list.indexOfFirst { it.id == updatedExpense.id }
            if (index == -1)
            {
                Log.w(TAG, "updateExpense failed: Expense ID ${updatedExpense.id} not found")
                return false
            }

            list[index] = updatedExpense
            val success = atomicWrite(file, gson.toJson(list))
            if (success)
            {
                Log.d(TAG, "Expense updated for user $username: ID ${updatedExpense.id}")
            }
            success
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception in updateExpense for $username", exception)
            false
        }
    }

    /**
     * Deletes an expense by ID for the user.
     *
     * @param context Application context
     * @param username User identifier
     * @param expenseId ID of the expense to delete
     * @return True if deleted successfully, false otherwise
     */
    fun deleteExpense(context: Context, username: String, expenseId: Int): Boolean
    {
        return try
        {
            val file = getFile(context, "${username}_expenses.json")
            if (!file.exists())
            {
                return false
            }

            val listType = object : TypeToken<MutableList<Expense>>() {}.type
            val list: MutableList<Expense> = gson.fromJson(file.readText(), listType) ?: return false

            val removed = list.removeAll { it.id == expenseId }
            if (!removed)
            {
                Log.w(TAG, "deleteExpense failed: Expense ID $expenseId not found")
                return false
            }

            val success = atomicWrite(file, gson.toJson(list))
            if (success)
            {
                Log.d(TAG, "Expense deleted for user $username: ID $expenseId")
            }
            success
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception in deleteExpense for $username", exception)
            false
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
            return gson.fromJson(file.readText(), listType) ?: emptyList()
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
     * @return True if saved successfully, false otherwise
     */
    fun saveGoal(context: Context, username: String, goal: Goal): Boolean
    {
        return try
        {
            val file = getFile(context, "${username}_goals.json")
            val json = gson.toJson(goal)
            val success = atomicWrite(file, json)
            if (success)
            {
                Log.d(TAG, "Goal saved for user $username")
            }
            success
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception in saveGoal for $username", exception)
            false
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
     * @return True if saved successfully or already existing, false on write error
     */
    fun saveBadge(context: Context, username: String, badge: Badge): Boolean
    {
        return try
        {
            val file = getFile(context, "${username}_badges.json")
            val listType = object : TypeToken<MutableList<Badge>>() {}.type
            val list: MutableList<Badge> = if (file.exists())
            {
                gson.fromJson(file.readText(), listType) ?: mutableListOf()
            }
            else
            {
                mutableListOf()
            }

            if (list.none { it.id == badge.id })
            {
                list.add(badge)
                val success = atomicWrite(file, gson.toJson(list))
                if (success)
                {
                    Log.d(TAG, "Badge saved for user $username: ${badge.name}")
                }
                success
            }
            else
            {
                true
            }
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception in saveBadge for $username", exception)
            false
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
            return gson.fromJson(file.readText(), listType) ?: emptyList()
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
     * @return True if saved successfully, false otherwise
     */
    fun saveStreakData(context: Context, username: String, streakData: StreakData): Boolean
    {
        return try
        {
            val file = getFile(context, "${username}_streak.json")
            val json = gson.toJson(streakData)
            val success = atomicWrite(file, json)
            if (success)
            {
                Log.d(TAG, "Streak data saved for user $username")
            }
            success
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Exception in saveStreakData for $username", exception)
            false
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