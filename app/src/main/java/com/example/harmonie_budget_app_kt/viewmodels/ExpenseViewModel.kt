package com.example.harmonie_budget_app_kt.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.harmonie_budget_app_kt.db.DatabaseRepository
import com.example.harmonie_budget_app_kt.models.Expense

/**
 * ExpenseViewModel manages data and business logic for expense operations.
 *
 * It uses DatabaseRepository to perform direct SQLite database reads and writes,
 * ensuring relational data integrity and transaction persistence.
 */
class ExpenseViewModel : ViewModel()
{
    /**
     * Retrieves the list of expenses for the specified user from the database.
     */
    fun getExpenses(context: Context, username: String): List<Expense>
    {
        val repository = DatabaseRepository(context)
        return repository.getExpenses(username)
    }

    /**
     * Saves a new expense for the user into the database repository.
     */
    fun saveExpense(context: Context, username: String, expense: Expense): Boolean
    {
        val repository = DatabaseRepository(context)
        return repository.saveExpense(username, expense)
    }

    /**
     * Updates an existing expense record in the database repository.
     */
    fun updateExpense(context: Context, username: String, updatedExpense: Expense): Boolean
    {
        val repository = DatabaseRepository(context)
        return repository.saveExpense(username, updatedExpense)
    }

    /**
     * Deletes an expense by ID from the database repository.
     */
    fun deleteExpense(context: Context, username: String, expenseId: Int): Boolean
    {
        val repository = DatabaseRepository(context)
        return repository.deleteExpense(username, expenseId)
    }
}