package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
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

            // Category constructor expects (id: Int, name: String)
            // id is auto-generated as 0 for new categories
            val category = Category(0, name)
            JsonHelper.saveCategory(this, username, category)

            // Refresh list
            val updatedList = JsonHelper.loadCategories(this, username)
            adapter.updateList(updatedList)

            etCategoryName.text.clear()
            Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Inner adapter class for the category list.
     * This resolves the unresolved reference 'CategoryAdapter'.
     * Displays each category in a simple TextView.
     * Uses notifyDataSetChanged only when necessary (as warned).
     */
    private class CategoryAdapter(private var list: List<Category>)
        : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

        class ViewHolder(val tv: TextView) : RecyclerView.ViewHolder(tv)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val tv = TextView(parent.context)
            tv.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            tv.setPadding(16, 16, 16, 16)
            return ViewHolder(tv)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.tv.text = list[position].name
        }

        override fun getItemCount(): Int = list.size

        fun updateList(newList: List<Category>) {
            list = newList
            notifyDataSetChanged()
        }
    }
}