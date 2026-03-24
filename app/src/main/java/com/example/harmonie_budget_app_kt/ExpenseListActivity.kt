package com.example.harmonie_budget_app_kt

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.utils.JsonHelper

class ExpenseListActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_list)

        val rvExpenses: RecyclerView = findViewById(R.id.rv_expenses)

        // Username is passed from the calling activity
        val username = intent.getStringExtra("username") ?: "admin"

        rvExpenses.layoutManager = LinearLayoutManager(this)

        val expenses = JsonHelper.loadExpenses(this, username)
        val adapter = ExpenseAdapter(expenses) { expense ->
            if (expense.photoUri != null) {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(Uri.parse(expense.photoUri), "image/*")
                startActivity(intent)
            } else {
                Toast.makeText(this, "No photo attached", Toast.LENGTH_SHORT).show()
            }
        }
        rvExpenses.adapter = adapter
    }
}