package com.example.harmonie_budget_app_kt.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File

object JsonHelper
{
    private val gson = Gson()

    private fun getFile(context: Context, fileName: String): File
    {
        // Create dedicated budget_data folder if it does not exist (Part 2 requirement)
        val folder = File(context.filesDir, "budget_data")
        if (!folder.exists())
        {
            folder.mkdirs()
        }
        return File(folder, fileName)
    }

    // Required for Part 2 user isolation (OPSC6311POE.pdf page 7)
    fun saveUser(context: Context, user: User)
    {
        val file = getFile(context, "${user.username}.json")
        val json = gson.toJson(user)
        file.writeText(json)
    }

    // Required for Part 2 user isolation (OPSC6311POE.pdf page 7)
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
        list.add(expense)
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

    // Required for Part 2 budget goals (OPSC6311POE.pdf page 7)
    fun saveGoal(context: Context, username: String, goal: Goal)
    {
        val file = getFile(context, "${username}_goals.json")
        val json = gson.toJson(goal)
        file.writeText(json)
    }

    // Required for Part 2 budget goals (OPSC6311POE.pdf page 7)
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
}