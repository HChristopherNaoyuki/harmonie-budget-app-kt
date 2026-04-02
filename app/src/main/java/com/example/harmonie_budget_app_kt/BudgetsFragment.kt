package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class BudgetsFragment : Fragment()
{
    private lateinit var btnViewTotals: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = inflater.inflate(R.layout.fragment_budgets, container, false)

        // Resolve the btn_view_totals ID declared in fragment_budgets.xml
        btnViewTotals = view.findViewById(R.id.btn_view_totals)

        // The button is now present and can be wired to a listener in future expansions
        // (no action is attached here because the fragment only needs the ID to compile)

        return view
    }
}