// app/kotlin+java/com.example.harmonie_budget_app_kt/utils/JsonHelper.kt
package com.example.harmonie_budget_app_kt.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.lang.reflect.Type

/**
 * Utility class for all JSON persistence.
 * Uses internal storage (filesDir/budget_data/) to create dedicated folder.
 * If file does not exist, it is created automatically on first save.
 * Pure JSON - no RoomDB or query languages as per assignment requirement.
 * All changes are mentioned in comments below.
 */
object JsonHelper {

    private const val DATA_FOLDER = "budget_data"
    private val gson = Gson()

    /**
     * Returns the dedicated data folder and ensures it exists.
     * Change: Added mkdirs() to auto-create folder on first use.
     */
    private fun getDataDir(context: Context): File {
        val dir = File(context.filesDir, DATA_FOLDER)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    /**
     * Saves any object list as JSON.
     * Change: Generic support for all models (User, Category, Expense, Goal).
     */
    fun <T> saveList(context: Context, filename: String, list: List<T>) {
        val file = File(getDataDir(context), filename)
        val json = gson.toJson(list)
        file.writeText(json)
    }

    /**
     * Loads list from JSON. Returns empty list if file missing.
     * Change: Auto-creates empty file if not present.
     */
    fun <T> loadList(context: Context, filename: String, type: Type): List<T> {
        val file = File(getDataDir(context), filename)
        if (!file.exists()) {
            file.writeText("[]") // Create empty JSON
            return emptyList()
        }
        val json = file.readText()
        return gson.fromJson(json, type)
    }

    // Convenience methods for each model
    fun saveUsers(context: Context, users: List<User>) = saveList(context, "users.json", users)
    fun loadUsers(context: Context): List<User> {
        val type: Type = object : TypeToken<List<User>>() {}.type
        return loadList(context, "users.json", type)
    }

    fun saveCategories(context: Context, categories: List<Category>) = saveList(context, "categories.json", categories)
    fun loadCategories(context: Context): List<Category> {
        val type: Type = object : TypeToken<List<Category>>() {}.type
        return loadList(context, "categories.json", type)
    }

    fun saveExpenses(context: Context, expenses: List<Expense>) = saveList(context, "expenses.json", expenses)
    fun loadExpenses(context: Context): List<Expense> {
        val type: Type = object : TypeToken<List<Expense>>() {}.type
        return loadList(context, "expenses.json", type)
    }

    fun saveGoal(context: Context, goal: Goal) {
        val file = File(getDataDir(context), "goal.json")
        val json = gson.toJson(goal)
        file.writeText(json)
    }

    fun loadGoal(context: Context): Goal {
        val file = File(getDataDir(context), "goal.json")
        if (!file.exists()) {
            // Default goal if none saved
            val default = Goal(0.0, 5000.0)
            saveGoal(context, default)
            return default
        }
        val json = file.readText()
        return gson.fromJson(json, Goal::class.java)
    }
}