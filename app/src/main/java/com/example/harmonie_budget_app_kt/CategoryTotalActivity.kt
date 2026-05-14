package com.example.harmonie_budget_app_kt

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.viewmodels.CategoryViewModel
import com.example.harmonie_budget_app_kt.viewmodels.ExpenseViewModel
import java.util.Calendar
import java.util.Locale

/**
 * CategoryTotalActivity displays the expense history table with filtering capabilities.
 * Users can filter expenses by category and date, and view a scrollable list of results.
 */
class CategoryTotalActivity : AppCompatActivity()
{
    private lateinit var rvExpenseHistory: RecyclerView
    private lateinit var spinnerFilterCategory: Spinner
    private lateinit var etFilterDate: EditText
    private lateinit var btnApplyFilter: Button
    private lateinit var btnClearFilter: Button
    private lateinit var btnReturnHome: Button
    private lateinit var tvTotalAmount: TextView

    private lateinit var username: String
    private lateinit var expenseAdapter: ExpenseHistoryAdapter

    private val expenseViewModel = ExpenseViewModel()
    private val categoryViewModel = CategoryViewModel()

    // Data holders
    private var allExpenses: List<Expense> = emptyList()
    private var filteredExpenses: List<Expense> = emptyList()
    private var categories: List<Category> = emptyList()

    // Filter state
    private var selectedCategoryId: Int = -1 // -1 means all categories
    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_total)

        username = intent.getStringExtra("username") ?: "admin"

        // Initialize views
        rvExpenseHistory = findViewById(R.id.rv_expense_history)
        spinnerFilterCategory = findViewById(R.id.spinner_filter_category)
        etFilterDate = findViewById(R.id.et_filter_date)
        btnApplyFilter = findViewById(R.id.btn_apply_filter)
        btnClearFilter = findViewById(R.id.btn_clear_filter)
        btnReturnHome = findViewById(R.id.btn_return_home)
        tvTotalAmount = findViewById(R.id.tv_total_amount)

        rvExpenseHistory.layoutManager = LinearLayoutManager(this)

        // Load initial data
        loadData()

        // Setup UI listeners
        setupCategorySpinner()
        setupDatePicker()
        setupButtonListeners()
    }

    /**
     * Loads expenses and categories from the ViewModel.
     * Sorts expenses chronologically and initializes the adapter.
     */
    private fun loadData()
    {
        allExpenses = expenseViewModel.getExpenses(this, username)
        categories = categoryViewModel.getCategories(this, username)

        // Sort expenses in chronological order (oldest first)
        allExpenses = allExpenses.sortedWith(
            compareBy<Expense> { it.date }
                .thenBy { it.startTime }
        )

        // Initialize filtered expenses to all expenses
        filteredExpenses = allExpenses.toList()

        // Initialize adapter and set data
        expenseAdapter = ExpenseHistoryAdapter(categories)
        rvExpenseHistory.adapter = expenseAdapter
        updateAdapterAndTotal()
    }

    /**
     * Sets up the category filter spinner with "All Categories" option.
     */
    private fun setupCategorySpinner()
    {
        val categoryNames = mutableListOf(getString(R.string.all_categories))
        categoryNames.addAll(categories.map { it.name })

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerFilterCategory.adapter = adapter

        spinnerFilterCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long)
            {
                selectedCategoryId = if (position == 0) -1 else categories[position - 1].id
                // Do not auto-apply filter on selection change to improve performance.
                // User must click "Apply Filter".
            }

            override fun onNothingSelected(parent: AdapterView<*>)
            {
                selectedCategoryId = -1
            }
        }
    }

    /**
     * Sets up the date picker dialog for the filter date field.
     */
    private fun setupDatePicker()
    {
        etFilterDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, day)
                    etFilterDate.setText(selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    /**
     * Sets up click listeners for the Apply Filter, Clear Filter, and Return Home buttons.
     */
    private fun setupButtonListeners()
    {
        btnApplyFilter.setOnClickListener {
            applyFilter()
        }

        btnClearFilter.setOnClickListener {
            clearFilter()
        }

        btnReturnHome.setOnClickListener {
            // Navigate back to the DashboardActivity and explicitly select the "Budgets" tab.
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("username", username)
            // Pass an extra to tell the Dashboard which tab to select.
            intent.putExtra("selected_tab", R.id.nav_budgets)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }

    /**
     * Filters the expenses based on the current selectedCategoryId and selectedDate.
     * Updates the RecyclerView and the total amount display.
     */
    private fun applyFilter()
    {
        // Perform filtering logic
        val filtered = allExpenses.filter { expense ->
            var matches = true

            // Check category filter
            if (selectedCategoryId != -1)
            {
                matches = matches && (expense.categoryId == selectedCategoryId)
            }

            // Check date filter
            selectedDate?.let { date ->
                if (date.isNotEmpty())
                {
                    matches = matches && (expense.date == date)
                }
            }

            matches
        }

        // Update the displayed list and total
        filteredExpenses = filtered
        updateAdapterAndTotal()

        if (filteredExpenses.isEmpty())
        {
            Toast.makeText(this, getString(R.string.no_expense_available), Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Clears all active filters, resetting the display to show all expenses.
     */
    private fun clearFilter()
    {
        // Reset filter state
        selectedCategoryId = -1
        selectedDate = null
        etFilterDate.text.clear()
        spinnerFilterCategory.setSelection(0)

        // Reset displayed data to the full list
        filteredExpenses = allExpenses
        updateAdapterAndTotal()
    }

    /**
     * Updates the RecyclerView adapter with the current filtered expenses list
     * and refreshes the total amount display.
     */
    private fun updateAdapterAndTotal()
    {
        expenseAdapter.submitList(filteredExpenses)
        updateTotalAmountDisplay()
    }

    /**
     * Calculates the sum of the currently displayed (filtered) expenses
     * and updates the total amount TextView with ZAR formatting.
     */
    private fun updateTotalAmountDisplay()
    {
        val total = filteredExpenses.sumOf { it.amount }
        val formattedTotal = String.format(Locale.US, "R %,.2f", total)
        tvTotalAmount.text = formattedTotal
    }
}