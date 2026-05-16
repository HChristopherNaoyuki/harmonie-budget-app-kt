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

        username = intent.getStringExtra("username")
            ?: throw IllegalStateException("Username must be passed to DashboardActivity")

        bottomNav = findViewById(R.id.bottom_nav)

        val selectedTabId = intent.getIntExtra("selected_tab", R.id.nav_home)

        if (savedInstanceState == null)
        {
            when (selectedTabId)
            {
                R.id.nav_home -> loadFragment(createHomeFragment())
                R.id.nav_budget -> loadFragment(createBudgetFragment())
                R.id.nav_transactions -> loadFragment(createTransactionsFragment())
                R.id.nav_budgets -> loadFragment(createBudgetsFragment())
                R.id.nav_more -> loadFragment(createMoreFragment())
                else -> loadFragment(createHomeFragment())
            }
            bottomNav.selectedItemId = selectedTabId
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId)
            {
                R.id.nav_home ->
                {
                    loadFragment(createHomeFragment())
                    true
                }
                R.id.nav_budget ->
                {
                    loadFragment(createBudgetFragment())
                    true
                }
                R.id.nav_transactions ->
                {
                    loadFragment(createTransactionsFragment())
                    true
                }
                R.id.nav_budgets ->
                {
                    loadFragment(createBudgetsFragment())
                    true
                }
                R.id.nav_more ->
                {
                    loadFragment(createMoreFragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun createHomeFragment(): Fragment
    {
        val fragment = HomeFragment()
        val args = Bundle()
        args.putString("username", username)
        fragment.arguments = args
        return fragment
    }

    private fun createBudgetFragment(): Fragment
    {
        val fragment = BudgetFragment()
        val args = Bundle()
        args.putString("username", username)
        fragment.arguments = args
        return fragment
    }

    private fun createTransactionsFragment(): Fragment
    {
        val fragment = TransactionsFragment()
        val args = Bundle()
        args.putString("username", username)
        fragment.arguments = args
        return fragment
    }

    private fun createBudgetsFragment(): Fragment
    {
        val fragment = BudgetsFragment()
        val args = Bundle()
        args.putString("username", username)
        fragment.arguments = args
        return fragment
    }

    private fun createMoreFragment(): Fragment
    {
        val fragment = MoreFragment()
        val args = Bundle()
        args.putString("username", username)
        fragment.arguments = args
        return fragment
    }

    private fun loadFragment(fragment: Fragment)
    {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}