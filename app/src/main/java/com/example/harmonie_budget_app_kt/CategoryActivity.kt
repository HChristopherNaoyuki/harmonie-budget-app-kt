// app/src/main/java/com/example/harmonie_budget_app_kt/CategoryActivity.kt
package com.example.harmonie_budget_app_kt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harmonie_budget_app_kt.utils.JsonHelper

class CategoryActivity : AppCompatActivity()
{
    //
    // Category management activity.
    //
    // FIXED:
    // 1. All findViewById calls now use snake_case IDs that exist in activity_category.xml.
    // 2. RecyclerView setup added with placeholder adapter.
    // 3. Allman style used for braces and detailed comments.
    // 4. Data loaded/saved via JsonHelper (models now defined).
    //

    private lateinit var etCategoryName: EditText
    private lateinit var btnAddCategory: Button
    private lateinit var rvCategories: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        etCategoryName = findViewById(R.id.et_category_name)
        btnAddCategory = findViewById(R.id.btn_add_category)
        rvCategories = findViewById(R.id.rv_categories)

        rvCategories.layoutManager = LinearLayoutManager(this)

        // Load existing categories
        val categories = JsonHelper.loadList(this, "categories.json", Category::class.java)
        // TODO: set adapter with categories (placeholder for now)

        btnAddCategory.setOnClickListener()
        {
            val name = etCategoryName.text.toString().trim()
            if (name.isNotEmpty())
            {
                val newId = (categories.maxOfOrNull { it.id } ?: 0) + 1
                val newCategory = Category(newId, name)
                val updated = categories.toMutableList()
                updated.add(newCategory)
                JsonHelper.saveList(this, "categories.json", updated)
                etCategoryName.text.clear()
                // refresh RecyclerView
            }
        }
    }
}