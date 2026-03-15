// app/kotlin+java/com.example.harmonie_budget_app_kt/CategoryActivity.kt
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

/**
 * CategoryActivity - Create and manage expense categories.
 * Data is saved to categories.json in the dedicated budget_data folder.
 * RecyclerView shows current categories with clean minimal layout.
 * All changes are persisted immediately.
 */
class CategoryActivity : AppCompatActivity() {
    private lateinit var etCategoryName: EditText
    private lateinit var btnAddCategory: Button
    private lateinit var rvCategories: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        etCategoryName = findViewById(R.id.et_category_name)
        btnAddCategory = findViewById(R.id.btn_add_category)
        rvCategories = findViewById(R.id.rv_categories)

        rvCategories.layoutManager = LinearLayoutManager(this)

        loadCategoriesAndSetAdapter()

        btnAddCategory.setOnClickListener {
            val name = etCategoryName.text.toString().trim()
            if (name.isNotEmpty()) {
                val categories = JsonHelper.loadCategories(this).toMutableList()
                val newId = (categories.maxOfOrNull { it.id } ?: 0) + 1
                categories.add(Category(newId, name))
                JsonHelper.saveCategories(this, categories)
                etCategoryName.text.clear()
                loadCategoriesAndSetAdapter()
                Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun loadCategoriesAndSetAdapter() {
        val categories = JsonHelper.loadCategories(this)
        val adapter = CategoryAdapter(categories, this)
        rvCategories.adapter = adapter
    }
}

/**
 * Simple adapter for RecyclerView in CategoryActivity.
 */
class CategoryAdapter(private val list: List<Category>, private val context: android.content.Context)
    : androidx.recyclerview.widget.RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    class ViewHolder(val tv: android.widget.TextView) : androidx.recyclerview.widget.RecyclerView.ViewHolder(tv)

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val tv = android.widget.TextView(parent.context)
        tv.layoutParams = android.view.ViewGroup.LayoutParams(
            android.view.ViewGroup.LayoutParams.MATCH_PARENT,
            120
        )
        tv.setPadding(32, 16, 32, 16)
        return ViewHolder(tv)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv.text = "${list[position].id} - ${list[position].name}"
    }

    override fun getItemCount() = list.size
}