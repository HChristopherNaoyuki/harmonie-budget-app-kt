package com.example.harmonie_budget_app_kt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class BudgetFragment : Fragment()
{
    private lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = inflater.inflate(R.layout.fragment_budget, container, false)

        // Username is received from DashboardActivity arguments
        // This ensures user-specific data isolation for all operations
        username = arguments?.getString("username") ?: "admin"

        val btnAddExpense: Button = view.findViewById(R.id.btn_add_expense)
        val btnEditBudget: Button = view.findViewById(R.id.btn_edit_budget)

        // Start ExpenseActivity and pass the username
        // (fixes L-03)
        btnAddExpense.setOnClickListener {
            val intent = Intent(requireContext(), ExpenseActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        // Start GoalActivity and pass the username
        // (fixes L-04)
        btnEditBudget.setOnClickListener {
            val intent = Intent(requireContext(), GoalActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        return view
    }

    companion object
    {
        fun newInstance(username: String): BudgetFragment
        {
            val fragment = BudgetFragment()
            val args = Bundle()
            args.putString("username", username)
            fragment.arguments = args
            return fragment
        }
    }
}