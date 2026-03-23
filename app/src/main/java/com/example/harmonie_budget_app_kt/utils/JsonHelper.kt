package com.example.harmonie_budget_app_kt.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.harmonie_budget_app_kt.models.User
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.models.Goal
import java.io.File

/**
 * JsonHelper class.
 * This is the single point for all JSON read and write operations in the app.
 * All data is stored exclusively in the budget_data folder inside the app's internal storage.
 * The folder is created automatically if it does not exist.
 * Every user has their own isolated JSON files (username.json for the user object,
 * username_categories.json, username_expenses.json, username_goals.json).
 * This guarantees that data for one user (for example, MichaelRichards123)
 * is never visible to any other user.
 * The only exception is the hardcoded admin account, which is handled by its username.
 * Gson is used for serialization (the dependency is already declared in build.gradle.kts).
 * No query languages or external databases are used, as required by the assignment.
 */
object JsonHelper {

    private const val FOLDER_NAME = "budget_data"

    /**
     * Returns the file handle for a given filename.
     * Creates the budget_data folder if it does not exist.
     * This ensures the project creates the required folder automatically.
     */
    private fun getFile(context: Context, fileName: String): File {
        val folder = File(context.filesDir, FOLDER_NAME)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return File(folder, fileName)
    }

    /**
     * Saves a User object to username.json.
     * Called from RegisterActivity after password validation.
     * Data is isolated per username.
     */
    fun saveUser(context: Context, user: User) {
        val file = getFile(context, "${user.username}.json")
        val gson = Gson()
        val json = gson.toJson(user)
        file.writeText(json)
    }

    /**
     * Loads a User object from username.json.
     * Used in MainActivity during login to verify credentials.
     * Returns null if the file does not exist (user not registered).
     */
    fun loadUser(context: Context, username: String): User? {
        val file = getFile(context, "$username.json")
        if (!file.exists()) {
            return null
        }
        val gson = Gson()
        return gson.fromJson(file.readText(), User::class.java)
    }

    /**
     * Saves a Category object by appending it to username_categories.json.
     * Called from CategoryActivity.
     */
    fun saveCategory(context: Context, username: String, category: Category) {
        val fileName = "${username}_categories.json"
        val file = getFile(context, fileName)
        val gson = Gson()
        val type = object : TypeToken<MutableList<Category>>() {}.type
        val list: MutableList<Category> = if (file.exists()) {
            gson.fromJson(file.readText(), type)
        } else {
            mutableListOf()
        }
        list.add(category)
        val json = gson.toJson(list, type)
        file.writeText(json)
    }

    /**
     * Loads the list of categories for the current user.
     * Called from CategoryActivity and other screens that display categories.
     */
    fun loadCategories(context: Context, username: String): List<Category> {
        val fileName = "${username}_categories.json"
        val file = getFile(context, fileName)
        if (!file.exists()) {
            return emptyList()
        }
        val gson = Gson()
        val type = object : TypeToken<List<Category>>() {}.type
        return gson.fromJson(file.readText(), type)
    }

    /**
     * Saves an Expense object by appending it to username_expenses.json.
     * Called from ExpenseActivity.
     */
    fun saveExpense(context: Context, username: String, expense: Expense) {
        val fileName = "${username}_expenses.json"
        val file = getFile(context, fileName)
        val gson = Gson()
        val type = object : TypeToken<MutableList<Expense>>() {}.type
        val list: MutableList<Expense> = if (file.exists()) {
            gson.fromJson(file.readText(), type)
        } else {
            mutableListOf()
        }
        list.add(expense)
        val json = gson.toJson(list, type)
        file.writeText(json)
    }

    /**
     * Loads the list of expenses for the current user.
     * Used in ExpenseListActivity and CategoryTotalActivity.
     */
    fun loadExpenses(context: Context, username: String): List<Expense> {
        val fileName = "${username}_expenses.json"
        val file = getFile(context, fileName)
        if (!file.exists()) {
            return emptyList()
        }
        val gson = Gson()
        val type = object : TypeToken<List<Expense>>() {}.type
        return gson.fromJson(file.readText(), type)
    }

    /**
     * Saves a Goal object to username_goals.json.
     * Called from GoalActivity.
     */
    fun saveGoal(context: Context, username: String, goal: Goal) {
        val fileName = "${username}_goals.json"
        val file = getFile(context, fileName)
        val gson = Gson()
        val json = gson.toJson(goal)
        file.writeText(json)
    }

    /**
     * Loads the Goal object for the current user.
     * Used in GoalActivity and dashboard calculations.
     */
    fun loadGoal(context: Context, username: String): Goal? {
        val fileName = "${username}_goals.json"
        val file = getFile(context, fileName)
        if (!file.exists()) {
            return null
        }
        val gson = Gson()
        return gson.fromJson(file.readText(), Goal::class.java)
    }
}