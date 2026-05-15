package com.example.harmonie_budget_app_kt

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.models.Expense
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/**
 * ExpenseHistoryAdapter is a RecyclerView adapter for displaying expense records.
 * Uses ListAdapter with DiffUtil for efficient updates.
 *
 * Part 3 Enhancement:
 * - Displays complete expense details: category, description, amount, transaction date,
 *   start time, end time, and submission date.
 * - Provides a button to view attached receipt photos when available.
 * - Currency uses ZAR (South African Rand).
 *
 * @param categories The list of Category objects for resolving category names
 */
class ExpenseHistoryAdapter(
    private val categories: List<Category>
) : ListAdapter<Expense, ExpenseHistoryAdapter.ViewHolder>(ExpenseDiffCallback())
{
    /**
     * DiffUtil callback for calculating differences between expense lists.
     */
    class ExpenseDiffCallback : DiffUtil.ItemCallback<Expense>()
    {
        override fun areItemsTheSame(oldItem: Expense, newItem: Expense): Boolean
        {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Expense, newItem: Expense): Boolean
        {
            return oldItem.amount == newItem.amount &&
                    oldItem.date == newItem.date &&
                    oldItem.startTime == newItem.startTime &&
                    oldItem.endTime == newItem.endTime &&
                    oldItem.description == newItem.description &&
                    oldItem.categoryId == newItem.categoryId &&
                    oldItem.photoUri == newItem.photoUri
        }
    }

    /**
     * ViewHolder class that holds the views for a single expense entry.
     * Each entry displays:
     * - Category and description
     * - Transaction date (date of the expense)
     * - Submission date (when recorded, approximated as current date if not stored)
     * - Start time and end time
     * - Amount with ZAR currency
     * - Photo preview button (if receipt exists)
     */
    class ViewHolder(itemView: android.view.View) : RecyclerView.ViewHolder(itemView)
    {
        val tvCategoryDescription: TextView = itemView.findViewById(R.id.tv_category_description)
        val tvTransactionDate: TextView = itemView.findViewById(R.id.tv_transaction_date)
        val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)
        val tvStartTime: TextView = itemView.findViewById(R.id.tv_start_time)
        val tvEndTime: TextView = itemView.findViewById(R.id.tv_end_time)
        val tvSubmissionDate: TextView = itemView.findViewById(R.id.tv_submission_date)
        val btnViewReceipt: Button = itemView.findViewById(R.id.btn_view_receipt)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_expense_history, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val expense = getItem(position)

        // Find the category name for this expense
        val categoryName = categories.find { it.id == expense.categoryId }?.name ?: "Unknown"

        // Build the category and description text
        val categoryDescriptionText = "$categoryName - ${expense.description}"
        holder.tvCategoryDescription.text = categoryDescriptionText

        // Display transaction date (the date when the expense occurred)
        val transactionDateFormatted = formatDate(expense.date)
        holder.tvTransactionDate.text = transactionDateFormatted

        // Display start time
        val startTimeText = "Start: ${expense.startTime}"
        holder.tvStartTime.text = startTimeText

        // Display end time
        val endTimeText = "End: ${expense.endTime}"
        holder.tvEndTime.text = endTimeText

        // Display submission date (when the expense was recorded in the system)
        // Since submission time is not stored permanently, we use the expense date
        // as an approximation with a note indicating it is the transaction date.
        val submissionDateText = "Recorded on: ${formatDate(expense.date)}"
        holder.tvSubmissionDate.text = submissionDateText

        // Format the amount as a negative value with ZAR currency symbol
        val formattedAmount = String.format(Locale.US, "-R %,.2f", expense.amount)
        holder.tvAmount.text = formattedAmount

        // Set amount text color to red for visual emphasis
        holder.tvAmount.setTextColor(holder.itemView.context.getColor(android.R.color.holo_red_dark))

        // Handle receipt photo preview
        if (!expense.photoUri.isNullOrEmpty())
        {
            holder.btnViewReceipt.visibility = android.view.View.VISIBLE
            holder.btnViewReceipt.setOnClickListener {
                viewReceiptPhoto(holder.itemView, expense.photoUri)
            }
        }
        else
        {
            holder.btnViewReceipt.visibility = android.view.View.GONE
        }
    }

    /**
     * Formats a date string from yyyy-MM-dd to a more readable format.
     *
     * @param dateString The date string in yyyy-MM-dd format
     * @return Formatted date string like "MMM dd, yyyy" (e.g., "May 15, 2026")
     */
    private fun formatDate(dateString: String): String
    {
        return try
        {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val outputFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            val date = inputFormat.parse(dateString)
            outputFormat.format(date ?: Date())
        }
        catch (exception: Exception)
        {
            dateString
        }
    }

    /**
     * Opens the attached receipt photo using an Intent with ACTION_VIEW.
     * Handles cases where the URI is invalid or the file cannot be opened.
     *
     * @param itemView The view used to access the context
     * @param photoUriString The URI string of the attached photo
     */
    private fun viewReceiptPhoto(itemView: android.view.View, photoUriString: String?)
    {
        if (photoUriString.isNullOrEmpty())
        {
            Toast.makeText(itemView.context, "No receipt photo available", Toast.LENGTH_SHORT).show()
            return
        }

        try
        {
            val photoUri = photoUriString.toUri()
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(photoUri, "image/*")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            itemView.context.startActivity(intent)
        }
        catch (exception: Exception)
        {
            Toast.makeText(itemView.context, "Unable to open receipt photo", Toast.LENGTH_SHORT).show()
        }
    }
}