package com.example.harmonie_budget_app_kt

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.viewmodels.CategoryViewModel
import com.example.harmonie_budget_app_kt.viewmodels.ExpenseViewModel
import java.util.Calendar
import java.util.Locale

/**
 * ExpenseActivity allows the user to create a new expense entry.
 * Categories are loaded into the spinner through the ViewModel.
 * Photo attachment now launches the gallery picker (addresses review note on dummy URI).
 * The selected photo URI is stored as a string for later display in the expense list.
 * This implements the optional photo feature with real functionality.
 */
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
    private val categoryViewModel = CategoryViewModel()
    private val expenseViewModel = ExpenseViewModel()
    private var categoriesList: List<com.example.harmonie_budget_app_kt.models.Category> = emptyList()

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

        categoriesList = categoryViewModel.getCategories(this, username)

        val categoryNames = categoriesList.map { it.name }
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

        // Real photo attachment using gallery picker (replaces hardcoded dummy URI)
        btnAttachPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_CODE_PHOTO)
        }

        btnSaveExpense.setOnClickListener {
            val amountStr = etAmount.text.toString().trim()
            val date = etDate.text.toString().trim()
            val startTime = etStartTime.text.toString().trim()
            val endTime = etEndTime.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val selectedIndex = spinnerCategory.selectedItemPosition

            if (amountStr.isNotEmpty() && date.isNotEmpty() && startTime.isNotEmpty() && endTime.isNotEmpty() && description.isNotEmpty())
            {
                if (categoriesList.isEmpty() || selectedIndex < 0 || selectedIndex >= categoriesList.size)
                {
                    Toast.makeText(this, "Please create at least one category first and select a category", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                val amount = amountStr.toDoubleOrNull() ?: 0.0
                val selectedCategory = categoriesList[selectedIndex]

                val expense = Expense(
                    id = 0,
                    amount = amount,
                    date = date,
                    startTime = startTime,
                    endTime = endTime,
                    description = description,
                    categoryId = selectedCategory.id,
                    photoUri = photoUri
                )

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
    {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PHOTO && resultCode == Activity.RESULT_OK && data != null)
        {
            photoUri = data.data.toString()
            Toast.makeText(this, "Photo attached", Toast.LENGTH_SHORT).show()
        }
    }

    companion object
    {
        private const val REQUEST_CODE_PHOTO = 1
    }
}