package com.example.harmonie_budget_app_kt.utils

import android.content.Context
import com.example.harmonie_budget_app_kt.models.Badge
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.models.Goal
import com.example.harmonie_budget_app_kt.models.StreakData
import com.example.harmonie_budget_app_kt.models.User
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

/**
 * JsonHelper handles all JSON file operations for the application.
 * It manages user data, categories, expenses, goals, and Part 3 gamification data (badges and streaks).
 * All data is stored in the budget_data folder within the app's internal storage.
 * Each user has their own set of files identified by their username.
 */
class JsonHelper
{
    private val gson: Gson by lazy { Gson() }

    private fun getFile(context: Context, fileName: String): File
    {
        val folder = File(context.filesDir, "budget_data")
        if (!folder.exists())
        {
            folder.mkdirs()
        }
        return File(folder, fileName)
    }

    // ==================== User Methods ====================

    fun saveUser(context: Context, user: User)
    {
        val file = getFile(context, "${user.username}.json")
        val json = gson.toJson(user)
        file.writeText(json)
    }

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
        list.add(category)
        file.writeText(gson.toJson(list))
    }

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

    fun saveGoal(context: Context, username: String, goal: Goal)
    {
        val file = getFile(context, "${username}_goals.json")
        val json = gson.toJson(goal)
        file.writeText(json)
    }

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

    // ==================== Part 3: Gamification - Badge Methods ====================

    /**
     * Saves a badge for the user.
     * Badges are stored in a list to support multiple achievements.
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

        // Prevent duplicate badges
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

    // ==================== Part 3: Gamification - Streak Methods ====================

    /**
     * Saves streak data for the user.
     *
     * @param context Application context
     * @param username User identifier
     * @param streakData Streak data to save
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

    // ==================== Existing Export and Reset Methods ====================

    fun exportData(context: Context, username: String): Boolean
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
                try
                {
                    source.copyTo(dest, overwrite = true)
                }
                catch (_: Exception)
                {
                    return false
                }
            }
        }
        return true
    }

    fun resetProgress(context: Context, username: String): Boolean
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
        return true
    }
}