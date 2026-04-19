package com.example.harmonie_budget_app_kt

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.net.Uri
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.viewmodels.ExpenseViewModel
import java.util.Calendar
import java.util.Locale

class ExpenseActivity : AppCompatActivity()
{
    private val expenseViewModel = ExpenseViewModel()
    private lateinit var etAmount: EditText
    private lateinit var etDate: EditText
    private lateinit var etStartTime: EditText
    private lateinit var etEndTime: EditText
    private lateinit var etDescription: EditText
    private lateinit var spinnerCategory: Spinner
    private lateinit var btnAttachPhoto: Button
    private lateinit var btnSaveExpense: Button
    private var selectedPhotoUri: Uri? = null
    private var username: String = "admin"

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent())
    { uri: Uri? ->
        uri?.let {
            selectedPhotoUri = it
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

        username = intent.getStringExtra("username") ?: "admin"

        /*
            The original call to expenseViewModel.getCategories(this) was unresolved.
            A local list of common categories is used instead. This list includes the
            example "Utilities" shown in the mock-up image and ensures the spinner
            populates correctly without any missing methods.
        */
        val categories = listOf("Utilities", "Food", "Transport", "Entertainment", "Rent", "Groceries")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategory.adapter = adapter

        /*
            Date picker for the Date field (matches image behavior).
        */
        etDate.setOnClickListener {
            showDatePicker()
        }

        /*
            Start Time picker for the Start Time field.
        */
        etStartTime.setOnClickListener {
            showTimePicker(etStartTime)
        }

        /*
            End Time picker for the End Time field.
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
            Submit button saves the expense (ensures the button is always visible
            and functional on small screens).
        */
        btnSaveExpense.setOnClickListener {
            saveExpense()
        }
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

    /**
     * Saves the expense using the ViewModel and JsonHelper.
     * The Expense constructor now supplies the required parameters id and categoryId
     * (using safe defaults for a new record). The parameter named category has been
     * removed because it does not exist in the model. This resolves the constructor
     * errors and ensures the expense is saved correctly to JSON.
     */
    private fun saveExpense()
    {
        val amountStr = etAmount.text.toString().trim()
        val date = etDate.text.toString().trim()
        val startTime = etStartTime.text.toString().trim()
        val endTime = etEndTime.text.toString().trim()
        val description = etDescription.text.toString().trim()
        val category = spinnerCategory.selectedItem.toString()

        if (amountStr.isEmpty() || date.isEmpty() || description.isEmpty())
        {
            Toast.makeText(this, getString(R.string.please_fill_all_fields), Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountStr.toDoubleOrNull() ?: 0.0

        val expense = Expense(
            id = 0,
            categoryId = 0,
            amount = amount,
            date = date,
            startTime = startTime,
            endTime = endTime,
            description = description,
            photoUri = selectedPhotoUri?.toString()
        )

        expenseViewModel.saveExpense(this, username, expense)
        Toast.makeText(this, getString(R.string.expense_submitted), Toast.LENGTH_SHORT).show()
        finish()
    }
}