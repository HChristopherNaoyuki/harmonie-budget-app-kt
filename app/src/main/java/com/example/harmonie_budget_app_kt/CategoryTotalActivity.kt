// app/kotlin+java/com.example.harmonie_budget_app_kt/CategoryTotalActivity.kt
package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.utils.JsonHelper

/**
 * CategoryTotalActivity - Displays total spent per category.
 * Uses simple TextView for clean prototype display.
 * Data loaded from expenses.json in budget_data folder.
 */
class CategoryTotalActivity : AppCompatActivity() {
    private lateinit var tvTotals: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_total)

        tvTotals = findViewById(R.id.tv_totals)

        val expenses = JsonHelper.loadExpenses(this)

        // Explicit type to resolve inference issues
        val categoryMap: Map<Int, Double> = expenses
            .groupBy { expense -> expense.categoryId }
            .mapValues { entry -> entry.value.sumOf { exp -> exp.amount } }

        val sb = StringBuilder("Category Totals:\n")
        categoryMap.forEach { (catId, total) ->
            sb.append("Category $catId: R$total\n")
        }
        tvTotals.text = sb.toString()
    }
}