package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.TextView

class MoreFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_more, container, false)

        val rvMore: RecyclerView = view.findViewById(R.id.menu_container)

        rvMore.layoutManager = LinearLayoutManager(requireContext())

        // More options list with expandable behavior
        val options = listOf("Export Data", "Reset Progress", "About", "Help and Info", "Version", "Log Out")
        val adapter = MoreAdapter(options) { option: String ->
            Toast.makeText(requireContext(), "$option clicked", Toast.LENGTH_SHORT).show()
        }
        rvMore.adapter = adapter

        return view
    }

    /**
     * Inner adapter class for the more options list.
     * This resolves the unresolved reference 'MoreAdapter'.
     * Each item can be tapped to expand/collapse (logic shown in onBindViewHolder).
     */
    private class MoreAdapter(
        private val list: List<String>,
        private val onClick: (String) -> Unit
    ) : RecyclerView.Adapter<MoreAdapter.ViewHolder>() {

        class ViewHolder(val tv: TextView) : RecyclerView.ViewHolder(tv)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val tv = TextView(parent.context)
            tv.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            tv.setPadding(16, 16, 16, 16)
            return ViewHolder(tv)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val option = list[position]
            holder.tv.text = option
            holder.tv.setOnClickListener { onClick(option) }
        }

        override fun getItemCount(): Int = list.size
    }
}