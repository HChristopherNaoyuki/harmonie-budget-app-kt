package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.utils.JsonHelper

class CategoryActivity : AppCompatActivity()
{
    private lateinit var etCategoryName: EditText
    private lateinit var btnAddCategory: Button
    private lateinit var btnReturnHome: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        username = intent.getStringExtra("username") ?: "admin"

        etCategoryName = findViewById(R.id.et_category_name)
        btnAddCategory = findViewById(R.id.btn_add_category)
        btnReturnHome = findViewById(R.id.btn_return_home)
        recyclerView = findViewById(R.id.recycler_categories)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load the initial list of categories for the logged-in user
        // (data isolation is enforced by the JsonHelper filename pattern)
        val categories = JsonHelper.loadCategories(this, username).toMutableList()
        adapter = CategoryAdapter(categories)
        recyclerView.adapter = adapter

        btnAddCategory.setOnClickListener {
            val name = etCategoryName.text.toString().trim()
            if (name.isNotEmpty())
            {
                val category = Category(0, name)
                JsonHelper.saveCategory(this, username, category)

                // Add the new category directly to the adapter's mutable list
                // and notify only the newly inserted item
                // (this uses a specific change event and resolves the lint rule)
                adapter.addCategory(category)

                etCategoryName.text.clear()
                Toast.makeText(this, "Category added", Toast.LENGTH_SHORT).show()
            }
        }

        btnReturnHome.setOnClickListener {
            finish()
        }
    }

    private class CategoryAdapter(private val list: MutableList<Category>)
        : RecyclerView.Adapter<CategoryAdapter.ViewHolder>()
    {
        class ViewHolder(val tv: android.widget.TextView) : RecyclerView.ViewHolder(tv)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        {
            val tv = android.widget.TextView(parent.context)
            tv.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            tv.setPadding(32, 16, 32, 16)
            return ViewHolder(tv)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int)
        {
            holder.tv.text = list[position].name
        }

        override fun getItemCount(): Int = list.size

        // Adds a single category and notifies only the inserted position
        // This uses a specific change event (notifyItemInserted) instead of
        // notifyDataSetChanged, addressing the Android lint recommendation
        // for better performance and RecyclerView efficiency.
        fun addCategory(category: Category)
        {
            list.add(category)
            notifyItemInserted(list.size - 1)
        }
    }
}