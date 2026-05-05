package com.example.harmonie_budget_app_kt

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.harmonie_budget_app_kt.viewmodels.MoreViewModel

/**
 * MoreFragment displays the settings and utility options for the user.
 * It provides expandable cards for Export Data, Reset Progress, About,
 * Help and Information, Version, and Log Out.
 *
 * The layout uses MaterialCardView components with clickable titles
 * that expand to show additional content when tapped.
 *
 * Part 3 custom features:
 * - Export Data: Exports all user data (categories, expenses, goals, badges, streak) to JSON files
 * - Reset Progress: Clears all user progress data while preserving user account information
 */
class MoreFragment : Fragment()
{
    private lateinit var username: String

    private val moreViewModel = MoreViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = inflater.inflate(R.layout.fragment_more, container, false)

        username = arguments?.getString("username") ?: "admin"

        // Export Data card - using correct IDs from fragment_more.xml
        val tvExportTitle: TextView = view.findViewById(R.id.tv_export_title)
        val layoutExportContent: View = view.findViewById(R.id.layout_export_content)

        tvExportTitle.setOnClickListener
        {
            layoutExportContent.isVisible = !layoutExportContent.isVisible
            if (layoutExportContent.isVisible)
            {
                // Call through the ViewModel layer to export data
                val success = moreViewModel.exportData(requireContext(), username)
                if (success)
                {
                    Toast.makeText(requireContext(), getString(R.string.data_exported), Toast.LENGTH_SHORT).show()
                }
            }
        }

        // Reset Progress card - using correct IDs from fragment_more.xml
        val tvResetTitle: TextView = view.findViewById(R.id.tv_reset_title)
        val layoutResetContent: View = view.findViewById(R.id.layout_reset_content)

        tvResetTitle.setOnClickListener
        {
            layoutResetContent.isVisible = !layoutResetContent.isVisible
            if (layoutResetContent.isVisible)
            {
                // Call through the ViewModel layer to reset progress
                val success = moreViewModel.resetProgress(requireContext(), username)
                if (success)
                {
                    Toast.makeText(requireContext(), getString(R.string.progress_reset), Toast.LENGTH_SHORT).show()
                }
            }
        }

        // About card - using correct IDs from fragment_more.xml
        val tvAboutTitle: TextView = view.findViewById(R.id.tv_about_title)
        val layoutAboutContent: View = view.findViewById(R.id.layout_about_content)

        tvAboutTitle.setOnClickListener
        {
            layoutAboutContent.isVisible = !layoutAboutContent.isVisible
        }

        // Help and Information card - using correct IDs from fragment_more.xml
        val tvHelpTitle: TextView = view.findViewById(R.id.tv_help_title)
        val layoutHelpContent: View = view.findViewById(R.id.layout_help_content)

        tvHelpTitle.setOnClickListener
        {
            layoutHelpContent.isVisible = !layoutHelpContent.isVisible
        }

        // Version card - using correct IDs from fragment_more.xml
        val tvVersionTitle: TextView = view.findViewById(R.id.tv_version_title)
        val layoutVersionContent: View = view.findViewById(R.id.layout_version_content)

        tvVersionTitle.setOnClickListener
        {
            layoutVersionContent.isVisible = !layoutVersionContent.isVisible
        }

        // Log Out card - using correct ID from fragment_more.xml
        val tvLogOutTitle: TextView = view.findViewById(R.id.tv_log_out_title)

        tvLogOutTitle.setOnClickListener
        {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }

    companion object
    {
        /**
         * Factory method to create a new instance of MoreFragment with the specified username.
         * This ensures user-specific data isolation throughout the fragment's lifecycle.
         *
         * @param username The logged-in user's username
         * @return A configured MoreFragment instance
         */
        fun newInstance(username: String): MoreFragment
        {
            val fragment = MoreFragment()
            val args = Bundle()
            args.putString("username", username)
            fragment.arguments = args
            return fragment
        }
    }
}