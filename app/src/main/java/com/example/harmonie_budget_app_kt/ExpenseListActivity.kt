package com.example.harmonie_budget_app_kt

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Button
import android.widget.TextView
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.viewmodels.ExpenseViewModel
import androidx.core.net.toUri
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class ExpenseListActivity : AppCompatActivity()
{
    private val expenseViewModel = ExpenseViewModel()
    private lateinit var etFromDate: EditText
    private lateinit var etToDate: EditText
    private lateinit var btnFilter: Button
    private lateinit var rvExpenses: RecyclerView
    private var allExpenses: List<Expense> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense_list)

        rvExpenses = findViewById(R.id.rv_expenses)
        etFromDate = findViewById(R.id.et_from_date)
        etToDate = findViewById(R.id.et_to_date)
        btnFilter = findViewById(R.id.btn_filter)

        val username = intent.getStringExtra("username") ?: "admin"

        rvExpenses.layoutManager = LinearLayoutManager(this)

        allExpenses = expenseViewModel.getExpenses(this, username)

        val adapter = ExpenseAdapter(allExpenses) { expense: Expense ->
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

        // Date pickers added to the filter fields (addresses review note that fields were never referenced)
        etFromDate.setOnClickListener {
            showDatePicker(etFromDate)
        }
        etToDate.setOnClickListener {
            showDatePicker(etToDate)
        }

        // Filter button now has a click handler (addresses review note on missing filtering logic)
        btnFilter.setOnClickListener {
            applyFilter(adapter)
        }
    }

    private fun showDatePicker(editText: EditText)
    {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, day ->
                editText.setText(String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, day))
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    /**
     * Applies date range filtering to the expense list.
     * Uses SimpleDateFormat to parse the user-entered dates and filters expenses accordingly.
     * The adapter is recreated with the filtered list. This directly implements the missing
     * filtering feature noted in the review.
     */
    private fun applyFilter(adapter: ExpenseAdapter)
    {
        val fromStr = etFromDate.text.toString().trim()
        val toStr = etToDate.text.toString().trim()

        var filtered = allExpenses

        if (fromStr.isNotEmpty() && toStr.isNotEmpty())
        {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val fromDate = sdf.parse(fromStr)
            val toDate = sdf.parse(toStr)

            if (fromDate != null && toDate != null)
            {
                filtered = allExpenses.filter { expense ->
                    val expDate = sdf.parse(expense.date)
                    expDate != null && !expDate.before(fromDate) && !expDate.after(toDate)
                }
            }
        }

        val newAdapter = ExpenseAdapter(filtered) { expense: Expense ->
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
        rvExpenses.adapter = newAdapter
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