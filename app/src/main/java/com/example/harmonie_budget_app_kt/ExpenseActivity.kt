// app/kotlin+java/com.example.harmonie_budget_app_kt/ExpenseActivity.kt
package com.example.harmonie_budget_app_kt

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.utils.JsonHelper

/**
 * ExpenseActivity
 * Creates expense entry with start and end time.
 * Photo attachment optional.
 * Data saved to expenses.json in budget_data folder.
 * Fixed: removed unused import directives.
 */
class ExpenseActivity : AppCompatActivity() {
    private lateinit var etAmount: EditText
    private lateinit var etDate: EditText
    private lateinit var etStartTime: EditText
    private lateinit var etEndTime: EditText
    private lateinit var etDescription: EditText
    private lateinit var etCategoryId: EditText
    private lateinit var btnAttachPhoto: Button
    private lateinit var btnSaveExpense: Button
    private var photoUri: String? = null

    private val pickPhotoLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            photoUri = uri.toString()
            Toast.makeText(this, "Photo attached", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)

        etAmount = findViewById(R.id.et_amount)
        etDate = findViewById(R.id.et_date)
        etStartTime = findViewById(R.id.et_start_time)
        etEndTime = findViewById(R.id.et_end_time)
        etDescription = findViewById(R.id.et_description)
        etCategoryId = findViewById(R.id.et_category_id)
        btnAttachPhoto = findViewById(R.id.btn_attach_photo)
        btnSaveExpense = findViewById(R.id.btn_save_expense)

        btnAttachPhoto.setOnClickListener {
            pickPhotoLauncher.launch("image/*")
        }

        btnSaveExpense.setOnClickListener {
            val amountStr = etAmount.text.toString().trim()
            val date = etDate.text.toString().trim()
            val startTime = etStartTime.text.toString().trim()
            val endTime = etEndTime.text.toString().trim()
            val desc = etDescription.text.toString().trim()
            val catIdStr = etCategoryId.text.toString().trim()

            if (amountStr.isEmpty() || date.isEmpty() || startTime.isEmpty() || endTime.isEmpty() || desc.isEmpty() || catIdStr.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val amount = amountStr.toDoubleOrNull() ?: 0.0
            val catId = catIdStr.toIntOrNull() ?: 0

            val expenses: MutableList<Expense> = JsonHelper.loadExpenses(this).toMutableList()
            val maxId = if (expenses.isEmpty()) 0 else expenses.maxOf { it.id }
            expenses.add(Expense(maxId + 1, amount, date, startTime, endTime, desc, catId, photoUri))
            JsonHelper.saveExpenses(this, expenses)
            Toast.makeText(this, "Expense saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}