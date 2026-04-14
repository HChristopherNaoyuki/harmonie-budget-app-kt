package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.viewmodels.ExpenseViewModel

class CategoryTotalActivity : AppCompatActivity()
{
    private lateinit var tvTotals: TextView
    private lateinit var username: String

    private val expenseViewModel = ExpenseViewModel()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_total)

        username = intent.getStringExtra("username") ?: "admin"
        tvTotals = findViewById(R.id.tv_totals)

        // Call through the ViewModel layer
        val expenses = expenseViewModel.getExpenses(this, username)

        val totals = expenses.groupBy { it.categoryId }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val builder = StringBuilder()
        for ((categoryId, total) in totals)
        {
            builder.append("Category $categoryId: $total\n")
        }
        if (totals.isEmpty())
        {
            builder.append("No expenses found")
        }
        tvTotals.text = builder.toString()
    }
}