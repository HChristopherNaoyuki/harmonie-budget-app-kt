package com.example.harmonie_budget_app_kt.utils

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.harmonie_budget_app_kt.models.User
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.models.Goal
import java.io.File

object JsonHelper {

    private const val FOLDER_NAME = "budget_data"

    private fun getFile(context: Context, fileName: String): File {
        val folder = File(context.filesDir, FOLDER_NAME)
        if (!folder.exists()) {
            folder.mkdirs()
        }
        return File(folder, fileName)
    }

    fun saveUser(context: Context, user: User) {
        val file = getFile(context, "${user.username}.json")
        val gson = Gson()
        val json = gson.toJson(user)
        file.writeText(json)
    }

    fun loadUser(context: Context, username: String): User? {
        val file = getFile(context, "$username.json")
        if (!file.exists()) return null
        val gson = Gson()
        return gson.fromJson(file.readText(), User::class.java)
    }

    fun saveCategory(context: Context, username: String, category: Category) {
        val file = getFile(context, "${username}_categories.json")
        val gson = Gson()
        val type = object : TypeToken<List<Category>>() {}.type
        val list = loadCategories(context, username).toMutableList()
        list.add(category)
        val json = gson.toJson(list, type)
        file.writeText(json)
    }

    fun loadCategories(context: Context, username: String): List<Category> {
        val file = getFile(context, "${username}_categories.json")
        if (!file.exists()) return emptyList()
        val gson = Gson()
        val type = object : TypeToken<List<Category>>() {}.type
        return gson.fromJson(file.readText(), type) ?: emptyList()
    }

    fun saveExpense(context: Context, username: String, expense: Expense) {
        val file = getFile(context, "${username}_expenses.json")
        val gson = Gson()
        val type = object : TypeToken<List<Expense>>() {}.type
        val list = loadExpenses(context, username).toMutableList()
        list.add(expense)
        val json = gson.toJson(list, type)
        file.writeText(json)
    }

    fun loadExpenses(context: Context, username: String): List<Expense> {
        val file = getFile(context, "${username}_expenses.json")
        if (!file.exists()) return emptyList()
        val gson = Gson()
        val type = object : TypeToken<List<Expense>>() {}.type
        return gson.fromJson(file.readText(), type) ?: emptyList()
    }

    fun saveGoal(context: Context, username: String, goal: Goal) {
        val file = getFile(context, "${username}_goals.json")
        val gson = Gson()
        val json = gson.toJson(goal)
        file.writeText(json)
    }

    fun loadGoal(context: Context, username: String): Goal? {
        val file = getFile(context, "${username}_goals.json")
        if (!file.exists()) return null
        val gson = Gson()
        return gson.fromJson(file.readText(), Goal::class.java)
    }
}