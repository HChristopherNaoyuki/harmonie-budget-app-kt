package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.harmonie_budget_app_kt.utils.JsonHelper

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val username = activity?.intent?.getStringExtra("username") ?: "admin"

        val tvTotalBalance: TextView = view.findViewById(R.id.tv_total_balance)

        // Load user-specific data (totals calculated from expenses)
        val expenses = JsonHelper.loadExpenses(requireContext(), username)
        val total = expenses.sumOf { it.amount }
        tvTotalBalance.text = "$${total}"

        // Additional dashboard elements populated here from JSON (per mockup)

        return view
    }
}