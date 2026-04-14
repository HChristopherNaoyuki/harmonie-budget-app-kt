package com.example.harmonie_budget_app_kt.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.utils.JsonHelper

/**
 * CategoryViewModel manages data and logic for category-related screens.
 * It calls JsonHelper methods to load and save categories.
 * This follows the MVVM pattern by separating UI logic from data access.
 * All calls to JsonHelper are now routed through this ViewModel.
 */
class CategoryViewModel : ViewModel()
{
    private val jsonHelper = JsonHelper()

    /**
     * Returns the list of categories for the given user.
     * Context is required by JsonHelper.
     */
    fun getCategories(context: Context, username: String): List<Category>
    {
        return jsonHelper.loadCategories(context, username)
    }

    /**
     * Saves a new category for the user.
     * Context is required by JsonHelper.
     */
    fun saveCategory(context: Context, username: String, category: Category)
    {
        jsonHelper.saveCategory(context, username, category)
    }
}