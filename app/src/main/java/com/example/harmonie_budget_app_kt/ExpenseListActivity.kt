// app/kotlin+java/com.example.harmonie_budget_app_kt/ExpenseListActivity.kt
package com.example.harmonie_budget_app_kt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.utils.JsonHelper

/**
 * ExpenseListActivity
 * Displays expense list with photo access.
 * Data loaded from expenses.json in budget_data folder.
 * Fixed: removed redundant qualifiers.
 */
class ExpenseListActivity : AppCompatActivity() {
    private lateinit var rvExpenses: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_list)

        rvExpenses = findViewById(R.id.rv_expenses)
        rvExpenses.layoutManager = LinearLayoutManager(this)

        val expenses = JsonHelper.loadExpenses(this)
        val adapter = ExpenseAdapter(expenses, this)
        rvExpenses.adapter = adapter
    }
}

/**
 * ExpenseAdapter
 * Simple list adapter.
 * Fixed: removed redundant qualifiers.
 */
class ExpenseAdapter(private val list: List<Expense>, private val context: android.content.Context)
    : androidx.recyclerview.widget.RecyclerView.Adapter<ExpenseAdapter.ViewHolder>() {

    class ViewHolder(val tv: android.widget.TextView) : androidx.recyclerview.widget.RecyclerView.ViewHolder(tv)

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val tv = android.widget.TextView(parent.context)
        tv.layoutParams = android.view.ViewGroup.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            120
        )
        tv.setPadding(32, 16, 32, 16)
        return ViewHolder(tv)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val exp = list[position]
        var text = "${exp.date} | R${exp.amount} | ${exp.description}"
        if (exp.photoUri != null) {
            text += " (Photo attached - tap to view)"
            holder.tv.setOnClickListener {
                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW)
                intent.setDataAndType(android.net.Uri.parse(exp.photoUri), "image/*")
                context.startActivity(intent)
            }
        }
        holder.tv.text = text
    }

    override fun getItemCount() = list.size
}