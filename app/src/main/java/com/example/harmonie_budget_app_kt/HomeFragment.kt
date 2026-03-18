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
 * Matches the Home tab in the GUI image.
 */
class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val tvDashboard = view.findViewById<TextView>(R.id.tv_dashboard)
        val btnAddNew = view.findViewById<Button>(R.id.btn_add_new)
        val btnEditBudget = view.findViewById<Button>(R.id.btn_edit_budget)

        tvDashboard.text = "My Dashboard"

        btnAddNew.setOnClickListener {
            // Navigate to add expense
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, BudgetFragment())
                .addToBackStack(null)
                .commit()
        }

        btnEditBudget.setOnClickListener {
            // Navigate to goal edit
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, GoalFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}