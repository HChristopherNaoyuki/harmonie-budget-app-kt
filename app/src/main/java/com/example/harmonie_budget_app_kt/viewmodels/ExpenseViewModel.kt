package com.example.harmonie_budget_app_kt.viewmodels

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
     * This method is called from ExpenseActivity and ExpenseListActivity.
     */
    fun getExpenses(username: String): List<Expense>
    {
        return jsonHelper.loadExpenses(/* context not needed in this call pattern */)
    }

    /**
     * Saves a new expense for the user.
     * This method is called from ExpenseActivity when the user submits an expense.
     */
    fun saveExpense(username: String, expense: Expense)
    {
        jsonHelper.saveExpense(/* context not needed in this call pattern */, username, expense)
    }
}