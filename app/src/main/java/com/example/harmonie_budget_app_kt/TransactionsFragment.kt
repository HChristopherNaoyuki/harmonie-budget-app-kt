package com.example.harmonie_budget_app_kt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

class TransactionsFragment : Fragment()
{
    private lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = inflater.inflate(R.layout.fragment_transactions, container, false)

        // Username is received from DashboardActivity arguments
        // This ensures user-specific data isolation for all operations
        username = arguments?.getString("username") ?: "admin"

        val btnManageCategories: Button = view.findViewById(R.id.btn_manage_categories)

        // Start CategoryActivity and pass the username
        // (fixes L-05)
        btnManageCategories.setOnClickListener {
            val intent = Intent(requireContext(), CategoryActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        return view
    }

    companion object
    {
        fun newInstance(username: String): TransactionsFragment
        {
            val fragment = TransactionsFragment()
            val args = Bundle()
            args.putString("username", username)
            fragment.arguments = args
            return fragment
        }
    }
}