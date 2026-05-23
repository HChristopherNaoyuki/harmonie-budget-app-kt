package com.example.harmonie_budget_app_kt

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.viewmodels.CategoryViewModel
import com.example.harmonie_budget_app_kt.viewmodels.ExpenseViewModel
import java.util.Calendar
import java.util.Locale

/**
 * BudgetsFragment displays the Expense History section with filtering capabilities.
 * Supports filtering by category, month, and year.
 * Displays total spent and transaction count for the filtered results.
 */
class BudgetsFragment : Fragment()
{
    private lateinit var username: String
    private lateinit var rvExpenseHistory: RecyclerView
    private lateinit var btnViewTotals: Button
    private lateinit var spinnerFilterCategory: Spinner
    private lateinit var spinnerFilterMonth: Spinner
    private lateinit var tvFilterYear: TextView
    private lateinit var tvTotalSpent: TextView
    private lateinit var tvTransactionCount: TextView
    private lateinit var tvCurrentMonthYear: TextView

    private val expenseViewModel = ExpenseViewModel()
    private val categoryViewModel = CategoryViewModel()

    private var allExpenses: List<Expense> = emptyList()
    private var filteredExpenses: List<Expense> = emptyList()
    private var categories: List<Category> = emptyList()
    private lateinit var expenseAdapter: ExpenseHistoryAdapter

    // Filter state
    private var selectedCategoryId: Int = -1
    private var selectedMonth: Int = -1
    private var selectedYear: Int = -1

    private companion object
    {
        private const val ALL_CATEGORIES_POSITION = 0
    }

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
        spinnerFilterMonth = view.findViewById(R.id.spinner_filter_month)
        tvFilterYear = view.findViewById(R.id.tv_filter_year)
        tvTotalSpent = view.findViewById(R.id.tv_total_spent)
        tvTransactionCount = view.findViewById(R.id.tv_transaction_count)
        tvCurrentMonthYear = view.findViewById(R.id.tv_current_month_year)

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

        // Set up spinners and year picker
        setupCategorySpinner()
        setupMonthSpinner()
        setupYearPicker()

        // Apply initial filter (current month and year)
        applyFilters()
    }

    private fun setupCategorySpinner()
    {
        val categoryNames = mutableListOf(getString(R.string.all_categories))
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
                selectedCategoryId = if (position == ALL_CATEGORIES_POSITION) -1
                else categories[position - 1].id
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedCategoryId = -1
                applyFilters()
            }
        }
    }

    private fun setupMonthSpinner()
    {
        val months = arrayOf(
            getString(R.string.month_january),
            getString(R.string.month_february),
            getString(R.string.month_march),
            getString(R.string.month_april),
            getString(R.string.month_may),
            getString(R.string.month_june),
            getString(R.string.month_july),
            getString(R.string.month_august),
            getString(R.string.month_september),
            getString(R.string.month_october),
            getString(R.string.month_november),
            getString(R.string.month_december)
        )
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, months)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilterMonth.adapter = adapter

        // Set current month as default selection
        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        spinnerFilterMonth.setSelection(currentMonth)

        spinnerFilterMonth.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(
                parent: AdapterView<*>,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedMonth = position + 1
                applyFilters()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                selectedMonth = Calendar.getInstance().get(Calendar.MONTH) + 1
                applyFilters()
            }
        }
    }

    private fun setupYearPicker()
    {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        selectedYear = currentYear
        tvFilterYear.text = currentYear.toString()

        tvFilterYear.setOnClickListener {
            showYearPickerDialog()
        }
    }

    /**
     * Shows a native year picker dialog using NumberPicker.
     * This replaces the hardcoded year spinner with a native Android component.
     */
    private fun showYearPickerDialog()
    {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val minYear = currentYear - 10
        val maxYear = currentYear + 10

        val numberPicker = NumberPicker(requireContext())
        numberPicker.minValue = minYear
        numberPicker.maxValue = maxYear
        numberPicker.value = selectedYear

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Select Year")
            .setView(numberPicker)
            .setPositiveButton("OK") { _, _ ->
                selectedYear = numberPicker.value
                tvFilterYear.text = selectedYear.toString()
                applyFilters()
            }
            .setNegativeButton("Cancel", null)
            .create()

        dialog.show()
    }

    private fun applyFilters()
    {
        filteredExpenses = allExpenses.filter { expense ->
            var matches = true

            // Category filter
            if (selectedCategoryId != -1)
            {
                matches = matches && (expense.categoryId == selectedCategoryId)
            }

            // Month and Year filter
            if (selectedMonth != -1 && selectedYear != -1)
            {
                val expenseDateParts = expense.date.split("-")
                if (expenseDateParts.size >= 3)
                {
                    val expenseYear = expenseDateParts[0].toIntOrNull() ?: 0
                    val expenseMonth = expenseDateParts[1].toIntOrNull() ?: 0
                    matches = matches && (expenseYear == selectedYear && expenseMonth == selectedMonth)
                }
            }

            matches
        }

        // Update the month/year display
        updateMonthYearDisplay()

        // Update total spent and transaction count
        updateSummary()

        // Update the adapter
        expenseAdapter.submitList(filteredExpenses)
    }

    private fun updateMonthYearDisplay()
    {
        val monthNames = arrayOf(
            getString(R.string.month_january),
            getString(R.string.month_february),
            getString(R.string.month_march),
            getString(R.string.month_april),
            getString(R.string.month_may),
            getString(R.string.month_june),
            getString(R.string.month_july),
            getString(R.string.month_august),
            getString(R.string.month_september),
            getString(R.string.month_october),
            getString(R.string.month_november),
            getString(R.string.month_december)
        )
        val monthName = if (selectedMonth in 1..12) monthNames[selectedMonth - 1] else ""
        val displayText = if (selectedMonth != -1 && selectedYear != -1) {
            "$monthName $selectedYear"
        } else {
            ""
        }
        tvCurrentMonthYear.text = displayText
    }

    private fun updateSummary()
    {
        val total = filteredExpenses.sumOf { it.amount }
        tvTotalSpent.text = String.format(Locale.US, "-R %,.2f", total)

        val transactionCountText = resources.getQuantityString(
            R.plurals.transaction_count,
            filteredExpenses.size,
            filteredExpenses.size
        )
        tvTransactionCount.text = transactionCountText
    }

    fun newInstance(username: String): BudgetsFragment
    {
        val fragment = BudgetsFragment()
        val args = Bundle()
        args.putString("username", username)
        fragment.arguments = args
        return fragment
    }
}