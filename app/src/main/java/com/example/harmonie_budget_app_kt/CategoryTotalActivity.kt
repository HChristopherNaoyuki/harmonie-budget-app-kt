// app/kotlin+java/com.example.harmonie_budget_app_kt/CategoryTotalActivity.kt
package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.utils.JsonHelper

/**
 * CategoryTotalActivity - Simple totals per category (prototype shows all).
 * TextView list for clean display.
 */
class CategoryTotalActivity : AppCompatActivity() {

    private lateinit var tvTotals: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_total)

        tvTotals = findViewById(R.id.tv_totals)

        val expenses = JsonHelper.loadExpenses(this)
        val categoryMap = expenses.groupBy { it.categoryId }
            .mapValues { it.value.sumOf { exp -> exp.amount } }

        val sb = StringBuilder("Category Totals:\n")
        categoryMap.forEach { (catId, total) ->
            sb.append("Category $catId: R$total\n")
        }
        tvTotals.text = sb.toString()
    }
}