// app/kotlin+java/com.example.harmonie_budget_app_kt/ExpenseActivity.kt
package com.example.harmonie_budget_app_kt

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.utils.JsonHelper

/**
 * ExpenseActivity - Create expense with optional photo attachment.
 * Photo URI saved as string in expenses.json.
 * All data in dedicated budget_data folder.
 */
class ExpenseActivity : AppCompatActivity() {
    private lateinit var etAmount: EditText
    private lateinit var etDate: EditText
    private lateinit var etDescription: EditText
    private lateinit var etCategoryId: EditText
    private lateinit var btnAddPhoto: Button
    private lateinit var btnSaveExpense: Button
    private var photoUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_expense)

        etAmount = findViewById(R.id.et_amount)
        etDate = findViewById(R.id.et_date)
        etDescription = findViewById(R.id.et_description)
        etCategoryId = findViewById(R.id.et_category_id)
        btnAddPhoto = findViewById(R.id.btn_add_photo)
        btnSaveExpense = findViewById(R.id.btn_save_expense)

        btnAddPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }

        btnSaveExpense.setOnClickListener {
            val amountStr = etAmount.text.toString().trim()
            val date = etDate.text.toString().trim()
            val desc = etDescription.text.toString().trim()
            val catIdStr = etCategoryId.text.toString().trim()
            if (amountStr.isEmpty() || date.isEmpty() || desc.isEmpty() || catIdStr.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val amount = amountStr.toDoubleOrNull() ?: 0.0
            val catId = catIdStr.toIntOrNull() ?: 0
            val expenses = JsonHelper.loadExpenses(this).toMutableList()
            val maxId = if (expenses.isEmpty()) 0 else expenses.maxOf { it.id }
            expenses.add(Expense(maxId + 1, amount, date, desc, catId, photoUri))
            JsonHelper.saveExpenses(this, expenses)
            Toast.makeText(this, "Expense saved!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            photoUri = data?.data.toString()
            Toast.makeText(this, "Photo attached", Toast.LENGTH_SHORT).show()
        }
    }
}