package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MoreFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_more, container, false)

        val rvMore: RecyclerView = view.findViewById(R.id.menu_container)

        rvMore.layoutManager = LinearLayoutManager(requireContext())

        // More options list with expandable behavior
        val options = listOf("Export Data", "Reset Progress", "About", "Help and Info", "Version", "Log Out")
        val adapter = MoreAdapter(options) { option ->
            // Expand / collapse logic handled in adapter
            Toast.makeText(requireContext(), "$option clicked", Toast.LENGTH_SHORT).show()
        }
        rvMore.adapter = adapter

        return view
    }
}