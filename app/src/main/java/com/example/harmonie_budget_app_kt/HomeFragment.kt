package com.example.harmonie_budget_app_kt

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.harmonie_budget_app_kt.models.Badge
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.models.Goal
import com.example.harmonie_budget_app_kt.models.StreakData
import com.example.harmonie_budget_app_kt.viewmodels.CategoryViewModel
import com.example.harmonie_budget_app_kt.viewmodels.GoalViewModel
import com.example.harmonie_budget_app_kt.viewmodels.GamificationViewModel
import com.example.harmonie_budget_app_kt.viewmodels.HomeViewModel
import com.example.harmonie_budget_app_kt.viewmodels.UserViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

/**
 * HomeFragment displays the user's dashboard with budget information,
 * current spending totals, category breakdown, and gamification elements.
 *
 * Part 3 enhancements:
 * - Visual display showing progress relative to monthly spending goals (progress bar)
 * - Gamification badges display showing earned achievements
 * - Current streak display for consistent expense logging
 * - Budget badge awarded when spending stays within the maximum goal
 */
class HomeFragment : Fragment()
{
    private lateinit var username: String
    private lateinit var tvGreeting: TextView
    private lateinit var tvCurrentDate: TextView
    private lateinit var tvBudgetRange: TextView
    private lateinit var tvTotalBalance: TextView
    private lateinit var tvBudgetStatus: TextView
    private lateinit var rvCategoryBreakdown: RecyclerView
    private lateinit var tvUserId: TextView
    private lateinit var btnCopyUserId: Button

    // Part 3: Gamification UI elements
    private lateinit var tvCurrentStreak: TextView
    private lateinit var tvLongestStreak: TextView
    private lateinit var tvBadgesLabel: TextView
    private lateinit var badgesContainer: LinearLayout
    private lateinit var progressBarSpending: ProgressBar
    private lateinit var tvProgressPercentage: TextView

    private val homeViewModel = HomeViewModel()
    private val goalViewModel = GoalViewModel()
    private val userViewModel = UserViewModel()
    private val categoryViewModel = CategoryViewModel()
    private val gamificationViewModel = GamificationViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = inflater.inflate(R.layout.fragment_home, container, false)

        username = arguments?.getString("username") ?: "admin"

        tvGreeting = view.findViewById(R.id.tv_greeting)
        tvCurrentDate = view.findViewById(R.id.tv_current_date)
        tvBudgetRange = view.findViewById(R.id.tv_budget_range)
        tvTotalBalance = view.findViewById(R.id.tv_total_balance)
        tvBudgetStatus = view.findViewById(R.id.tv_budget_status)
        rvCategoryBreakdown = view.findViewById(R.id.rv_category_breakdown)
        tvUserId = view.findViewById(R.id.tv_user_id)
        btnCopyUserId = view.findViewById(R.id.btn_copy_user_id)

        // Part 3: Initialize gamification UI elements
        tvCurrentStreak = view.findViewById(R.id.tv_current_streak)
        tvLongestStreak = view.findViewById(R.id.tv_longest_streak)
        tvBadgesLabel = view.findViewById(R.id.tv_badges_label)
        badgesContainer = view.findViewById(R.id.badges_container)
        progressBarSpending = view.findViewById(R.id.progress_bar_spending)
        tvProgressPercentage = view.findViewById(R.id.tv_progress_percentage)

        tvGreeting.text = getString(R.string.greetings, username)

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        tvCurrentDate.text = dateFormat.format(calendar.time)

        val user = userViewModel.loadUser(requireContext(), username)
        tvUserId.text = user?.userId ?: username

