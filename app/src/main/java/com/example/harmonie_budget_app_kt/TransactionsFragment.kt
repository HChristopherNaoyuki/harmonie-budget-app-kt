// app/kotlin+java/com.example.harmonie_budget_app_kt/TransactionsFragment.kt
package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

/**
 * TransactionsFragment
 * Handles category management as shown in the "Transactions" tab of the image.
 * Button opens the full CategoryActivity for adding and viewing categories.
 * Clean card layout with minimal design.
 */
class TransactionsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_transactions, container, false)

        val btnManageCategories = view.findViewById<Button>(R.id.btn_manage_categories)

        btnManageCategories.setOnClickListener {
            // Open full CategoryActivity
            startActivity(android.content.Intent(requireContext(), CategoryActivity::class.java))
        }

        return view
    }
}