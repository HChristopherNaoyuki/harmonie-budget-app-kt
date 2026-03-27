package com.example.harmonie_budget_app_kt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.example.harmonie_budget_app_kt.HomeFragment
import com.example.harmonie_budget_app_kt.BudgetFragment
import com.example.harmonie_budget_app_kt.TransactionsFragment
import com.example.harmonie_budget_app_kt.BudgetsFragment
import com.example.harmonie_budget_app_kt.MoreFragment

class DashboardActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val bottomNav: BottomNavigationView = findViewById(R.id.bottom_nav)

        // Username is passed from MainActivity or RegisterActivity and is required for per-user data isolation as specified in Part 2 of the assignment and the process document
        val username = intent.getStringExtra("username") ?: "admin"

        // Load the default HomeFragment on startup to match the mockup homepage as described in the User Interface.pdf (the sole source for structure, navigation, content, and visual design)
        loadFragment(HomeFragment())

        bottomNav.setOnItemSelectedListener { item ->
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