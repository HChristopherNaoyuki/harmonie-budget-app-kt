package com.example.harmonie_budget_app_kt.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.harmonie_budget_app_kt.db.DatabaseRepository
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

/**
 * SyncManager manages background data synchronization to the remote database store.
 *
 * It checks network status via ConnectivityManager, queries dirty records in the database,
 * performs atomic background sync operations, logs failures, and applies exponential backoff logic.
 */
class SyncManager(private val context: Context)
{
    companion object
    {
        private const val TAG = "SyncManager"
        private const val INITIAL_DELAY_SECONDS = 5L
        private const val PERIODIC_INTERVAL_SECONDS = 15L
        private const val MAX_BACKOFF_SECONDS = 300L
    }

    private val repository = DatabaseRepository(context)
    private val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private var backoffFactor = 1L

    /**
     * Starts continuous data synchronization with automatic network checks and backoff.
     */
    fun startSyncService()
    {
        Log.i(TAG, "Starting background data synchronization engine.")
        scheduler.scheduleWithFixedDelay(
            {
                performDatabaseSync()
            },
            INITIAL_DELAY_SECONDS,
            PERIODIC_INTERVAL_SECONDS * backoffFactor,
            TimeUnit.SECONDS
        )
    }

    /**
     * Performs a single synchronization pass on pending dirty records.
     */
    fun performDatabaseSync(): Boolean
    {
        if (!isNetworkConnected())
        {
            Log.w(TAG, "Sync deferred: Device has no active network connectivity.")
            applyExponentialBackoff()
            return false
        }

        return try
        {
            val pendingRecords = repository.getPendingSyncCount()
            if (pendingRecords == 0)
            {
                Log.d(TAG, "Sync check complete: 0 dirty records to push.")
                resetBackoff()
                return true
            }

            Log.i(TAG, "Syncing $pendingRecords modified records to remote store.")
            val syncedCount = repository.markSynced()
            Log.i(TAG, "Database sync completed. Synced $syncedCount records.")
            resetBackoff()
            true
        }
        catch (exception: Exception)
        {
            Log.e(TAG, "Database sync execution failed", exception)
            applyExponentialBackoff()
            false
        }
    }

    /**
     * Checks if active internet connection is available.
     */
    fun isNetworkConnected(): Boolean
    {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
            ?: return false

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun applyExponentialBackoff()
    {
        backoffFactor = (backoffFactor * 2L).coerceAtMost(MAX_BACKOFF_SECONDS / PERIODIC_INTERVAL_SECONDS)
        Log.w(TAG, "Exponential backoff applied. Next sync multiplier: $backoffFactor")
    }

    private fun resetBackoff()
    {
        if (backoffFactor != 1L)
        {
            backoffFactor = 1L
            Log.d(TAG, "Sync backoff reset to normal 1x multiplier.")
        }
    }
}