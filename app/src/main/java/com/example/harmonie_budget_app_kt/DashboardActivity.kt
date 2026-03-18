// app/kotlin+java/com.example.harmonie_budget_app_kt/DashboardActivity.kt
package com.example.harmonie_budget_app_kt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * DashboardActivity
 * Main app screen with bottom navigation bar matching the provided GUI image.
 * Tabs: Home, Budget, Transactions, Budgets, More.
 * Each tab loads a fragment with corresponding content.
 * Clean, minimal design with light background and rounded elements.
 */
class DashboardActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        bottomNavigationView = findViewById(R.id.bottom_navigation)

        // Load default fragment on start
        loadFragment(HomeFragment())

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> loadFragment(HomeFragment())
                R.id.nav_budget -> loadFragment(BudgetFragment())
                R.id.nav_transactions -> loadFragment(TransactionsFragment())
                R.id.nav_budgets -> loadFragment(BudgetsFragment())
                R.id.nav_more -> loadFragment(MoreFragment())
            }
            true
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}