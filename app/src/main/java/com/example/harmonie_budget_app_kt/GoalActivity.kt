// app/kotlin+java/com.example.harmonie_budget_app_kt/GoalActivity.kt
package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.Goal
import com.example.harmonie_budget_app_kt.utils.JsonHelper

/**
 * GoalActivity - Set min and max monthly spending goals.
 * Saved to JSON.
 */
class GoalActivity : AppCompatActivity() {

    private lateinit var etMinGoal: EditText
    private lateinit var etMaxGoal: EditText
    private lateinit var btnSaveGoal: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        etMinGoal = findViewById(R.id.et_min_goal)
        etMaxGoal = findViewById(R.id.et_max_goal)
        btnSaveGoal = findViewById(R.id.btn_save_goal)

        // Load existing
        val current = JsonHelper.loadGoal(this)
        etMinGoal.setText(current.minMonthly.toString())
        etMaxGoal.setText(current.maxMonthly.toString())

        btnSaveGoal.setOnClickListener {
            val min = etMinGoal.text.toString().toDoubleOrNull() ?: 0.0
            val max = etMaxGoal.text.toString().toDoubleOrNull() ?: 0.0
            if (min > max) {
                Toast.makeText(this, "Min cannot exceed Max", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            JsonHelper.saveGoal(this, Goal(min, max))
            Toast.makeText(this, "Goals saved", Toast.LENGTH_SHORT).show()
        }
    }
}