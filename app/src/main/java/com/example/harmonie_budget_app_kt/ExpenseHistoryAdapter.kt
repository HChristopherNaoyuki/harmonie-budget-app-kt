package com.example.harmonie_budget_app_kt

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.models.Expense
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * ExpenseHistoryAdapter is a RecyclerView adapter for displaying expense records.
 * Uses ListAdapter with DiffUtil for efficient updates.
 *
 * Part 3 Enhancement:
 * - Updated display format to match the mockup design.
 * - Shows category, description, amount, transaction date, start time, end time,
 *   submission date, and a receipt button when applicable.
 * - Time format uses 24-hour format (HH:mm) as required.
 *
 * @param categories The list of Category objects for resolving category names
 */
class ExpenseHistoryAdapter(
    private val categories: List<Category>
) : ListAdapter<Expense, ExpenseHistoryAdapter.ViewHolder>(ExpenseDiffCallback())
{
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

        // Find category name, default to "General" if not found
        val categoryName = categories.find { it.id == expense.categoryId }?.name ?: "General"

        // Format: "Category - Description" (e.g., "Transport - Taxi to Joburg CBD")
        val categoryDescriptionText = "$categoryName - ${expense.description}"
        holder.tvCategoryDescription.text = categoryDescriptionText

        // Format transaction date as "MMM dd, yyyy" (e.g., "May 20, 2025")
        val transactionDateFormatted = formatDate(expense.date)
        holder.tvTransactionDate.text = transactionDateFormatted

        // Format start time as "Start HH:mm" using 24-hour format
        val startTimeText = "Start ${expense.startTime}"
        holder.tvStartTime.text = startTimeText

        // Format end time as "HH:mm" using 24-hour format (no AM/PM conversion)
        val endTimeFormatted = expense.endTime
        holder.tvEndTime.text = endTimeFormatted

        // Format submission date as "Recorded on: MMM dd, yyyy"
        val submissionDateText = "Recorded on: ${formatDate(expense.date)}"
        holder.tvSubmissionDate.text = submissionDateText

        // Format amount with ZAR currency (negative value in red)
        val formattedAmount = String.format(Locale.US, "-R %,.2f", expense.amount)
        holder.tvAmount.text = formattedAmount
        holder.tvAmount.setTextColor(holder.itemView.context.getColor(android.R.color.holo_red_dark))

        // Handle receipt photo visibility and click
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
     * @return Formatted date string like "MMM dd, yyyy" (e.g., "May 20, 2025")
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
     * Handles both content:// and file:// URI schemes.
     * Uses FileProvider for file URIs to ensure proper permissions.
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

            if (photoUri.scheme == "file")
            {
                val photoFile = File(photoUri.path ?: "")
                if (photoFile.exists())
                {
                    val contentUri = FileProvider.getUriForFile(
                        itemView.context,
                        "${itemView.context.packageName}.fileprovider",
                        photoFile
                    )
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(contentUri, "image/*")
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    itemView.context.startActivity(intent)
                }
                else
                {
                    Toast.makeText(itemView.context, "Receipt file not found", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(photoUri, "image/*")
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                itemView.context.startActivity(intent)
            }
        }
        catch (exception: Exception)
        {
            Toast.makeText(itemView.context, "Unable to open receipt photo", Toast.LENGTH_SHORT).show()
        }
    }
}