package com.example.harmonie_budget_app_kt.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.utils.JsonHelper

/**
 * ExpenseViewModel manages data and business logic for expense operations.
 *
 * It serves as an intermediary between the user interface and the local JSON data layer.
 * All operations are delegated to JsonHelper to perform atomic operations on internal storage.
 */
class ExpenseViewModel : ViewModel()
{
    private val jsonHelper = JsonHelper()

    /**
     * Retrieves the list of expenses for the specified user.
     *
     * @param context Application context required for file access
     * @param username Unique identifier of the logged-in user
     * @return List of expenses associated with the user
     */
    fun getExpenses(context: Context, username: String): List<Expense>
    {
        return jsonHelper.loadExpenses(context, username)
    }

    /**
     * Saves a new expense for the user after validation.
     *
     * @param context Application context required for file access
     * @param username Unique identifier of the logged-in user
     * @param expense Expense model instance to persist
     * @return True if the expense was saved successfully, false otherwise
     */
    fun saveExpense(context: Context, username: String, expense: Expense): Boolean
    {
        return jsonHelper.saveExpense(context, username, expense)
    }

    /**
     * Updates an existing expense for the user.
     *
     * @param context Application context required for file access
     * @param username Unique identifier of the logged-in user
     * @param updatedExpense Expense model instance with updated field values
     * @return True if the expense was updated successfully, false otherwise
     */
    fun updateExpense(context: Context, username: String, updatedExpense: Expense): Boolean
    {
        return jsonHelper.updateExpense(context, username, updatedExpense)
    }

    /**
     * Deletes an expense by its unique identifier.
     *
     * @param context Application context required for file access
     * @param username Unique identifier of the logged-in user
     * @param expenseId Numeric ID of the expense to remove
     * @return True if the expense was removed successfully, false otherwise
     */
    fun deleteExpense(context: Context, username: String, expenseId: Int): Boolean
    {
        return jsonHelper.deleteExpense(context, username, expenseId)
    }
}