package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.models.Goal
import com.example.harmonie_budget_app_kt.utils.JsonHelper
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class HomeFragment : Fragment()
{
    private lateinit var username: String
    private lateinit var tvGreeting: TextView
    private lateinit var tvCurrentDate: TextView
    private lateinit var tvBudgetRange: TextView
    private lateinit var tvTotalBalance: TextView
    private lateinit var tvBudgetStatus: TextView
    private lateinit var rvCategoryBreakdown: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Username is received from DashboardActivity arguments
        // This ensures user-specific data isolation for all operations
        username = arguments?.getString("username") ?: "admin"

        tvGreeting = view.findViewById(R.id.tv_greeting)
        tvCurrentDate = view.findViewById(R.id.tv_current_date)
        tvBudgetRange = view.findViewById(R.id.tv_budget_range)
        tvTotalBalance = view.findViewById(R.id.tv_total_balance)
        tvBudgetStatus = view.findViewById(R.id.tv_budget_status)
        rvCategoryBreakdown = view.findViewById(R.id.rv_category_breakdown)

        // Set greeting using string resource with placeholder
        tvGreeting.text = getString(R.string.greetings, username)

        // Display current date in human-readable format
        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        tvCurrentDate.text = dateFormat.format(calendar.time)

        // Load user goals and display min/max budget using string resource with placeholders
        val goal = JsonHelper.loadGoal(requireContext(), username)
        if (goal != null)
        {
            tvBudgetRange.text = getString(R.string.monthly_budget, goal.minGoal, goal.maxGoal)
        }
        else
        {
            // Use existing resource to avoid any string literal in setText
            tvBudgetRange.text = getString(R.string.set_monthly_goals)
        }

        // Calculate total balance spent for the current month from expense records
        val expenses = JsonHelper.loadExpenses(requireContext(), username)
        val currentMonthTotal = calculateCurrentMonthTotal(expenses)
        tvTotalBalance.text = getString(R.string.total_balance, currentMonthTotal)

        // Determine budget status and set using string resource
        val status = determineBudgetStatus(currentMonthTotal, goal)
        tvBudgetStatus.text = status

        // Display all-time spending per category breakdown
        rvCategoryBreakdown.layoutManager = LinearLayoutManager(requireContext())

        // Pre-format all strings in the fragment using getString
        // This prevents any string literals from appearing in the adapter
        val categoryBreakdown = calculateCategoryBreakdown(expenses)
        rvCategoryBreakdown.adapter = CategoryBreakdownAdapter(categoryBreakdown)

        return view
    }

    // Helper function to calculate total spent in the current month
    private fun calculateCurrentMonthTotal(expenses: List<Expense>): Double
    {
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)

        return expenses.filter { expense ->
            val expenseDate = try
            {
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(expense.date)
            }
            catch (_: Exception)
            {
                null
            }
            if (expenseDate != null)
            {
                val cal = Calendar.getInstance()
                cal.time = expenseDate
                cal.get(Calendar.YEAR) == currentYear && cal.get(Calendar.MONTH) == currentMonth
            }
            else false
        }.sumOf { it.amount }
    }

    // Helper function to determine budget status using string resources
    private fun determineBudgetStatus(spent: Double, goal: Goal?): String
    {
        if (goal == null)
        {
            return getString(R.string.set_monthly_goals)
        }
        return when
        {
            spent > goal.maxGoal -> getString(R.string.over_budget)
            spent >= goal.maxGoal * 0.8 -> getString(R.string.near_budget_limit)
            else -> getString(R.string.within_budget)
        }
    }

    // Helper function to calculate all-time spending per category
    private fun calculateCategoryBreakdown(expenses: List<Expense>): List<String>
    {
        return expenses.groupBy { it.categoryId }
            .map { (categoryId, list) ->
                // Format each line using string resource with placeholders
                getString(R.string.category_total, "Category $categoryId", list.sumOf { it.amount })
            }
    }

    companion object
    {
        fun newInstance(username: String): HomeFragment
        {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString("username", username)
            fragment.arguments = args
            return fragment
        }
    }

    // RecyclerView adapter receives a list of pre-formatted strings
    // This eliminates any string literals inside the adapter and removes the inner class receiver issue
    private class CategoryBreakdownAdapter(
        private val data: List<String>
    ) : RecyclerView.Adapter<CategoryBreakdownAdapter.ViewHolder>()
    {
        class ViewHolder(val tv: TextView) : RecyclerView.ViewHolder(tv)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        {
            val tv = TextView(parent.context)
            tv.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            tv.setPadding(16, 8, 16, 8)
            return ViewHolder(tv)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int)
        {
            holder.tv.text = data[position]
        }

        override fun getItemCount(): Int = data.size
    }
}