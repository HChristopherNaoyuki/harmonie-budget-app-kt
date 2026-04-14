package com.example.harmonie_budget_app_kt.viewmodels

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
     * This method is called from MoreFragment when the user chooses to export data.
     */
    fun exportData(username: String): Boolean
    {
        return jsonHelper.exportData(/* context not needed in this call pattern */)
    }

    /**
     * Resets the user's progress data.
     * This method is called from MoreFragment when the user chooses to reset progress.
     */
    fun resetProgress(username: String): Boolean
    {
        return jsonHelper.resetProgress(/* context not needed in this call pattern */)
    }
}