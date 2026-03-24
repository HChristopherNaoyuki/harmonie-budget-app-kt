package com.example.harmonie_budget_app_kt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class HomeFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val btnAddNew: Button = view.findViewById(R.id.btn_add_new)
        val btnEditBudget: Button = view.findViewById(R.id.btn_edit_budget)

        // Username is passed from DashboardActivity
        val username = requireActivity().intent.getStringExtra("username") ?: "admin"

        btnAddNew.setOnClickListener {
            val intent = Intent(requireContext(), ExpenseActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        btnEditBudget.setOnClickListener {
            val intent = Intent(requireContext(), GoalActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        return view
    }
}