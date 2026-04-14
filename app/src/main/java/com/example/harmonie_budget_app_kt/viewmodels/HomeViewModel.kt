package com.example.harmonie_budget_app_kt.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.utils.JsonHelper

/**
 * HomeViewModel manages data and logic for the HomeFragment.
 * It calls JsonHelper methods to load user expenses and calculate totals.
 * This follows the MVVM pattern by separating UI logic from data access.
 * All calls to JsonHelper are now routed through this ViewModel.
 */
class HomeViewModel : ViewModel()
{
    private val jsonHelper = JsonHelper()

    /**
     * Returns the total balance spent by the user.
     * It loads all expenses and sums the amount field.
     * Context is required by JsonHelper.
     */
    fun getTotalBalance(context: Context, username: String): Double
    {
        val expenses = jsonHelper.loadExpenses(context, username)
        return expenses.sumOf { it.amount }
    }

    /**
     * Returns the list of all expenses for the user.
     * This is used for category breakdown calculations in the HomeFragment.
     * Context is required by JsonHelper.
     */
    fun getAllExpenses(context: Context, username: String): List<Expense>
    {
        return jsonHelper.loadExpenses(context, username)
    }
}