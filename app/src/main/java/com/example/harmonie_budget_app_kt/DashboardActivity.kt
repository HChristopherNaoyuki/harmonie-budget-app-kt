package com.example.harmonie_budget_app_kt

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity()
{
    private lateinit var bottomNav: BottomNavigationView
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        username = intent.getStringExtra("username") ?: "admin"

        bottomNav = findViewById(R.id.bottom_nav)

        // Load HomeFragment by default (matches mockup)
        loadFragment(HomeFragment())

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId)
            {
                R.id.nav_home ->
                {
                    loadFragment(HomeFragment())
                    true
                }
                R.id.nav_budget ->
                {
                    loadFragment(BudgetFragment())
                    true
                }
                R.id.nav_transactions ->
                {
                    loadFragment(TransactionsFragment())
                    true
                }
                R.id.nav_budgets ->
                {
                    loadFragment(BudgetsFragment())
                    true
                }
                R.id.nav_more ->
                {
                    loadFragment(MoreFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment)
    {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}