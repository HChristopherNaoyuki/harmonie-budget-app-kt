package com.example.harmonie_budget_app_kt.db

import android.content.Context
import android.util.Log
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.models.Goal
import com.example.harmonie_budget_app_kt.models.User
import com.example.harmonie_budget_app_kt.utils.JsonHelper

/**
 * DatabaseRepository provides a unified repository layer connecting the embedded SQLite database
 * and the local JSON file store.
 *
 * All read and write operations execute against the SQLite database first, ensuring full database
 * connectivity and transactional persistence, while maintaining JSON file synchronization as a secondary backup.
 */
class DatabaseRepository(private val context: Context)
{
    companion object
    {
        private const val TAG = "DatabaseRepository"
    }

    private val dbHelper = HarmonieDatabaseHelper(context)
    private val jsonHelper = JsonHelper()

    // ==================== User Repository Operations ====================

    /**
     * Saves a user into both SQLite database and JSON file store.
     */
    fun saveUser(user: User, ssoProvider: String = "LOCAL"): Boolean
    {
        val dbSuccess = dbHelper.saveUser(user, ssoProvider)
        val jsonSuccess = try
        {
            jsonHelper.saveUser(context, user)
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "JSON backup failed during saveUser", exception)
            false
        }

        Log.d(TAG, "saveUser result: SQLite=$dbSuccess, JSON=$jsonSuccess")
        return dbSuccess || jsonSuccess
    }

    /**
     * Loads a user from SQLite database with fallback to JSON store.
     */
    fun loadUser(username: String): User?
    {
        val dbUser = dbHelper.getUser(username)
        if (dbUser != null)
        {
            return dbUser
        }

        val jsonUser = jsonHelper.loadUser(context, username)
        if (jsonUser != null)
        {
            dbHelper.saveUser(jsonUser)
        }
        return jsonUser
    }

    // ==================== Category Repository Operations ====================

    /**
     * Saves a category into SQLite database and syncs with JSON store.
     */
    fun saveCategory(username: String, category: Category): Boolean
    {
        val dbSuccess = dbHelper.saveCategory(username, category)
        val jsonSuccess = jsonHelper.saveCategory(context, username, category)
        return dbSuccess || jsonSuccess
    }

    /**
     * Loads categories from SQLite database with fallback to JSON store.
     */
    fun getCategories(username: String): List<Category>
    {
        val dbCategories = dbHelper.getCategories(username)
        if (dbCategories.isNotEmpty())
        {
            return dbCategories
        }

        val jsonCategories = jsonHelper.loadCategories(context, username)
        for (category in jsonCategories)
        {
            dbHelper.saveCategory(username, category)
        }
        return jsonCategories
    }

    /**
     * Deletes a category from both SQLite database and JSON store.
     */
    fun deleteCategory(username: String, categoryId: Int): Boolean
    {
        val dbSuccess = dbHelper.deleteCategory(username, categoryId)
        val jsonSuccess = jsonHelper.deleteCategory(context, username, categoryId)
        return dbSuccess || jsonSuccess
    }

    // ==================== Expense Repository Operations ====================

    /**
     * Saves an expense into SQLite database and syncs with JSON store.
     */
    fun saveExpense(username: String, expense: Expense): Boolean
    {
        val dbSuccess = dbHelper.saveExpense(username, expense)
        val jsonSuccess = jsonHelper.saveExpense(context, username, expense)
        return dbSuccess || jsonSuccess
    }

    /**
     * Loads expenses from SQLite database with fallback to JSON store.
     */
    fun getExpenses(username: String): List<Expense>
    {
        val dbExpenses = dbHelper.getExpenses(username)
        if (dbExpenses.isNotEmpty())
        {
            return dbExpenses
        }

        val jsonExpenses = jsonHelper.loadExpenses(context, username)
        for (expense in jsonExpenses)
        {
            dbHelper.saveExpense(username, expense)
        }
        return jsonExpenses
    }

    /**
     * Deletes an expense from both SQLite database and JSON store.
     */
    fun deleteExpense(username: String, expenseId: Int): Boolean
    {
        val dbSuccess = dbHelper.deleteExpense(username, expenseId)
        val jsonSuccess = jsonHelper.deleteExpense(context, username, expenseId)
        return dbSuccess || jsonSuccess
    }

    // ==================== Goal Repository Operations ====================

    /**
     * Saves goal configuration into SQLite database and syncs with JSON store.
     */
    fun saveGoal(username: String, goal: Goal): Boolean
    {
        val dbSuccess = dbHelper.saveGoal(username, goal)
        val jsonSuccess = jsonHelper.saveGoal(context, username, goal)
        return dbSuccess || jsonSuccess
    }

    /**
     * Loads goal configuration from SQLite database with fallback to JSON store.
     */
    fun getGoal(username: String): Goal?
    {
        val dbGoal = dbHelper.getGoal(username)
        if (dbGoal != null)
        {
            return dbGoal
        }

        val jsonGoal = jsonHelper.loadGoal(context, username)
        if (jsonGoal != null)
        {
            dbHelper.saveGoal(username, jsonGoal)
        }
        return jsonGoal
    }

    // ==================== Sync Operations ====================

    fun getPendingSyncCount(): Int
    {
        return dbHelper.getDirtyRecordCount()
    }

    fun markSynced(): Int
    {
        val count = dbHelper.clearDirtyFlags()
        dbHelper.logSyncEvent("SUCCESS", count, null)
        return count
    }
}