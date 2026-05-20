package com.example.harmonie_budget_app_kt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.viewmodels.CategoryViewModel
import com.example.harmonie_budget_app_kt.viewmodels.ExpenseViewModel

/**
 * BudgetsFragment now displays the Expense History section.
 * This screen shows all user expenses with filtering capabilities.
 * The View Category Totals button navigates to the pie chart screen.
 */
class BudgetsFragment : Fragment()
{
    private lateinit var username: String
    private lateinit var rvExpenseHistory: RecyclerView
    private lateinit var btnViewTotals: Button

    private val expenseViewModel = ExpenseViewModel()
    private val categoryViewModel = CategoryViewModel()

    private var allExpenses: List<Expense> = emptyList()
    private var categories: List<Category> = emptyList()
    private lateinit var expenseAdapter: ExpenseHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = inflater.inflate(R.layout.fragment_budgets, container, false)

        username = arguments?.getString("username") ?: "admin"

        rvExpenseHistory = view.findViewById(R.id.rv_expense_history)
        btnViewTotals = view.findViewById(R.id.btn_view_totals)

        rvExpenseHistory.layoutManager = LinearLayoutManager(requireContext())

        // View Category Totals button navigates to CategoryTotalActivity (pie chart)
        btnViewTotals.setOnClickListener {
            val intent = Intent(requireContext(), CategoryTotalActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        return view
    }

    override fun onResume()
    {
        super.onResume()
        loadExpenseHistory()
    }

    /**
     * Loads expenses and categories and displays them in the RecyclerView.
     */
    private fun loadExpenseHistory()
    {
        allExpenses = expenseViewModel.getExpenses(requireContext(), username)
        categories = categoryViewModel.getCategories(requireContext(), username)

        // Sort expenses in reverse chronological order (newest first)
        allExpenses = allExpenses.sortedWith(
            compareByDescending<Expense> { it.date }
                .thenByDescending { it.startTime }
        )

        expenseAdapter = ExpenseHistoryAdapter(categories)
        rvExpenseHistory.adapter = expenseAdapter
        expenseAdapter.submitList(allExpenses)
    }

    companion object
    {
        fun newInstance(username: String): BudgetsFragment
        {
            val fragment = BudgetsFragment()
            val args = Bundle()
            args.putString("username", username)
            fragment.arguments = args
            return fragment
        }
    }
}