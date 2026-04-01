package com.example.harmonie_budget_app_kt.models

//
// Goal model for Part 2 of OPSC6311POE.pdf
// Stores the minimum and maximum monthly budget goals (Double type) for the logged-in user.
// This model is required to resolve the minGoal and maxGoal references in GoalActivity.kt.
//
data class Goal(
    val minGoal: Double,
    val maxGoal: Double
)