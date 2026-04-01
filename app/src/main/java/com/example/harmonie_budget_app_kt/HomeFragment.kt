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

        // Resolve the total_balance view ID declared in fragment_home.xml
        tvTotalBalance = view.findViewById(R.id.total_balance)

        // Use the formatted string resource with placeholder
        // (avoids concatenation and satisfies "do not concatenate text" and
        // "string literal cannot be translated" lint rules)
        // Example value 0.00 is for demonstration, replaced by JSON-loaded balance in full implementation
        val formattedBalance = getString(R.string.total_balance, 0.00)
        tvTotalBalance.text = formattedBalance

        return view
    }
}