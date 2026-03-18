// app/kotlin+java/com.example.harmonie_budget_app_kt/BudgetsFragment.kt
package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

/**
 * BudgetsFragment
 * Shows category totals as the "Budgets" tab in the image.
 * Button opens the full CategoryTotalActivity.
 * Simple placeholder for "Smart Detection" as shown in the image.
 */
class BudgetsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_budgets, container, false)

        val btnViewTotals = view.findViewById<Button>(R.id.btn_view_totals)

        btnViewTotals.setOnClickListener {
            // Open full CategoryTotalActivity
            startActivity(android.content.Intent(requireContext(), CategoryTotalActivity::class.java))
        }

        return view
    }
}