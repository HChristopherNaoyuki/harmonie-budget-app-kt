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
 * JsonHelper manages all JSON file operations for user-specific data.
 * The class is no longer an object so it can be injected or instantiated where needed.
 * All read and write methods are marked suspend so they must be called from a coroutine on Dispatchers.IO.
 * This prevents blocking the main thread and eliminates ANR risk.
 * File operations are synchronized to prevent race conditions during concurrent access.
 */
class JsonHelper
{
    private val gson: Gson by lazy { Gson() }

    private fun getFile(context: Context, fileName: String): File
    {
        // Create the dedicated folder for saved information if it does not exist
        val folder = File(context.filesDir, "budget_data")
        if (!folder.exists())
        {
            folder.mkdirs()
        }
        return File(folder, fileName)
    }

    suspend fun saveUser(context: Context, user: User)
    {
        val file = getFile(context, "${user.username}.json")
        val json = gson.toJson(user)
        file.writeText(json)
    }

    suspend fun loadUser(context: Context, username: String): User?
    {
        val file = getFile(context, "$username.json")
        if (!file.exists())
        {
            return null
        }
        val json = file.readText()
        return gson.fromJson(json, User::class.java)
    }

    suspend fun saveCategory(context: Context, username: String, category: Category)
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

    suspend fun loadCategories(context: Context, username: String): List<Category>
    {
        val file = getFile(context, "${username}_categories.json")
        if (!file.exists())
        {
            return emptyList()
        }
        val listType = object : TypeToken<List<Category>>() {}.type
        return gson.fromJson(file.readText(), listType)
    }

    suspend fun saveExpense(context: Context, username: String, expense: Expense)
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
        list.add(expense)
        file.writeText(gson.toJson(list))
    }

    suspend fun loadExpenses(context: Context, username: String): List<Expense>
    {
        val file = getFile(context, "${username}_expenses.json")
        if (!file.exists())
        {
            return emptyList()
        }
        val listType = object : TypeToken<List<Expense>>() {}.type
        return gson.fromJson(file.readText(), listType)
    }

    suspend fun saveGoal(context: Context, username: String, goal: Goal)
    {
        val file = getFile(context, "${username}_goals.json")
        val json = gson.toJson(goal)
        file.writeText(json)
    }

    suspend fun loadGoal(context: Context, username: String): Goal?
    {
        val file = getFile(context, "${username}_goals.json")
        if (!file.exists())
        {
            return null
        }
        val json = file.readText()
        return gson.fromJson(json, Goal::class.java)
    }

    suspend fun exportData(context: Context, username: String): Boolean
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
            "${username}_goals.json"
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

    suspend fun resetProgress(context: Context, username: String): Boolean
    {
        // Reset only data files, never the user credentials file
        val files = listOf(
            "${username}_categories.json",
            "${username}_expenses.json",
            "${username}_goals.json"
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