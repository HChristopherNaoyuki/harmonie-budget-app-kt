package com.example.harmonie_budget_app_kt.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.utils.JsonHelper

/**
 * CategoryViewModel manages data and business logic for category operations.
 *
 * It serves as an intermediary between the user interface and the local JSON data layer.
 * All operations are delegated to JsonHelper to perform atomic operations on internal storage.
 */
class CategoryViewModel : ViewModel()
{
    private val jsonHelper = JsonHelper()

    /**
     * Retrieves the list of categories for the specified user.
     *
     * @param context Application context required for file access
     * @param username Unique identifier of the logged-in user
     * @return List of categories associated with the user
     */
    fun getCategories(context: Context, username: String): List<Category>
    {
        return jsonHelper.loadCategories(context, username)
    }

    /**
     * Saves a new category for the user after validation.
     *
     * @param context Application context required for file access
     * @param username Unique identifier of the logged-in user
     * @param category Category model instance to persist
     * @return True if the category was saved successfully, false otherwise
     */
    fun saveCategory(context: Context, username: String, category: Category): Boolean
    {
        return jsonHelper.saveCategory(context, username, category)
    }

    /**
     * Updates an existing category for the user.
     *
     * @param context Application context required for file access
     * @param username Unique identifier of the logged-in user
     * @param updatedCategory Category model instance with updated field values
     * @return True if the category was updated successfully, false otherwise
     */
    fun updateCategory(context: Context, username: String, updatedCategory: Category): Boolean
    {
        return jsonHelper.updateCategory(context, username, updatedCategory)
    }

    /**
     * Deletes a category by its unique identifier.
     *
     * @param context Application context required for file access
     * @param username Unique identifier of the logged-in user
     * @param categoryId Numeric ID of the category to remove
     * @return True if the category was removed successfully, false otherwise
     */
    fun deleteCategory(context: Context, username: String, categoryId: Int): Boolean
    {
        return jsonHelper.deleteCategory(context, username, categoryId)
    }
}