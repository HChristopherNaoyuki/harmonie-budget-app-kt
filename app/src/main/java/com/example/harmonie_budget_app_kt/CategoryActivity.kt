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
 * CategoryActivity - Create and manage categories.
 * Uses RecyclerView for list display (clean Apple-like layout).
 * JSON persistence with auto ID generation.
 */
class CategoryActivity : AppCompatActivity() {

    private lateinit var etCategoryName: EditText
    private lateinit var btnAddCategory: Button
    private lateinit var rvCategories: RecyclerView
    private val categories = mutableListOf<Category>()
    private lateinit var adapter: CategoryAdapter // Defined below in same file for simplicity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        etCategoryName = findViewById(R.id.et_category_name)
        btnAddCategory = findViewById(R.id.btn_add_category)
        rvCategories = findViewById(R.id.rv_categories)

        rvCategories.layoutManager = LinearLayoutManager(this)
        adapter = CategoryAdapter(categories)
        rvCategories.adapter = adapter

        loadCategories()

        btnAddCategory.setOnClickListener {
            val name = etCategoryName.text.toString().trim()
            if (name.isEmpty()) {
                Toast.makeText(this, "Enter category name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val maxId = if (categories.isEmpty()) 0 else categories.maxOf { it.id }
            categories.add(Category(maxId + 1, name))
            JsonHelper.saveCategories(this, categories)
            adapter.notifyDataSetChanged()
            etCategoryName.text.clear()
            Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadCategories() {
        categories.clear()
        categories.addAll(JsonHelper.loadCategories(this))
        adapter.notifyDataSetChanged()
    }
}

// Simple Adapter for RecyclerView (inside same file for prototype completeness)
class CategoryAdapter(private val list: List<Category>) : androidx.recyclerview.widget.RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    class ViewHolder(val textView: android.widget.TextView) : androidx.recyclerview.widget.RecyclerView.ViewHolder(textView)

    override fun onCreateViewHolder(parent: android.view.ViewGroup, viewType: Int): ViewHolder {
        val tv = android.widget.TextView(parent.context)
        tv.layoutParams = android.view.ViewGroup.LayoutParams(android.view.ViewGroup.LayoutParams.MATCH_PARENT, 100)
        tv.setPadding(32, 16, 32, 16)
        return ViewHolder(tv)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = "${list[position].id}: ${list[position].name}"
    }

    override fun getItemCount() = list.size
}