        btnCopyUserId.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("User ID", tvUserId.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), getString(R.string.user_id_copied), Toast.LENGTH_SHORT).show()
        }

        val goal = goalViewModel.getGoal(requireContext(), username)
        if (goal != null)
        {
            tvBudgetRange.text = getString(R.string.monthly_budget, goal.minGoal, goal.maxGoal)
        }
        else
        {
            tvBudgetRange.text = getString(R.string.set_monthly_goals)
        }

        val currentMonthTotal = homeViewModel.getTotalBalance(requireContext(), username)
        tvTotalBalance.text = getString(R.string.total_balance, currentMonthTotal)

        // Part 3: Update progress bar showing spending relative to max goal
        updateProgressBar(currentMonthTotal, goal)

        val status = determineBudgetStatus(currentMonthTotal, goal)
        tvBudgetStatus.text = status

        // Part 3: Award budget badge if spending is within budget
        if (goal != null && currentMonthTotal <= goal.maxGoal && goal.maxGoal > 0)
        {
            gamificationViewModel.checkAndAwardBudgetBadge(requireContext(), username, currentMonthTotal, goal.maxGoal)
        }

        val expenses = homeViewModel.getAllExpenses(requireContext(), username)

        // Load categories to enable name mapping
        val categories = categoryViewModel.getCategories(requireContext(), username)
        val categoryMap: Map<Int, Category> = categories.associateBy { it.id }

        rvCategoryBreakdown.layoutManager = LinearLayoutManager(requireContext())
        rvCategoryBreakdown.adapter = CategoryBreakdownAdapter(calculateCategoryBreakdown(expenses, categoryMap))

        // Part 3: Load and display gamification data
        loadAndDisplayGamificationData()

        return view
    }

    /**
     * Part 3: Updates the progress bar to visually show spending relative to the monthly max goal.
     *
     * @param spent Amount spent in the current month
     * @param goal User's monthly goal (min and max)
     */
    private fun updateProgressBar(spent: Double, goal: Goal?)
    {
        if (goal != null && goal.maxGoal > 0)
        {
            val percentage = ((spent / goal.maxGoal) * 100).coerceIn(0.0, 100.0)
            progressBarSpending.progress = percentage.toInt()
            tvProgressPercentage.text = String.format(Locale.US, "%.0f%% of monthly budget", percentage)

            // Change progress bar color based on spending level
            val colorRes = when
            {
                percentage >= 100 -> android.R.color.holo_red_dark
                percentage >= 80 -> android.R.color.holo_orange_dark
                else -> android.R.color.holo_green_dark
            }
            progressBarSpending.progressTintList = ContextCompat.getColorStateList(requireContext(), colorRes)
        }
        else
        {
            progressBarSpending.progress = 0
            tvProgressPercentage.text = "Set a monthly budget to see progress"
        }
    }

    /**
     * Part 3: Loads streak data and badges, then updates the UI.
     */
    private fun loadAndDisplayGamificationData()
    {
        // Load and display streak data
        val streakData = gamificationViewModel.getStreakData(requireContext(), username)
        if (streakData != null)
        {
            tvCurrentStreak.text = String.format(Locale.US, "Current Streak: %d days", streakData.currentStreak)
            tvLongestStreak.text = String.format(Locale.US, "Longest Streak: %d days", streakData.longestStreak)
        }
        else
        {
            tvCurrentStreak.text = "Current Streak: 0 days"
            tvLongestStreak.text = "Longest Streak: 0 days"
        }

        // Load and display badges
        val badges = gamificationViewModel.getBadges(requireContext(), username)
        displayBadges(badges)
    }

    /**
     * Part 3: Dynamically creates and adds badge views to the badges container.
     * Each badge is displayed as a colored chip showing the badge name.
     *
     * @param badges List of badges earned by the user
     */
    private fun displayBadges(badges: List<Badge>)
    {
        badgesContainer.removeAllViews()

        if (badges.isEmpty())
        {
            val emptyText = TextView(requireContext())
            emptyText.text = "No badges yet. Add expenses and stay within your budget to earn rewards!"
            emptyText.setTextColor(Color.GRAY)
            emptyText.textSize = 14f
            emptyText.setPadding(8, 8, 8, 8)
            badgesContainer.addView(emptyText)
            tvBadgesLabel.visibility = View.VISIBLE
            return
        }

        tvBadgesLabel.visibility = View.VISIBLE

        for (badge in badges)
        {
            val badgeView = TextView(requireContext())
            badgeView.text = "🏆 ${badge.name}"
            badgeView.setTextColor(Color.WHITE)
            badgeView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.accent_blue))
            badgeView.setPadding(24, 12, 24, 12)
            badgeView.textSize = 12f

            val layoutParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            layoutParams.setMargins(0, 0, 16, 8)
            badgeView.layoutParams = layoutParams

            // Set rounded background using a drawable
            badgeView.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_button)

            badgeView.setOnClickListener {
                Toast.makeText(requireContext(), badge.description, Toast.LENGTH_SHORT).show()
            }

            badgesContainer.addView(badgeView)
        }
    }

    private fun determineBudgetStatus(spent: Double, goal: Goal?): String
    {
        if (goal == null)
        {
            return getString(R.string.set_monthly_goals)
        }
        return when
        {
            spent > goal.maxGoal -> getString(R.string.over_budget)
            spent >= goal.maxGoal * 0.8 -> getString(R.string.near_budget_limit)
            else -> getString(R.string.within_budget)
        }
    }

    /**
     * Calculates category breakdown strings using actual category names from the categories.json file.
     * The categoryMap is passed in to avoid repeated loading.
     */
    private fun calculateCategoryBreakdown(expenses: List<Expense>, categoryMap: Map<Int, Category>): List<String>
    {
        return expenses.groupBy { it.categoryId }
            .map { (categoryId, list) ->
                val categoryName = categoryMap[categoryId]?.name ?: "Category $categoryId"
                getString(R.string.category_total, categoryName, list.sumOf { it.amount })
            }
    }

    companion object
    {
        fun newInstance(username: String): HomeFragment
        {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString("username", username)
            fragment.arguments = args
            return fragment
        }
    }

    private class CategoryBreakdownAdapter(
        private val data: List<String>
    ) : RecyclerView.Adapter<CategoryBreakdownAdapter.ViewHolder>()
    {
        class ViewHolder(val tv: TextView) : RecyclerView.ViewHolder(tv)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
        {
            val tv = TextView(parent.context)
            tv.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            tv.setPadding(16, 8, 16, 8)
            return ViewHolder(tv)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int)
        {
            holder.tv.text = data[position]
        }

        override fun getItemCount(): Int = data.size
    }
}