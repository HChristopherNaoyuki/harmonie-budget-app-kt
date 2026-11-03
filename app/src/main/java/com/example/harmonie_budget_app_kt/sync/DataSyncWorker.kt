package com.example.harmonie_budget_app_kt.sync

import android.content.Context
import android.util.Log

/**
 * DataSyncWorker provides a sync execution bridge for background database operations.
 */
class DataSyncWorker(private val context: Context)
{
    companion object
    {
        private const val TAG = "DataSyncWorker"
    }

    private val syncManager = SyncManager(context)

    /**
     * Executes background synchronization of dirty database records.
     */
    fun performSync(): Boolean
    {
        Log.d(TAG, "Executing scheduled database synchronization.")
        return syncManager.performDatabaseSync()
    }
}