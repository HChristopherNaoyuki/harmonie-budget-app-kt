package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.Goal
import com.example.harmonie_budget_app_kt.utils.JsonHelper
import com.example.harmonie_budget_app_kt.viewmodels.GoalViewModel

class GoalActivity : AppCompatActivity()
{
    private lateinit var etMinGoal: android.widget.EditText
    private lateinit var etMaxGoal: android.widget.EditText
    private lateinit var btnSaveGoals: Button
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

        // Call through the ViewModel layer
        val existingGoal = goalViewModel.getGoal(username)
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

                if (minGoal > 0.0 && maxGoal > minGoal && maxGoal <= 1000000.0)
                {
                    val goal = Goal(minGoal, maxGoal)

                    // Call through the ViewModel layer
                    goalViewModel.saveGoal(username, goal)

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
}