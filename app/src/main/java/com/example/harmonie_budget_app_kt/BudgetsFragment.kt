package com.example.harmonie_budget_app_kt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.viewmodels.CategoryViewModel
import com.example.harmonie_budget_app_kt.viewmodels.ExpenseViewModel

/**
 * BudgetsFragment displays the Expense History section with category filtering.
 * The bottom navigation label is "History" but the screen title remains "Budgets".
 *
 * Part 3 Enhancement:
 * - Added category filter spinner to allow users to filter expenses by category.
 * - Filter includes an "All Categories" option to show all expenses.
 * - Filter updates the expense history list dynamically when a category is selected.
 */
class BudgetsFragment : Fragment()
{
    private lateinit var username: String
    private lateinit var rvExpenseHistory: RecyclerView
    private lateinit var btnViewTotals: Button
    private lateinit var spinnerFilterCategory: Spinner

    private val expenseViewModel = ExpenseViewModel()
    private val categoryViewModel = CategoryViewModel()

    private var allExpenses: List<Expense> = emptyList()
    private var filteredExpenses: List<Expense> = emptyList()
    private var categories: List<Category> = emptyList()
    private lateinit var expenseAdapter: ExpenseHistoryAdapter

    // Filter state: -1 means all categories, otherwise the selected category ID
    private var selectedCategoryId: Int = -1

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
        spinnerFilterCategory = view.findViewById(R.id.spinner_filter_category)

        rvExpenseHistory.layoutManager = LinearLayoutManager(requireContext())

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
        loadData()
    }

    /**
     * Loads all expenses and categories, then sets up the filter spinner
     * and displays the expense history.
     */
    private fun loadData()
    {
        allExpenses = expenseViewModel.getExpenses(requireContext(), username)
        categories = categoryViewModel.getCategories(requireContext(), username)

        // Sort expenses in reverse chronological order (newest first)
        allExpenses = allExpenses.sortedWith(
            compareByDescending<Expense> { it.date }
                .thenByDescending { it.startTime }
        )

        // Initialize filtered expenses to all expenses
        filteredExpenses = allExpenses.toList()

        // Initialize adapter and set data
        expenseAdapter = ExpenseHistoryAdapter(categories)
        rvExpenseHistory.adapter = expenseAdapter
        updateAdapter()

        // Set up the category filter spinner
        setupCategorySpinner()
    }

    /**
     * Sets up the category filter spinner with category names.
     * Adds an "All Categories" option at the beginning.
     * When a category is selected, the expense list is filtered accordingly.
     */
    private fun setupCategorySpinner()
    {
        val categoryNames = mutableListOf("All Categories")
        categoryNames.addAll(categories.map { it.name })

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            categoryNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilterCategory.adapter = adapter

        spinnerFilterCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                if (position == 0)
                {
                    // "All Categories" selected
                    selectedCategoryId = -1
                }
                else
                {
                    // Specific category selected
                    selectedCategoryId = categories[position - 1].id
                }
                applyFilter()
            }

            override fun onNothingSelected(parent: AdapterView<*>)
            {
                selectedCategoryId = -1
                applyFilter()
            }
        }
    }

    /**
     * Applies the category filter to the expense list.
     * Updates the RecyclerView with the filtered results.
     */
    private fun applyFilter()
    {
        filteredExpenses = if (selectedCategoryId == -1)
        {
            // Show all expenses
            allExpenses
        }
        else
        {
            // Show only expenses matching the selected category
            allExpenses.filter { it.categoryId == selectedCategoryId }
        }

        updateAdapter()
    }

    /**
     * Updates the RecyclerView adapter with the current filtered expenses list.
     */
    private fun updateAdapter()
    {
        expenseAdapter.submitList(filteredExpenses)
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