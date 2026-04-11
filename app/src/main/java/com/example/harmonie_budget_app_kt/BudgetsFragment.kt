package com.example.harmonie_budget_app_kt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class BudgetsFragment : Fragment()
{
    private lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = inflater.inflate(R.layout.fragment_budgets, container, false)

        // Username is received from DashboardActivity arguments
        // This ensures user-specific data isolation for all operations
        username = arguments?.getString("username") ?: "admin"

        val btnViewTotals: Button = view.findViewById(R.id.btn_view_totals)

        // Start CategoryTotalActivity and pass the username
        // (fixes L-02)
        btnViewTotals.setOnClickListener {
            val intent = Intent(requireContext(), CategoryTotalActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        return view
    }

    companion object
    {
        fun newInstance(username: String): BudgetsFragment
        {
            val fragment = BudgetsFragment()
            val args = Bundle()
            args.putString("username", username)
            fragment.arguments = args
            return fragment
        }
    }
}