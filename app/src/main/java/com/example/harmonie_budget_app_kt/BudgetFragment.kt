// app/kotlin+java/com.example.harmonie_budget_app_kt/BudgetFragment.kt
package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

/**
 * BudgetFragment
 * Add Expense and Edit Budget options.
 * Buttons open existing activities (ExpenseActivity and GoalActivity).
 * Matches the Budget tab in the image.
 * Fixed: no unresolved references.
 */
class BudgetFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_budget, container, false)

        val btnAddExpense = view.findViewById<Button>(R.id.btn_add_expense)
        val btnEditBudget = view.findViewById<Button>(R.id.btn_edit_budget)

        btnAddExpense.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), ExpenseActivity::class.java))
        }

        btnEditBudget.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), GoalActivity::class.java))
        }

        return view
    }
}