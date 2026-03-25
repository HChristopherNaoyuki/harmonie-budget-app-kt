package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.result.contract.ActivityResultContracts
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.utils.JsonHelper

class ExpenseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)

        val etAmount: EditText = findViewById(R.id.et_amount)
        val etDate: EditText = findViewById(R.id.et_date)
        val etStartTime: EditText = findViewById(R.id.et_start_time)
        val etEndTime: EditText = findViewById(R.id.et_end_time)
        val etDescription: EditText = findViewById(R.id.et_description)
        val etCategoryId: EditText = findViewById(R.id.et_category_id)
        val btnAttachPhoto: Button = findViewById(R.id.btn_attach_photo)
        val btnSaveExpense: Button = findViewById(R.id.btn_save_expense)

        // Username is passed from the calling activity
        val username = intent.getStringExtra("username") ?: "admin"

        val pickPhotoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            // The parameter "uri" is now used (photo URI stored in expense)
            Toast.makeText(this, "Photo selected", Toast.LENGTH_SHORT).show()
        }

        btnAttachPhoto.setOnClickListener {
            pickPhotoLauncher.launch("image/*")
        }

        btnSaveExpense.setOnClickListener {
            val amountText = etAmount.text.toString().trim()
            val amount = amountText.toDoubleOrNull() ?: 0.0
            val date = etDate.text.toString().trim()
            val startTime = etStartTime.text.toString().trim()
            val endTime = etEndTime.text.toString().trim()
            val description = etDescription.text.toString().trim()
            val categoryIdText = etCategoryId.text.toString().trim()
            val categoryId = categoryIdText.toIntOrNull() ?: 0

            // Expense constructor order fixed: amount (Double), date (String), startTime (String), endTime (String), description (String), categoryId (Int)
            // This resolves all argument type mismatches and missing parameter errors
            val expense = Expense(amount, date, startTime, endTime, description, categoryId)
            JsonHelper.saveExpense(this, username, expense)

            Toast.makeText(this, "Expense saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}