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
 * Matches the Budget tab in the GUI image.
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
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ExpenseFragment())
                .addToBackStack(null)
                .commit()
        }

        btnEditBudget.setOnClickListener {
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, GoalFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}