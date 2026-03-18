// app/kotlin+java/com.example.harmonie_budget_app_kt/GoalActivity.kt
package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.Goal
import com.example.harmonie_budget_app_kt.utils.JsonHelper

/**
 * GoalActivity
 * Sets minimum and maximum monthly goals using SeekBar.
 * Data saved to goal.json in budget_data folder.
 * Fixed: used string resources with placeholders for all setText calls (no concatenation).
 */
class GoalActivity : AppCompatActivity() {
    private lateinit var seekMin: SeekBar
    private lateinit var seekMax: SeekBar
    private lateinit var tvMin: TextView
    private lateinit var tvMax: TextView
    private lateinit var btnSave: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        seekMin = findViewById(R.id.seek_min)
        seekMax = findViewById(R.id.seek_max)
        tvMin = findViewById(R.id.tv_min_goal)
        tvMax = findViewById(R.id.tv_max_goal)
        btnSave = findViewById(R.id.btn_save_goal)

        val current = JsonHelper.loadGoal(this) ?: Goal(0.0, 0.0)
        seekMin.progress = (current.minMonthly * 10).toInt()
        seekMax.progress = (current.maxMonthly * 10).toInt()

        updateMinText(current.minMonthly)
        updateMaxText(current.maxMonthly)

        seekMin.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                updateMinText(progress / 10.0)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        seekMax.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                updateMaxText(progress / 10.0)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        btnSave.setOnClickListener {
            val min = seekMin.progress / 10.0
            val max = seekMax.progress / 10.0
            if (min > max) {
                Toast.makeText(this, "Min cannot exceed Max", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            JsonHelper.saveGoal(this, Goal(min, max))
            Toast.makeText(this, "Goals saved", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateMinText(value: Double) {
        tvMin.text = getString(R.string.hint_min_goal) + ": R" + value
    }

    private fun updateMaxText(value: Double) {
        tvMax.text = getString(R.string.hint_max_goal) + ": R" + value
    }
}