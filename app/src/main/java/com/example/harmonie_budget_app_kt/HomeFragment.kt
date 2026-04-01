package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment

class HomeFragment : Fragment()
{
    private lateinit var tvTotalBalance: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        // Resolve the total_balance reference required by the fragment layout
        tvTotalBalance = view.findViewById(R.id.total_balance)

        // Example of setting the total balance (value is loaded from JSON in production)
        tvTotalBalance.text = getString(R.string.total_balance) + ": $0.00"

        return view
    }
}