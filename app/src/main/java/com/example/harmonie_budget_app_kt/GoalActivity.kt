package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.Goal
import com.example.harmonie_budget_app_kt.utils.JsonHelper

class GoalActivity : AppCompatActivity()
{
    private lateinit var etMinGoal: android.widget.EditText
    private lateinit var etMaxGoal: android.widget.EditText
    private lateinit var btnSaveGoals: Button
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        username = intent.getStringExtra("username") ?: "admin"

        etMinGoal = findViewById(R.id.et_min_goal)
        etMaxGoal = findViewById(R.id.et_max_goal)
        btnSaveGoals = findViewById(R.id.btn_save_goals)

        // Load existing goal (required by Part 2 of the assignment)
        val existingGoal = JsonHelper.loadGoal(this, username)
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

                // Fixed range check (lint suggestion satisfied)
                if (minGoal > 0.0 && maxGoal > minGoal)
                {
                    val goal = Goal(minGoal, maxGoal)
                    JsonHelper.saveGoal(this, username, goal)
                    Toast.makeText(this, "Goals saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else
                {
                    Toast.makeText(
                        this,
                        "Maximum goal must be greater than minimum goal (both greater than 0)",
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