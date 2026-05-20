package com.example.harmonie_budget_app_kt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.viewmodels.CategoryViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TransactionsFragment : Fragment()
{
    private lateinit var username: String
    private lateinit var btnManageCategories: Button
    private lateinit var categoriesContainer: LinearLayout

    private val categoryViewModel = CategoryViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = inflater.inflate(R.layout.fragment_transactions, container, false)

        username = arguments?.getString("username") ?: "admin"

        btnManageCategories = view.findViewById(R.id.btn_manage_categories)
        categoriesContainer = view.findViewById(R.id.categories_container)

        btnManageCategories.setOnClickListener {
            val intent = Intent(requireContext(), CategoryActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        return view
    }

    override fun onResume()
    {
        super.onResume()
        loadAndDisplayCategories()
    }

    private fun loadAndDisplayCategories()
    {
        lifecycleScope.launch {
            val categories = withContext(Dispatchers.IO) {
                categoryViewModel.getCategories(requireContext(), username)
            }

            withContext(Dispatchers.Main) {
                displayCategories(categories)
            }
        }
    }

    private fun displayCategories(categories: List<Category>)
    {
        categoriesContainer.removeAllViews()

        if (categories.isEmpty())
        {
            val emptyTextView = TextView(requireContext())
            emptyTextView.setText(R.string.no_categories_added_yet)
            emptyTextView.setTextColor(requireContext().getColor(R.color.text_secondary_light))
            emptyTextView.textSize = 14f
            emptyTextView.setPadding(16, 16, 16, 16)
            emptyTextView.gravity = android.view.Gravity.CENTER
            categoriesContainer.addView(emptyTextView)
        }
        else
        {
            for (category in categories)
            {
                val categoryTextView = TextView(requireContext())
                val categoryText = getString(R.string.category_bullet_format, category.name)
                categoryTextView.text = categoryText
                categoryTextView.setTextColor(requireContext().getColor(R.color.text_primary_light))
                categoryTextView.textSize = 16f
                categoryTextView.setPadding(16, 12, 16, 12)
                categoriesContainer.addView(categoryTextView)
            }
        }
    }

    companion object
    {
        fun newInstance(username: String): TransactionsFragment
        {
            val fragment = TransactionsFragment()
            val args = Bundle()
            args.putString("username", username)
            fragment.arguments = args
            return fragment
        }
    }
}