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

        username = intent.getStringExtra("username") ?: throw IllegalStateException("Username must be passed to DashboardActivity")

        bottomNav = findViewById(R.id.bottom_nav)

        // Only load the initial fragment if this is the first creation of the activity
        // This prevents duplicate fragments on configuration change (rotation)
        if (savedInstanceState == null)
        {
            loadFragment(HomeFragment())
        }

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