package com.example.harmonie_budget_app_kt

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.viewmodels.CategoryViewModel
import com.example.harmonie_budget_app_kt.viewmodels.ExpenseViewModel
import java.util.Locale

/**
 * CategoryTotalActivity displays the totals table with category breakdown.
 * Updated to match the mockup design precisely.
 * Optimized for small screens with a clean table layout.
 */
class CategoryTotalActivity : AppCompatActivity()
{
    private lateinit var btnReturnHome: Button
    private lateinit var ivBackArrow: TextView
    private lateinit var tableRowsContainer: LinearLayout
    private lateinit var username: String

    private val expenseViewModel = ExpenseViewModel()
    private val categoryViewModel = CategoryViewModel()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_total)

        username = intent.getStringExtra("username") ?: "admin"

        btnReturnHome = findViewById(R.id.btn_return_home)
        ivBackArrow = findViewById(R.id.iv_back_arrow)
        tableRowsContainer = findViewById(R.id.table_rows_container)

        ivBackArrow.setOnClickListener {
            navigateToBudgetsTab()
        }

        btnReturnHome.setOnClickListener {
            navigateToBudgetsTab()
        }

        loadAndDisplayData()
    }

    private fun navigateToBudgetsTab()
    {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("username", username)
        intent.putExtra("selected_tab", R.id.nav_budgets)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun loadAndDisplayData()
    {
        val expenses = expenseViewModel.getExpenses(this, username)
        val categories = categoryViewModel.getCategories(this, username)

        val categoryMap: Map<Int, Category> = categories.associateBy { it.id }

        val totals = expenses.groupBy { it.categoryId }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val grandTotal = totals.values.sum()

        val tableData = totals.map { (categoryId, amount) ->
            val categoryName = categoryMap[categoryId]?.name ?: getString(R.string.default_category_name)
            val percentage = if (grandTotal > 0) (amount / grandTotal * 100) else 0.0
            Triple(categoryName, amount, percentage)
        }.sortedByDescending { it.second }

        displayTotalsTable(tableData)
    }

    private fun displayTotalsTable(data: List<Triple<String, Double, Double>>)
    {
        tableRowsContainer.removeAllViews()

        if (data.isEmpty())
        {
            val emptyRow = TextView(this)
            emptyRow.text = getString(R.string.no_expense_data_available)
            emptyRow.setTextColor(getColor(R.color.text_secondary_light))
            emptyRow.textSize = 14f
            emptyRow.setPadding(16, 32, 16, 32)
            emptyRow.gravity = Gravity.CENTER
            tableRowsContainer.addView(emptyRow)
            return
        }

        for ((categoryName, amount, percentage) in data)
        {
            val rowLayout = LinearLayout(this)
            rowLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            rowLayout.orientation = LinearLayout.HORIZONTAL
            rowLayout.setPadding(0, 10, 0, 10)

            // Category Name - Left aligned, bold, takes 2 parts
            val categoryTextView = TextView(this)
            categoryTextView.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                2f
            )
            categoryTextView.text = categoryName
            categoryTextView.setTextColor(getColor(R.color.text_primary_light))
            categoryTextView.textSize = 14f
            categoryTextView.typeface = android.graphics.Typeface.DEFAULT_BOLD

            // Amount - Right aligned, takes 1 part
            val amountTextView = TextView(this)
            amountTextView.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            amountTextView.text = String.format(Locale.US, "%,.2f", amount)
            amountTextView.setTextColor(getColor(R.color.text_primary_light))
            amountTextView.textSize = 14f
            amountTextView.gravity = Gravity.END

            // Percentage - Right aligned, takes 1 part
            val percentageTextView = TextView(this)
            percentageTextView.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            val percentageText = String.format(Locale.US, "%.1f%%", percentage)
            percentageTextView.text = percentageText
            percentageTextView.setTextColor(getColor(R.color.text_secondary_light))
            percentageTextView.textSize = 14f
            percentageTextView.gravity = Gravity.END

            rowLayout.addView(categoryTextView)
            rowLayout.addView(amountTextView)
            rowLayout.addView(percentageTextView)

            tableRowsContainer.addView(rowLayout)
        }
    }
}