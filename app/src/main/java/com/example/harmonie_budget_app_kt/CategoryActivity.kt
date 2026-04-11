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

        val categories = JsonHelper.loadCategories(this.applicationContext, username).toMutableList()
        adapter = CategoryAdapter(categories)
        recyclerView.adapter = adapter

        btnAddCategory.setOnClickListener {
            val name = etCategoryName.text.toString().trim()
            if (name.isNotEmpty())
            {
                // Assign unique auto-increment ID
                // (fixes L-16)
                val nextId = if (categories.isEmpty()) 1 else categories.maxOf { it.id } + 1
                val category = Category(nextId, name)
                JsonHelper.saveCategory(this.applicationContext, username, category)

                adapter.addCategory(category)

                etCategoryName.text.clear()
                Toast.makeText(this, getString(R.string.category_added), Toast.LENGTH_SHORT).show()
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

        fun addCategory(category: Category)
        {
            list.add(category)
            notifyItemInserted(list.size - 1)
        }
    }
}