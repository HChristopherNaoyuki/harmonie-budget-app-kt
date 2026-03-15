// app/kotlin+java/com.example.harmonie_budget_app_kt/utils/JsonHelper.kt
package com.example.harmonie_budget_app_kt.utils

import android.content.Context
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.models.Goal
import com.example.harmonie_budget_app_kt.models.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

/**
 * JsonHelper
 * Central helper for all JSON persistence.
 * Creates and uses a dedicated budget_data folder inside the app's filesDir.
 * All files are created automatically if they do not exist.
 * Pure JSON only - no database or query language.
 * Gson 2.10.1 is used.
 */
object JsonHelper {
    private const val DATA_DIR_NAME = "budget_data"

    private fun getDataDir(context: Context): File {
        val dir = File(context.filesDir, DATA_DIR_NAME)
        if (!dir.exists()) {
            dir.mkdirs()
        }
        return dir
    }

    fun <T> saveList(context: Context, fileName: String, list: List<T>) {
        val file = File(getDataDir(context), fileName)
        val gson = Gson()
        val json = gson.toJson(list)
        file.writeText(json)
    }

    fun <T> loadList(context: Context, fileName: String, clazz: Class<T>): List<T> {
        val file = File(getDataDir(context), fileName)
        if (!file.exists()) {
            return emptyList()
        }
        val gson = Gson()
        val json = file.readText()
        val type = TypeToken.getParameterized(List::class.java, clazz).type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun saveUsers(context: Context, users: List<User>) {
        saveList(context, "users.json", users)
    }

    fun loadUsers(context: Context): List<User> {
        return loadList(context, "users.json", User::class.java)
    }

    fun saveExpenses(context: Context, expenses: List<Expense>) {
        saveList(context, "expenses.json", expenses)
    }

    fun loadExpenses(context: Context): List<Expense> {
        return loadList(context, "expenses.json", Expense::class.java)
    }

    fun saveCategories(context: Context, categories: List<Category>) {
        saveList(context, "categories.json", categories)
    }

    fun loadCategories(context: Context): List<Category> {
        return loadList(context, "categories.json", Category::class.java)
    }

    fun saveGoal(context: Context, goal: Goal) {
        val file = File(getDataDir(context), "goal.json")
        val gson = Gson()
        val json = gson.toJson(goal)
        file.writeText(json)
    }

    fun loadGoal(context: Context): Goal? {
        val file = File(getDataDir(context), "goal.json")
        if (!file.exists()) {
            return null
        }
        val gson = Gson()
        val json = file.readText()
        return gson.fromJson(json, Goal::class.java)
    }
}