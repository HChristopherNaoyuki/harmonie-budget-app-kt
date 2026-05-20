package com.example.harmonie_budget_app_kt

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.viewmodels.CategoryViewModel

/**
 * CategoryActivity allows users to manage expense categories.
 *
 * Part 3 Enhancement: Added back arrow navigation to return to the Transactions tab.
 */
class CategoryActivity : AppCompatActivity()
{
    private lateinit var etCategoryName: EditText
    private lateinit var btnAddCategory: Button
    private lateinit var btnReturnHome: Button
    private lateinit var ivBackArrow: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CategoryAdapter
    private lateinit var username: String

    private val categoryViewModel = CategoryViewModel()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        username = intent.getStringExtra("username") ?: "admin"

        etCategoryName = findViewById(R.id.et_category_name)
        btnAddCategory = findViewById(R.id.btn_add_category)
        btnReturnHome = findViewById(R.id.btn_return_home)
        ivBackArrow = findViewById(R.id.iv_back_arrow)
        recyclerView = findViewById(R.id.recycler_categories)

        recyclerView.layoutManager = LinearLayoutManager(this)

        // Back arrow navigation to return to the Transactions tab
        ivBackArrow.setOnClickListener {
            navigateToTransactionsTab()
        }

        btnReturnHome.setOnClickListener {
            finish()
        }

        val categories = categoryViewModel.getCategories(this, username).toMutableList()
        adapter = CategoryAdapter(categories)
        recyclerView.adapter = adapter

        btnAddCategory.setOnClickListener {
            val name = etCategoryName.text.toString().trim()
            if (name.isNotEmpty())
            {
                val nextId = if (categories.isEmpty()) 1 else categories.maxOf { it.id } + 1
                val category = Category(nextId, name)

                categoryViewModel.saveCategory(this, username, category)

                adapter.addCategory(category)

                etCategoryName.text.clear()
                Toast.makeText(this, getString(R.string.category_added), Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Navigates back to the DashboardActivity and selects the Transactions tab.
     */
    private fun navigateToTransactionsTab()
    {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("username", username)
        intent.putExtra("selected_tab", R.id.nav_transactions)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private class CategoryAdapter(private val list: MutableList<Category>)
        : RecyclerView.Adapter<CategoryAdapter.ViewHolder>()
    {
        class ViewHolder(val tv: TextView) : RecyclerView.ViewHolder(tv)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        {
            val tv = TextView(parent.context)
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