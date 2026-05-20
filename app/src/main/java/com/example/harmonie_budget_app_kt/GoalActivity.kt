package com.example.harmonie_budget_app_kt

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.Goal
import com.example.harmonie_budget_app_kt.viewmodels.GoalViewModel

/**
 * GoalActivity allows users to set monthly minimum and maximum spending goals.
 *
 * Part 3 Enhancements:
 * - Added back arrow navigation to Budget tab
 * - Fixed RETURN HOME button to navigate to Budget tab
 */
class GoalActivity : AppCompatActivity()
{
    private lateinit var etMinGoal: android.widget.EditText
    private lateinit var etMaxGoal: android.widget.EditText
    private lateinit var btnSaveGoals: Button
    private lateinit var btnReturnHome: Button
    private lateinit var ivBackArrow: TextView
    private lateinit var username: String

    private val goalViewModel = GoalViewModel()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        username = intent.getStringExtra("username") ?: "admin"

        etMinGoal = findViewById(R.id.et_min_goal)
        etMaxGoal = findViewById(R.id.et_max_goal)
        btnSaveGoals = findViewById(R.id.btn_save_goals)
        btnReturnHome = findViewById(R.id.btn_return_home)
        ivBackArrow = findViewById(R.id.iv_back_arrow)

        // Back arrow navigation to Budget tab
        ivBackArrow.setOnClickListener {
            navigateToBudgetTab()
        }

        // Fixed RETURN HOME button navigates to Budget tab
        btnReturnHome.setOnClickListener {
            navigateToBudgetTab()
        }

        val existingGoal = goalViewModel.getGoal(this, username)
        if (existingGoal != null)
        {
            etMinGoal.setText(existingGoal.minGoal.toString())
            etMaxGoal.setText(existingGoal.maxGoal.toString())
        }

        btnSaveGoals.setOnClickListener {
            val minStr = etMinGoal.text.toString().trim()
            val maxStr = etMaxGoal.text.toString().trim()

            if (minStr.isNotEmpty() && maxStr.isNotEmpty())
            {
                val minGoal = minStr.toDoubleOrNull() ?: 0.0
                val maxGoal = maxStr.toDoubleOrNull() ?: 0.0

                val isValidGoal = (minGoal > 0.0 && maxGoal > minGoal && maxGoal <= 1000000.0)

                if (isValidGoal)
                {
                    val goal = Goal(minGoal, maxGoal)
                    goalViewModel.saveGoal(this, username, goal)

                    Toast.makeText(this, "Goals saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else
                {
                    Toast.makeText(
                        this,
                        "Maximum goal must be greater than minimum goal (both greater than 0 and under 1,000,000)",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
            else
            {
                Toast.makeText(this, "Please enter both goals", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Navigates back to the DashboardActivity and selects the Budget tab.
     * This method is used by both the back arrow and the RETURN HOME button.
     */
    private fun navigateToBudgetTab()
    {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("username", username)
        intent.putExtra("selected_tab", R.id.nav_budget)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}