package com.example.harmonie_budget_app_kt.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.utils.JsonHelper

/**
 * ExpenseViewModel manages data and logic for expense-related screens.
 * It calls JsonHelper methods to load and save expenses.
 * This follows the MVVM pattern by separating UI logic from data access.
 * All calls to JsonHelper are now routed through this ViewModel.
 */
class ExpenseViewModel : ViewModel()
{
    private val jsonHelper = JsonHelper()

    /**
     * Returns the list of expenses for the given user.
     * Context is required by JsonHelper.
     */
    fun getExpenses(context: Context, username: String): List<Expense>
    {
        return jsonHelper.loadExpenses(context, username)
    }

    /**
     * Saves a new expense for the user.
     * Context is required by JsonHelper.
     */
    fun saveExpense(context: Context, username: String, expense: Expense)
    {
        jsonHelper.saveExpense(context, username, expense)
    }
}