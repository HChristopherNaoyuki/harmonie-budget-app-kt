package com.example.harmonie_budget_app_kt

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

    class ViewHolder(val textView: TextView) : RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val textView = TextView(parent.context)
        textView.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        textView.setPadding(16, 16, 16, 16)
        textView.setTextColor(parent.context.getColor(R.color.text_primary_light))
        textView.textSize = 14f
        return ViewHolder(textView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int)
    {
        val expense = getItem(position)

        val categoryName = categories.find { it.id == expense.categoryId }?.name ?: "Unknown"

        val submissionTime = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            .format(Calendar.getInstance().time)

        val displayText = holder.textView.context.getString(
            R.string.expense_history_row_format,
            String.format(Locale.US, "%.2f", expense.amount),
            expense.date,
            categoryName,
            submissionTime,
            expense.startTime,
            expense.endTime
        )

        holder.textView.text = displayText
    }
}