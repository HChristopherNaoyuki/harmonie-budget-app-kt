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
import com.example.harmonie_budget_app_kt.utils.JsonHelper

class MoreFragment : Fragment()
{
    private lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = inflater.inflate(R.layout.fragment_more, container, false)

        // Username is received from DashboardActivity arguments
        // This ensures user-specific data isolation for all operations
        username = arguments?.getString("username") ?: "admin"

        // Export Data card
        val tvExportTitle: TextView = view.findViewById(R.id.tv_export_title)
        val layoutExportContent: View = view.findViewById(R.id.layout_export_content)
        tvExportTitle.setOnClickListener {
            layoutExportContent.isVisible = !layoutExportContent.isVisible
            if (layoutExportContent.isVisible)
            {
                Thread {
                    val success = JsonHelper.exportData(requireContext().applicationContext, username)
                    requireActivity().runOnUiThread {
                        if (success)
                        {
                            Toast.makeText(requireContext(), getString(R.string.data_exported), Toast.LENGTH_SHORT).show()
                        }
                    }
                }.start()
            }
        }

        // Reset Progress card
        val tvResetTitle: TextView = view.findViewById(R.id.tv_reset_title)
        val layoutResetContent: View = view.findViewById(R.id.layout_reset_content)
        tvResetTitle.setOnClickListener {
            layoutResetContent.isVisible = !layoutResetContent.isVisible
            if (layoutResetContent.isVisible)
            {
                Thread {
                    val success = JsonHelper.resetProgress(requireContext().applicationContext, username)
                    requireActivity().runOnUiThread {
                        if (success)
                        {
                            Toast.makeText(requireContext(), getString(R.string.progress_reset), Toast.LENGTH_SHORT).show()
                        }
                    }
                }.start()
            }
        }

        // About card
        val tvAboutTitle: TextView = view.findViewById(R.id.tv_about_title)
        val layoutAboutContent: View = view.findViewById(R.id.layout_about_content)
        tvAboutTitle.setOnClickListener {
            layoutAboutContent.isVisible = !layoutAboutContent.isVisible
        }

        // Help and Information card
        val tvHelpTitle: TextView = view.findViewById(R.id.tv_help_title)
        val layoutHelpContent: View = view.findViewById(R.id.layout_help_content)
        tvHelpTitle.setOnClickListener {
            layoutHelpContent.isVisible = !layoutHelpContent.isVisible
        }

        // Version card
        val tvVersionTitle: TextView = view.findViewById(R.id.tv_version_title)
        val layoutVersionContent: View = view.findViewById(R.id.layout_version_content)
        tvVersionTitle.setOnClickListener {
            layoutVersionContent.isVisible = !layoutVersionContent.isVisible
        }

        // Log Out card (immediate action, no toggle)
        // (fixes L-08)
        val tvLogOutTitle: TextView = view.findViewById(R.id.tv_log_out_title)
        tvLogOutTitle.setOnClickListener {
            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }

    companion object
    {
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