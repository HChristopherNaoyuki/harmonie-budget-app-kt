package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.utils.JsonHelper

class CategoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        val etCategoryName: EditText = findViewById(R.id.et_category_name)
        val btnAddCategory: Button = findViewById(R.id.btn_add_category)
        val rvCategories: RecyclerView = findViewById(R.id.rv_categories)

        // Username is passed from the calling activity (Dashboard or Main)
        val username = intent.getStringExtra("username") ?: "admin"

        rvCategories.layoutManager = LinearLayoutManager(this)

        // Load categories for this user only
        val categories = JsonHelper.loadCategories(this, username)
        val adapter = CategoryAdapter(categories)
        rvCategories.adapter = adapter

        btnAddCategory.setOnClickListener {
            val name = etCategoryName.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Category name is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val category = Category(name)
            JsonHelper.saveCategory(this, username, category)

            // Refresh list
            val updatedList = JsonHelper.loadCategories(this, username)
            adapter.updateList(updatedList)

            etCategoryName.text.clear()
            Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show()
        }
    }
}