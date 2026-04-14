package com.example.harmonie_budget_app_kt

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.widget.TextView
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.viewmodels.ExpenseViewModel
import androidx.core.net.toUri

class ExpenseListActivity : AppCompatActivity()
{
    private val expenseViewModel = ExpenseViewModel()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_list)

        val rvExpenses: RecyclerView = findViewById(R.id.rv_expenses)
        val username = intent.getStringExtra("username") ?: "admin"

        rvExpenses.layoutManager = LinearLayoutManager(this)

        // Call through the ViewModel layer
        val expenses = expenseViewModel.getExpenses(this, username)

        val adapter = ExpenseAdapter(expenses) { expense: Expense ->
            if (expense.photoUri != null)
            {
                try
                {
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.setDataAndType(expense.photoUri.toUri(), "image/*")
                    startActivity(intent)
                }
                catch (_: Exception)
                {
                    Toast.makeText(this, "Unable to open photo", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "No photo attached", Toast.LENGTH_SHORT).show()
            }
        }
        rvExpenses.adapter = adapter
    }

    private class ExpenseAdapter(
        private val list: List<Expense>,
        private val onPhotoClick: (Expense) -> Unit
    ) : RecyclerView.Adapter<ExpenseAdapter.ViewHolder>()
    {
        class ViewHolder(val tv: TextView) : RecyclerView.ViewHolder(tv)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        {
            val tv = TextView(parent.context)
            tv.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            tv.setPadding(16, 16, 16, 16)
            return ViewHolder(tv)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int)
        {
            val exp = list[position]
            var text = "${exp.amount} - ${exp.date} - ${exp.description}"
            if (exp.photoUri != null)
            {
                text += " (Photo attached - tap to view)"
                holder.tv.setOnClickListener { onPhotoClick(exp) }
            }
            holder.tv.text = text
        }

        override fun getItemCount(): Int = list.size
    }
}