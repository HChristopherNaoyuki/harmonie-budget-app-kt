// app/kotlin+java/com.example.harmonie_budget_app_kt/MoreFragment.kt
package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment

/**
 * MoreFragment
 * Shows the menu list exactly as in the "More" tab of the image.
 * Items: Export Data, Theme, Reset Progress, About, Help & Info, Version, Contact.
 * Clean list style with icons (simple TextView for prototype).
 */
class MoreFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_more, container, false)

        val menuContainer = view.findViewById<LinearLayout>(R.id.menu_container)

        // Menu items matching the image
        val items = listOf(
            "Export Data",
            "Theme",
            "Reset Progress",
            "About",
            "Help & Info",
            "Version",
            "Contact"
        )

        for (item in items) {
            val tv = TextView(requireContext())
            tv.text = item
            tv.setPadding(32, 16, 32, 16)
            tv.textSize = 18f
            tv.setTextColor(0xFF1C1C1E.toInt())
            menuContainer.addView(tv)
        }

        return view
    }
}