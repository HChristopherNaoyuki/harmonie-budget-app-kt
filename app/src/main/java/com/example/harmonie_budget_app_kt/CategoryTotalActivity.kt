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
 */
class CategoryTotalActivity : AppCompatActivity()
{
    private lateinit var rvExpenseHistory: RecyclerView
    private lateinit var spinnerFilterCategory: Spinner
    private lateinit var etFilterDate: EditText
    private lateinit var btnApplyFilter: Button
    private lateinit var btnClearFilter: Button
    private lateinit var btnReturnHome: Button

    private lateinit var username: String
    private lateinit var expenseAdapter: ExpenseHistoryAdapter

    private val expenseViewModel = ExpenseViewModel()
    private val categoryViewModel = CategoryViewModel()

    private var allExpenses: List<Expense> = emptyList()
    private var categories: List<Category> = emptyList()
    private var selectedCategoryId: Int = -1
    private var selectedDate: String? = null

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_total)

        username = intent.getStringExtra("username") ?: "admin"

        rvExpenseHistory = findViewById(R.id.rv_expense_history)
        spinnerFilterCategory = findViewById(R.id.spinner_filter_category)
        etFilterDate = findViewById(R.id.et_filter_date)
        btnApplyFilter = findViewById(R.id.btn_apply_filter)
        btnClearFilter = findViewById(R.id.btn_clear_filter)
        btnReturnHome = findViewById(R.id.btn_return_home)

        rvExpenseHistory.layoutManager = LinearLayoutManager(this)

        loadData()

        // Create adapter with categories only (Expenses will be set via submitList)
        expenseAdapter = ExpenseHistoryAdapter(categories)
        rvExpenseHistory.adapter = expenseAdapter

        // Submit the initial expenses list to the adapter
        expenseAdapter.submitList(allExpenses)

        btnReturnHome.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("username", username)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        etFilterDate.setOnClickListener {
            showDatePicker()
        }

        btnApplyFilter.setOnClickListener {
            applyFilter()
        }

        btnClearFilter.setOnClickListener {
            clearFilter()
        }
    }

    private fun loadData()
    {
        allExpenses = expenseViewModel.getExpenses(this, username)
        categories = categoryViewModel.getCategories(this, username)

        allExpenses = allExpenses.sortedWith(
            compareBy<Expense> { it.date }
                .thenBy { it.startTime }
        )

        setupCategorySpinner()
    }

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
                if (position == 0)
                {
                    selectedCategoryId = -1
                }
                else
                {
                    selectedCategoryId = categories[position - 1].id
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>)
            {
                selectedCategoryId = -1
            }
        }
    }

    private fun showDatePicker()
    {
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

    private fun applyFilter()
    {
        var filteredExpenses = allExpenses

        if (selectedCategoryId != -1)
        {
            filteredExpenses = filteredExpenses.filter { it.categoryId == selectedCategoryId }
        }

        selectedDate?.let { date ->
            if (date.isNotEmpty())
            {
                filteredExpenses = filteredExpenses.filter { it.date == date }
            }
        }

        expenseAdapter.submitList(filteredExpenses)

        if (filteredExpenses.isEmpty())
        {
            Toast.makeText(this, getString(R.string.no_expense_available), Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearFilter()
    {
        selectedCategoryId = -1
        selectedDate = null
        etFilterDate.text.clear()
        spinnerFilterCategory.setSelection(0)

        expenseAdapter.submitList(allExpenses)
    }
}