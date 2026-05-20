package com.example.harmonie_budget_app_kt

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import com.example.harmonie_budget_app_kt.viewmodels.MoreViewModel

class MoreFragment : Fragment()
{
    private lateinit var username: String

    private val moreViewModel = MoreViewModel()

    companion object
    {
        private const val PREFS_NAME = "harmonie_prefs"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_LOGGED_IN_USERNAME = "logged_in_username"

        fun newInstance(username: String): MoreFragment
        {
            val fragment = MoreFragment()
            val args = Bundle()
            args.putString("username", username)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = inflater.inflate(R.layout.fragment_more, container, false)

        username = arguments?.getString("username") ?: "admin"

        val sharedPrefs = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        val tvExportTitle: TextView = view.findViewById(R.id.tv_export_title)
        val layoutExportContent: View = view.findViewById(R.id.layout_export_content)

        tvExportTitle.setOnClickListener {
            layoutExportContent.isVisible = !layoutExportContent.isVisible
            if (layoutExportContent.isVisible)
            {
                val success = moreViewModel.exportData(requireContext(), username)
                if (success)
                {
                    Toast.makeText(requireContext(), getString(R.string.data_exported), Toast.LENGTH_SHORT).show()
                }
            }
        }

        val tvResetTitle: TextView = view.findViewById(R.id.tv_reset_title)
        val layoutResetContent: View = view.findViewById(R.id.layout_reset_content)

        tvResetTitle.setOnClickListener {
            layoutResetContent.isVisible = !layoutResetContent.isVisible
            if (layoutResetContent.isVisible)
            {
                val success = moreViewModel.resetProgress(requireContext(), username)
                if (success)
                {
                    Toast.makeText(requireContext(), getString(R.string.progress_reset), Toast.LENGTH_SHORT).show()
                }
            }
        }

        val tvAboutTitle: TextView = view.findViewById(R.id.tv_about_title)
        val layoutAboutContent: View = view.findViewById(R.id.layout_about_content)

        tvAboutTitle.setOnClickListener {
            layoutAboutContent.isVisible = !layoutAboutContent.isVisible
        }

        val tvHelpTitle: TextView = view.findViewById(R.id.tv_help_title)
        val layoutHelpContent: View = view.findViewById(R.id.layout_help_content)

        tvHelpTitle.setOnClickListener {
            layoutHelpContent.isVisible = !layoutHelpContent.isVisible
        }

        val tvVersionTitle: TextView = view.findViewById(R.id.tv_version_title)
        val layoutVersionContent: View = view.findViewById(R.id.layout_version_content)

        tvVersionTitle.setOnClickListener {
            layoutVersionContent.isVisible = !layoutVersionContent.isVisible
        }

        val tvLogOutTitle: TextView = view.findViewById(R.id.tv_log_out_title)

        tvLogOutTitle.setOnClickListener {
            sharedPrefs.edit {
                putBoolean(KEY_IS_LOGGED_IN, false)
                remove(KEY_LOGGED_IN_USERNAME)
            }

            val intent = Intent(requireContext(), MainActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            requireActivity().finish()
        }

        return view
    }
}