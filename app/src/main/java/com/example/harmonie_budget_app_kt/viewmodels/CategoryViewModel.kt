package com.example.harmonie_budget_app_kt.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.harmonie_budget_app_kt.db.DatabaseRepository
import com.example.harmonie_budget_app_kt.models.Category

/**
 * CategoryViewModel manages data and business logic for category operations.
 *
 * It uses DatabaseRepository to perform direct SQLite database reads and writes,
 * guaranteeing category isolation per user account.
 */
class CategoryViewModel : ViewModel()
{
    /**
     * Retrieves the list of categories for the specified user from the database.
     */
    fun getCategories(context: Context, username: String): List<Category>
    {
        val repository = DatabaseRepository(context)
        return repository.getCategories(username)
    }

    /**
     * Saves a new category for the user into the database repository.
     */
    fun saveCategory(context: Context, username: String, category: Category): Boolean
    {
        val repository = DatabaseRepository(context)
        return repository.saveCategory(username, category)
    }

    /**
     * Updates an existing category record in the database repository.
     */
    fun updateCategory(context: Context, username: String, updatedCategory: Category): Boolean
    {
        val repository = DatabaseRepository(context)
        return repository.saveCategory(username, updatedCategory)
    }

    /**
     * Deletes a category by ID from the database repository.
     */
    fun deleteCategory(context: Context, username: String, categoryId: Int): Boolean
    {
        val repository = DatabaseRepository(context)
        return repository.deleteCategory(username, categoryId)
    }
}