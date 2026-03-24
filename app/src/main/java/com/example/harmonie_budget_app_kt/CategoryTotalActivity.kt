package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.utils.JsonHelper

class CategoryTotalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_total)

        val tvTotals: TextView = findViewById(R.id.tv_totals)

        // Username is passed from the calling activity
        val username = intent.getStringExtra("username") ?: "admin"

        val expenses = JsonHelper.loadExpenses(this, username)
        val categoryMap = expenses.groupBy { it.categoryId }.mapValues { entry ->
            entry.value.sumOf { it.amount }
        }

        val sb = StringBuilder()
        categoryMap.forEach { (id, total) ->
            sb.append("Category $id: R$total\n")
        }
        tvTotals.text = sb.toString()
    }
}