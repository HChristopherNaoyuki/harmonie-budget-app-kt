package com.example.harmonie_budget_app_kt.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.harmonie_budget_app_kt.utils.JsonHelper

/**
 * MoreViewModel manages data and logic for the MoreFragment.
 * It calls JsonHelper methods for export and reset operations.
 * This follows the MVVM pattern by separating UI logic from data access.
 * All calls to JsonHelper are now routed through this ViewModel.
 */
class MoreViewModel : ViewModel()
{
    private val jsonHelper = JsonHelper()

    /**
     * Exports the user's data.
     * Context is required by JsonHelper.
     */
    fun exportData(context: Context, username: String): Boolean
    {
        return jsonHelper.exportData(context, username)
    }

    /**
     * Resets the user's progress data.
     * Context is required by JsonHelper.
     */
    fun resetProgress(context: Context, username: String): Boolean
    {
        return jsonHelper.resetProgress(context, username)
    }
}