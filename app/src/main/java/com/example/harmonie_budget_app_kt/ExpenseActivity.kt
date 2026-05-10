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
 * It provides input fields for amount, date, time range, description, and category selection.
 * Users may attach a photo receipt via the system gallery picker.
 * All data is validated and persisted through the ExpenseViewModel layer.
 *
 * Part 3 enhancement: After saving an expense, the gamification streak is updated.
 * This enables streak-based badges for consistent expense logging.
 *
 * Part 3 Enhancement (Return Home):
 * - Added RETURN HOME button that navigates directly back to the Dashboard (Home screen).
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
    private var selectedPhotoUri: Uri? = null
    private var username: String = "admin"

    /**
     * Activity result launcher for the photo picker.
     * Uses the modern GetContent contract to open the system gallery.
     * Stores the selected URI for attachment to the expense record.
     */
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

        // Initialize view references from the layout
        etAmount = findViewById(R.id.et_amount)
        etDate = findViewById(R.id.et_date)
        etStartTime = findViewById(R.id.et_start_time)
        etEndTime = findViewById(R.id.et_end_time)
        etDescription = findViewById(R.id.et_description)
        spinnerCategory = findViewById(R.id.spinner_category)
        btnAttachPhoto = findViewById(R.id.btn_attach_photo)
        btnSaveExpense = findViewById(R.id.btn_save_expense)
        btnReturnHome = findViewById(R.id.btn_return_home)

        // Retrieve username from intent for user-specific data isolation
        username = intent.getStringExtra("username") ?: "admin"

        // Part 3 Enhancement: RETURN HOME button handler.
        // Navigates directly back to the DashboardActivity (Home screen).
        // The button uses FLAG_ACTIVITY_CLEAR_TOP to ensure the back stack
        // is properly managed and duplicates are avoided.
        btnReturnHome.setOnClickListener {
            val intent = Intent(this, DashboardActivity::class.java)
            intent.putExtra("username", username)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

        /*
            The Spinner must display only the categories the user has created.
            Categories are loaded directly from the JSON data file using JsonHelper.
            This is the same data saved by CategoryActivity and is not hard-coded.
            The list is built from the stored Category objects, ensuring the selection
            is restricted to user-created categories only.
        */
        val jsonHelper = JsonHelper()
        val categoryList = jsonHelper.loadCategories(this, username)
        val categories = categoryList.map { category: Category -> category.name }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        /*
            Date picker for the Date field.
            Opens a DatePickerDialog when the field receives focus or click.
        */
        etDate.setOnClickListener {
            showDatePicker()
        }

        /*
            Start Time picker for the Start Time field.
            Opens a TimePickerDialog when the field receives focus or click.
        */
        etStartTime.setOnClickListener {
            showTimePicker(etStartTime)
        }

        /*
            End Time picker for the End Time field.
            Opens a TimePickerDialog when the field receives focus or click.
        */
        etEndTime.setOnClickListener {
            showTimePicker(etEndTime)
        }

        /*
            Attach Photo button launches the modern gallery picker.
        */
        btnAttachPhoto.setOnClickListener {
            getContent.launch("image/*")
        }

        /*
            Submit button validates input and saves the expense.
        */
        btnSaveExpense.setOnClickListener {
            saveExpense()
        }
    }

    /**
     * Displays a DatePickerDialog configured with the current system date.
     * The selected date is formatted as yyyy-MM-dd and inserted into the target EditText.
     */
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

    /**
     * Displays a TimePickerDialog configured with the current system time.
     * The selected time is formatted as HH:mm and inserted into the target EditText.
     *
     * @param editText The EditText field to populate with the selected time
     */
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

    /**
     * Validates all required input fields and persists the expense record.
     * Required fields: amount, date, description.
     * The category selection is validated against the loaded category list.
     * If validation passes, the expense is saved via the ViewModel and the activity finishes.
     *
     * Part 3 enhancement: After saving the expense, update the gamification streak
     * to track consecutive days of expense logging for badge awarding.
     */
    private fun saveExpense()
    {
        val amountStr = etAmount.text.toString().trim()
        val date = etDate.text.toString().trim()
        val startTime = etStartTime.text.toString().trim()
        val endTime = etEndTime.text.toString().trim()
        val description = etDescription.text.toString().trim()

        // Validate required fields
        if (amountStr.isEmpty() || date.isEmpty() || description.isEmpty())
        {
            Toast.makeText(this, getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountStr.toDoubleOrNull() ?: 0.0

        // Determine selected category ID from spinner position
        val jsonHelper = JsonHelper()
        val categoryList = jsonHelper.loadCategories(this, username)
        val selectedCategoryPosition = spinnerCategory.selectedItemPosition
        val categoryId = if (selectedCategoryPosition >= 0 && selectedCategoryPosition < categoryList.size)
        {
            categoryList[selectedCategoryPosition].id
        }
        else
        {
            0
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

        // Part 3 gamification: Update streak after successful expense save
        gamificationViewModel.updateStreak(this, username)

        Toast.makeText(this, getString(R.string.expense_submitted), Toast.LENGTH_SHORT).show()
        finish()
    }
}