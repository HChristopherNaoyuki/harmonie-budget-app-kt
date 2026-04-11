package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.harmonie_budget_app_kt.utils.JsonHelper

class HomeFragment : Fragment()
{
    private lateinit var username: String
    private lateinit var tvTotalBalance: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        username = arguments?.getString("username") ?: "admin"

        tvTotalBalance = view.findViewById(R.id.tv_total_balance)

        // Calculate real total balance from the user's expense records
        // (fixes L-18)
        val expenses = JsonHelper.loadExpenses(requireContext(), username)
        val total = expenses.sumOf { it.amount }

        tvTotalBalance.text = getString(R.string.total_balance, total)

        return view
    }

    companion object
    {
        fun newInstance(username: String): HomeFragment
        {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString("username", username)
            fragment.arguments = args
            return fragment
        }
    }
}