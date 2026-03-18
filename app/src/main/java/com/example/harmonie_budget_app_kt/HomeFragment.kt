// app/kotlin+java/com.example.harmonie_budget_app_kt/HomeFragment.kt
package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment

/**
 * HomeFragment
 * Dashboard screen with "My Dashboard" and buttons for add new and edit budget.
 * Buttons open existing activities (ExpenseActivity and GoalActivity).
 * Matches the Home tab in the image.
 * Fixed: used string resource for setText.
 */
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val btnAddNew = view.findViewById<Button>(R.id.btn_add_new)
        val btnEditBudget = view.findViewById<Button>(R.id.btn_edit_budget)

        btnAddNew.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), ExpenseActivity::class.java))
        }

        btnEditBudget.setOnClickListener {
            startActivity(android.content.Intent(requireContext(), GoalActivity::class.java))
        }

        return view
    }
}