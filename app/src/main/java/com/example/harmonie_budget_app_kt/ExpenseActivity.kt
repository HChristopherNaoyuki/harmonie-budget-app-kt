package com.example.harmonie_budget_app_kt

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.utils.JsonHelper
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.viewmodels.ExpenseViewModel
import com.example.harmonie_budget_app_kt.viewmodels.GamificationViewModel
import java.util.Calendar
import java.util.Locale

/**
 * ExpenseActivity handles the creation and submission of new expense records.
 *
 * Part 3 Enhancement: Added back arrow navigation to return to the Budget tab.
 */
class ExpenseActivity : AppCompatActivity()
{
    private val expenseViewModel = ExpenseViewModel()
    private val gamificationViewModel = GamificationViewModel()
    private lateinit var etAmount: EditText
    private lateinit var etDate: EditText
    private lateinit var etStartTime: EditText
    private lateinit var etEndTime: EditText
    private lateinit var etDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnAttachPhoto: Button
    private lateinit var btnSaveExpense: Button
    private lateinit var btnReturnHome: Button
    private lateinit var ivBackArrow: TextView
    private var selectedPhotoUri: Uri? = null
    private var username: String = "admin"

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? ->
        uri?.let { selectedUri: Uri ->
            selectedPhotoUri = selectedUri
            Toast.makeText(this, "Photo attached", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)

        etAmount = findViewById(R.id.et_amount)
        etDate = findViewById(R.id.et_date)
        etStartTime = findViewById(R.id.et_start_time)
        etEndTime = findViewById(R.id.et_end_time)
        etDescription = findViewById(R.id.et_description)
        spinnerCategory = findViewById(R.id.spinner_category)
        btnAttachPhoto = findViewById(R.id.btn_attach_photo)
        btnSaveExpense = findViewById(R.id.btn_save_expense)
        btnReturnHome = findViewById(R.id.btn_return_home)
        ivBackArrow = findViewById(R.id.iv_back_arrow)

        username = intent.getStringExtra("username") ?: "admin"

        // Back arrow navigation to return to the Budget tab
        ivBackArrow.setOnClickListener {
            navigateToBudgetTab()
        }

        btnReturnHome.setOnClickListener {
            navigateToBudgetTab()
        }

        val jsonHelper = JsonHelper()
        val categoryList = jsonHelper.loadCategories(this, username)
        val categories = categoryList.map { category: Category -> category.name }
            .ifEmpty { listOf("General") }

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        etDate.setOnClickListener {
            showDatePicker()
        }

        etStartTime.setOnClickListener {
            showTimePicker(etStartTime)
        }

        etEndTime.setOnClickListener {
            showTimePicker(etEndTime)
        }

        btnAttachPhoto.setOnClickListener {
            getContent.launch("image/*")
        }

        btnSaveExpense.setOnClickListener {
            saveExpense()
        }
    }

    /**
     * Navigates back to the DashboardActivity and selects the Budget tab.
     */
    private fun navigateToBudgetTab()
    {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("username", username)
        intent.putExtra("selected_tab", R.id.nav_budget)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showDatePicker()
    {
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

    private fun showTimePicker(editText: EditText)
    {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            this,
            { _, hour, minute ->
                editText.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minute))
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun isTimeValid(startTime: String, endTime: String): Boolean
    {
        if (startTime.isEmpty() || endTime.isEmpty())
        {
            return true
        }

        val startParts = startTime.split(":")
        val endParts = endTime.split(":")

        if (startParts.size != 2 || endParts.size != 2)
        {
            return false
        }

        val startHour = startParts[0].toIntOrNull() ?: return false
        val startMinute = startParts[1].toIntOrNull() ?: return false
        val endHour = endParts[0].toIntOrNull() ?: return false
        val endMinute = endParts[1].toIntOrNull() ?: return false

        val startTotalMinutes = startHour * 60 + startMinute
        val endTotalMinutes = endHour * 60 + endMinute

        return endTotalMinutes >= startTotalMinutes
    }

    private fun saveExpense()
    {
        val amountStr = etAmount.text.toString().trim()
        val date = etDate.text.toString().trim()
        val startTime = etStartTime.text.toString().trim()
        val endTime = etEndTime.text.toString().trim()
        val description = etDescription.text.toString().trim()

        if (amountStr.isEmpty() || date.isEmpty() || description.isEmpty())
        {
            Toast.makeText(this, getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show()
            return
        }

        if (!isTimeValid(startTime, endTime))
        {
            Toast.makeText(this, "End time cannot be earlier than start time", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountStr.toDoubleOrNull() ?: 0.0

        val jsonHelper = JsonHelper()
        val categoryList = jsonHelper.loadCategories(this, username)

        val selectedCategoryPosition = spinnerCategory.selectedItemPosition
        val categoryId = if (selectedCategoryPosition >= 0 && selectedCategoryPosition < categoryList.size)
        {
            categoryList[selectedCategoryPosition].id
        }
        else if (categoryList.isNotEmpty())
        {
            categoryList[0].id
        }
        else
        {
            val generalCategory = Category(1, "General")
            jsonHelper.saveCategory(this, username, generalCategory)
            1
        }

        val expense = Expense(
            id = 0,
            categoryId = categoryId,
            amount = amount,
            date = date,
            startTime = startTime,
            endTime = endTime,
            description = description,
            photoUri = selectedPhotoUri?.toString()
        )

        expenseViewModel.saveExpense(this, username, expense)
        gamificationViewModel.updateStreak(this, username)

        Toast.makeText(this, getString(R.string.expense_submitted), Toast.LENGTH_SHORT).show()
        finish()
    }
}