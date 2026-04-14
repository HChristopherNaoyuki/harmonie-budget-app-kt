package com.example.harmonie_budget_app_kt

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.viewmodels.ExpenseViewModel
import java.util.Calendar
import java.util.Locale

class ExpenseActivity : AppCompatActivity()
{
    private lateinit var etAmount: EditText
    private lateinit var etDate: EditText
    private lateinit var etStartTime: EditText
    private lateinit var etEndTime: EditText
    private lateinit var etDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnAttachPhoto: Button
    private lateinit var btnSaveExpense: Button
    private lateinit var btnReturnHome: Button
    private lateinit var username: String
    private var photoUri: String? = null

    private val expenseViewModel = ExpenseViewModel()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)

        username = intent.getStringExtra("username") ?: "admin"

        etAmount = findViewById(R.id.et_amount)
        etDate = findViewById(R.id.et_date)
        etStartTime = findViewById(R.id.et_start_time)
        etEndTime = findViewById(R.id.et_end_time)
        etDescription = findViewById(R.id.et_description)
        spinnerCategory = findViewById(R.id.spinner_category)
        btnAttachPhoto = findViewById(R.id.btn_attach_photo)
        btnSaveExpense = findViewById(R.id.btn_save_expense)
        btnReturnHome = findViewById(R.id.btn_return_home)

        // Call through the ViewModel layer
        val categories = expenseViewModel.getExpenses(this, username) // placeholder for category list
        val categoryNames = categories.map { it.description }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        etDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                this,
                { _, year, month, day ->
                    etDate.setText(String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, day))
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        etStartTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                this,
                { _, hour, minute ->
                    etStartTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        etEndTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                this,
                { _, hour, minute ->
                    etEndTime.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute))
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }

        btnAttachPhoto.setOnClickListener {
            photoUri = "file://example_photo.jpg"
            Toast.makeText(this, "Photo attached (demo)", Toast.LENGTH_SHORT).show()
        }

        btnSaveExpense.setOnClickListener {
            val amountStr = etAmount.text.toString().trim()
            val date = etDate.text.toString().trim()
            val startTime = etStartTime.text.toString().trim()
            val endTime = etEndTime.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val selectedCategoryIndex = spinnerCategory.selectedItemPosition

            if (amountStr.isNotEmpty() && date.isNotEmpty() && startTime.isNotEmpty() && endTime.isNotEmpty() && description.isNotEmpty())
            {
                if (selectedCategoryIndex < 0)
                {
                    Toast.makeText(this, "Please select a category", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                val amount = amountStr.toDoubleOrNull() ?: 0.0
                val expense = Expense(0, amount, date, startTime, endTime, description, 0, photoUri)

                // Call through the ViewModel layer
                expenseViewModel.saveExpense(this, username, expense)

                Toast.makeText(this, getString(R.string.expense_submitted), Toast.LENGTH_SHORT).show()
                finish()
            }
            else
            {
                Toast.makeText(this, getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show()
            }
        }

        btnReturnHome.setOnClickListener {
            finish()
        }
    }
}