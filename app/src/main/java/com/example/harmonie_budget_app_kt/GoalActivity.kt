package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.Goal
import com.example.harmonie_budget_app_kt.utils.JsonHelper

class GoalActivity : AppCompatActivity()
{
    private lateinit var etMinGoal: EditText
    private lateinit var etMaxGoal: EditText
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

        // Load existing goal (now calls loadGoal to remove "never used" warning)
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
                val minGoal = minStr.toIntOrNull() ?: 0
                val maxGoal = maxStr.toIntOrNull() ?: 0

                if (minGoal > 0 && maxGoal > minGoal)
                {
                    val goal = Goal(minGoal, maxGoal)
                    JsonHelper.saveGoal(this, username, goal)
                    Toast.makeText(this, "Goals saved", Toast.LENGTH_SHORT).show()
                    finish()
                }
                else
                {
                    Toast.makeText(this, "Max goal must be greater than min goal", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this, "Please enter both goals", Toast.LENGTH_SHORT).show()
            }
        }
    }
}