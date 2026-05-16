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
 * Part 3 Enhancements:
 * - Progress bar showing spending relative to monthly max goal
 * - Gamification badges and streaks
 * - Budget badge awarded when spending stays within budget
 * - Default "General" category handling for orphaned expense category IDs
 * All user-facing text uses string resources.
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

    // Gamification UI elements
    private lateinit var tvCurrentStreak: TextView
    private lateinit var tvLongestStreak: TextView
    private lateinit var tvBadgesLabel: TextView
    private lateinit var badgesContainer: LinearLayout
    private lateinit var progressBarSpending: ProgressBar
    private lateinit var tvProgressPercentage: TextView

    // ViewModels
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

        // Initialize existing UI elements
        tvGreeting = view.findViewById(R.id.tv_greeting)
        tvCurrentDate = view.findViewById(R.id.tv_current_date)
        tvBudgetRange = view.findViewById(R.id.tv_budget_range)
        tvTotalBalance = view.findViewById(R.id.tv_total_balance)
        tvBudgetStatus = view.findViewById(R.id.tv_budget_status)
        rvCategoryBreakdown = view.findViewById(R.id.rv_category_breakdown)
        tvUserId = view.findViewById(R.id.tv_user_id)
        btnCopyUserId = view.findViewById(R.id.btn_copy_user_id)

        // Initialize gamification UI elements
        tvCurrentStreak = view.findViewById(R.id.tv_current_streak)
        tvLongestStreak = view.findViewById(R.id.tv_longest_streak)
        tvBadgesLabel = view.findViewById(R.id.tv_badges_label)
        badgesContainer = view.findViewById(R.id.badges_container)
        progressBarSpending = view.findViewById(R.id.progress_bar_spending)
        tvProgressPercentage = view.findViewById(R.id.tv_progress_percentage)

        // Set up greeting and date using string resource
        val greetingText = getString(R.string.greetings, username)
        tvGreeting.text = greetingText

        val calendar = Calendar.getInstance()
        val dateFormat = SimpleDateFormat("EEEE, dd MMMM yyyy", Locale.getDefault())
        tvCurrentDate.text = dateFormat.format(calendar.time)

        // Set up User ID display and copy functionality
        val user = userViewModel.loadUser(requireContext(), username)
        tvUserId.text = user?.userId ?: username

        btnCopyUserId.setOnClickListener {
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("User ID", tvUserId.text.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), getString(R.string.user_id_copied), Toast.LENGTH_SHORT).show()
        }

        // Load and display budget goals
        val goal = goalViewModel.getGoal(requireContext(), username)
        if (goal != null)
        {
            val budgetRangeText = getString(R.string.monthly_budget, goal.minGoal, goal.maxGoal)
            tvBudgetRange.text = budgetRangeText
        }
        else
        {
            tvBudgetRange.text = getString(R.string.set_monthly_goals)
        }

        // Calculate and display total spending
        val currentMonthTotal = homeViewModel.getTotalBalance(requireContext(), username)
        val totalBalanceText = getString(R.string.total_balance, currentMonthTotal)
        tvTotalBalance.text = totalBalanceText

        // Update progress bar
        updateProgressBar(currentMonthTotal, goal)

        // Determine and display budget status text
        val status = determineBudgetStatus(currentMonthTotal, goal)
        tvBudgetStatus.text = status

        // Award budget badge if applicable
        if (goal != null && currentMonthTotal <= goal.maxGoal && goal.maxGoal > 0)
        {
            gamificationViewModel.checkAndAwardBudgetBadge(
                requireContext(),
                username,
                currentMonthTotal,
                goal.maxGoal
            )
        }

        // Load expenses and build category breakdown
        val expenses = homeViewModel.getAllExpenses(requireContext(), username)
        val categories = categoryViewModel.getCategories(requireContext(), username)
        val categoryMap: Map<Int, Category> = categories.associateBy { it.id }

        rvCategoryBreakdown.layoutManager = LinearLayoutManager(requireContext())
        rvCategoryBreakdown.adapter = CategoryBreakdownAdapter(
            calculateCategoryBreakdown(expenses, categoryMap)
        )

        // Load gamification data
        loadAndDisplayGamificationData()

        return view
    }

    /**
     * Updates the progress bar to visually show spending relative to the monthly max goal.
     *
     * @param spent Amount spent in the current month
     * @param goal User's monthly goal (min and max), may be null
     */
    private fun updateProgressBar(spent: Double, goal: Goal?)
    {
        if (goal != null && goal.maxGoal > 0)
        {
            val percentage = ((spent / goal.maxGoal) * 100).coerceIn(0.0, 100.0)
            progressBarSpending.progress = percentage.toInt()

            val progressText = getString(R.string.progress_percentage, percentage.toInt())
            tvProgressPercentage.text = progressText

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
            tvProgressPercentage.text = getString(R.string.set_budget_to_see_progress)
        }
    }

    /**
     * Loads streak data and badges, then updates the UI.
     */
    private fun loadAndDisplayGamificationData()
    {
        // Load and display streak data using string resources
        val streakData = gamificationViewModel.getStreakData(requireContext(), username)
        if (streakData != null)
        {
            val currentStreakText = getString(R.string.current_streak, streakData.currentStreak)
            val longestStreakText = getString(R.string.longest_streak, streakData.longestStreak)
            tvCurrentStreak.text = currentStreakText
            tvLongestStreak.text = longestStreakText
        }
        else
        {
            tvCurrentStreak.text = getString(R.string.current_streak, 0)
            tvLongestStreak.text = getString(R.string.longest_streak, 0)
        }

        // Load and display badges
        val badges = gamificationViewModel.getBadges(requireContext(), username)
        displayBadges(badges)
    }

    /**
     * Dynamically creates and adds badge views to the badges container.
     *
     * @param badges List of badges earned by the user
     */
    private fun displayBadges(badges: List<Badge>)
    {
        badgesContainer.removeAllViews()

        if (badges.isEmpty())
        {
            val emptyText = TextView(requireContext())
            emptyText.text = getString(R.string.no_badges_message)
            emptyText.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.darker_gray))
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
            val badgeDisplayText = getString(R.string.badge_display_format, badge.name)
            badgeView.text = badgeDisplayText
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

            badgeView.background = ContextCompat.getDrawable(requireContext(), R.drawable.rounded_button)

            badgeView.setOnClickListener {
                Toast.makeText(requireContext(), badge.description, Toast.LENGTH_SHORT).show()
            }

            badgesContainer.addView(badgeView)
        }
    }

    /**
     * Determines the budget status text based on current spending and goals.
     *
     * @param spent Amount spent in the current month
     * @param goal User's monthly goal, may be null
     * @return Status string resource
     */
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
     * Calculates category breakdown strings using actual category names.
     * If an expense references a category that no longer exists (orphaned categoryId),
     * the expense is classified under the default "General" category. This ensures
     * that all expenses are displayed even if the original category was deleted.
     *
     * @param expenses List of expenses for the user
     * @param categoryMap Map of category ID to Category object
     * @return List of formatted strings for display
     */
    private fun calculateCategoryBreakdown(
        expenses: List<Expense>,
        categoryMap: Map<Int, Category>
    ): List<String>
    {
        return expenses.groupBy { it.categoryId }
            .map { (categoryId, list) ->
                // If categoryId is not found in the map, default to "General"
                val categoryName = categoryMap[categoryId]?.name ?: "General"
                getString(R.string.category_total, categoryName, list.sumOf { it.amount })
            }
    }

    companion object
    {
        /**
         * Factory method to create a new instance of HomeFragment with the specified username.
         *
         * @param username The logged-in user's username
         * @return A configured HomeFragment instance
         */
        fun newInstance(username: String): HomeFragment
        {
            val fragment = HomeFragment()
            val args = Bundle()
            args.putString("username", username)
            fragment.arguments = args
            return fragment
        }
    }

    /**
     * RecyclerView adapter for the category breakdown list.
     */
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