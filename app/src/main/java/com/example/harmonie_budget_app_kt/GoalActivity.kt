package com.example.harmonie_budget_app_kt

import android.os.Bundle
import android.widget.Button
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.Goal
import com.example.harmonie_budget_app_kt.utils.JsonHelper

class GoalActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_goal)

        val seekMin: SeekBar = findViewById(R.id.seek_min)
        val seekMax: SeekBar = findViewById(R.id.seek_max)
        val tvMinGoal: TextView = findViewById(R.id.tv_min_goal)
        val tvMaxGoal: TextView = findViewById(R.id.tv_max_goal)
        val btnSaveGoal: Button = findViewById(R.id.btn_save_goal)

        val username = intent.getStringExtra("username") ?: "admin"

        seekMin.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                tvMinGoal.text = getString(R.string.min_goal_text, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        seekMax.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                tvMaxGoal.text = getString(R.string.max_goal_text, progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}
            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })

        btnSaveGoal.setOnClickListener {
            val min = seekMin.progress
            val max = seekMax.progress
            val goal = Goal(min, max)
            JsonHelper.saveGoal(this, username, goal)
            Toast.makeText(this, "Goals saved", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}