package com.example.harmonie_budget_app_kt

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.models.Expense
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * ExpenseHistoryAdapter is a RecyclerView adapter for displaying expense records.
 * Uses ListAdapter with DiffUtil for efficient updates.
 *
 * Part 3 Enhancement:
 * - Updated the visual format of each expense entry to match the mockup design.
 * - Each entry now shows: category, description, amount, and relative date.
 * - Format example: "Groceries - Whole Foods    -$87.42" with "Today" or date below.
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
     * - Primary text: Category and description (e.g., "Groceries - Whole Foods")
     * - Secondary text: Relative date (Today, Yesterday, or formatted date)
     * - Amount: Negative amount in red (e.g., "-$87.42")
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        val tvCategoryDescription: TextView = itemView.findViewById(R.id.tv_category_description)
        val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)
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

        // Format the date as a relative string (Today, Yesterday, or formatted date)
        val relativeDate = getRelativeDateString(expense.date)
        holder.tvDate.text = relativeDate

        // Format the amount as a negative value with dollar sign
        val formattedAmount = String.format(Locale.US, "-$%.2f", expense.amount)
        holder.tvAmount.text = formattedAmount

        // Set amount text color to red for visual emphasis of expenses
        holder.tvAmount.setTextColor(holder.itemView.context.getColor(android.R.color.holo_red_dark))
    }

    /**
     * Converts a date string to a relative display format.
     *
     * @param dateString The expense date in yyyy-MM-dd format
     * @return "Today" if the date is today, "Yesterday" if yesterday,
     *         otherwise the formatted date as "MMM dd"
     */
    private fun getRelativeDateString(dateString: String): String
    {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val expenseDate = dateFormat.parse(dateString)

        val calendar = Calendar.getInstance()
        val todayDate = dateFormat.format(calendar.time)

        // Check if the expense date is today
        if (dateString == todayDate)
        {
            return "Today"
        }

        // Check if the expense date is yesterday
        calendar.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayDate = dateFormat.format(calendar.time)
        if (dateString == yesterdayDate)
        {
            return "Yesterday"
        }

        // Otherwise return formatted date (e.g., "May 12")
        val displayFormat = SimpleDateFormat("MMM dd", Locale.getDefault())
        return displayFormat.format(expenseDate ?: return dateString)
    }
}