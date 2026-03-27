package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import com.example.harmonie_budget_app_kt.utils.JsonHelper

class MoreFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_more, container, false)

        // Username is stored for future user-specific content in accordance with the data isolation requirement in Part 2 of the assignment and the process document
        val username = activity?.intent?.getStringExtra("username") ?: "admin"

        val cardExport: View = view.findViewById(R.id.card_export)
        val tvExportTitle: TextView = view.findViewById(R.id.tv_export_title)
        val layoutExportContent: View = view.findViewById(R.id.layout_export_content)

        tvExportTitle.setOnClickListener {
            layoutExportContent.isVisible = !layoutExportContent.isVisible
        }

        return view
    }
}