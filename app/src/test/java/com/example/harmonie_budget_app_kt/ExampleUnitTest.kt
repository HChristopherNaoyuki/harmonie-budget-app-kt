package com.example.harmonie_budget_app_kt

import org.junit.Test
import org.junit.Assert.*
import com.example.harmonie_budget_app_kt.models.Badge
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.models.Goal
import com.example.harmonie_budget_app_kt.models.StreakData
import com.example.harmonie_budget_app_kt.models.User
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * Local unit tests for pure logic and utility functions.
 * These tests execute on the development machine (host) and require no Android Context.
 * All tests are written in Allman style with detailed professional comments.
 *
 * Coverage includes:
 * - User ID generation logic
 * - Password validation regex
 * - Expense ID incrementation
 * - Goal validation logic
 * - Category model behavior
 * - Expense model data integrity
 * - User model construction
 * - Badge model tests (Part 3 gamification)
 * - StreakData model tests (Part 3 gamification)
 * - Streak calculation logic
 * - Percentage calculation for progress bar
 * - Budget badge logic
 * - Time validation logic (new)
 */
class ExampleUnitTest
{
    // ==================== Helper Functions for Testing ====================

    private fun isValidGoal(minGoal: Double, maxGoal: Double): Boolean
    {
        return (minGoal > 0.0 && maxGoal > minGoal && maxGoal <= 1000000.0)
    }

    private fun calculateNewStreak(currentStreak: Int, daysDifference: Int): Int
    {
        return when (daysDifference)
        {
            1 -> currentStreak + 1
            0 -> currentStreak
            else -> 1
        }
    }

    private fun calculateProgressPercentage(spent: Double, maxGoal: Double): Int
    {
        if (maxGoal <= 0)
        {
            return 0
        }
        val percentage = (spent / maxGoal) * 100.0
        return percentage.toInt().coerceIn(0, 100)
    }

    private fun shouldAwardBudgetBadge(totalSpent: Double, maxGoal: Double): Boolean
    {
        return (totalSpent <= maxGoal && maxGoal > 0)
    }

    /**
     * Part 3 Enhancement: Time validation logic test helper.
     * Validates that end time is not earlier than start time.
     * For times crossing midnight, the end time is considered earlier
     * because 00:00 (0 minutes) is less than 23:59 (1439 minutes).
     *
     * @param startTime The start time string in HH:mm format
     * @param endTime The end time string in HH:mm format
     * @return True if end time is not before start time, false otherwise
     */
    private fun isTimeValid(startTime: String, endTime: String): Boolean
    {
        if (startTime.isEmpty() || endTime.isEmpty())
        {
            return true
        }

        val startParts = startTime.split(":")
        val endParts = endTime.split(":")

        if (startParts.size != 2 || endParts.size != 2)
        {
            return false
        }

        val startHour = startParts[0].toIntOrNull() ?: return false
        val startMinute = startParts[1].toIntOrNull() ?: return false
        val endHour = endParts[0].toIntOrNull() ?: return false
        val endMinute = endParts[1].toIntOrNull() ?: return false

        val startTotalMinutes = startHour * 60 + startMinute
        val endTotalMinutes = endHour * 60 + endMinute

        // End time must not be earlier than start time (can be equal)
        // Cross-midnight times (e.g., 23:59 to 00:00) are invalid because
        // 00:00 (0 minutes) is earlier than 23:59 (1439 minutes)
        return endTotalMinutes >= startTotalMinutes
    }

    // ==================== User ID Generation Tests ====================

    @Test
    fun generateUserId_producesCorrectFormat()
    {
        val username = "abc"
        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val datePart = String.format(Locale.US, "%04d%02d%02d", year, month, day)
        val prefix = username.take(4).uppercase(Locale.US).padEnd(4, 'X')
        val counter = String.format(Locale.US, "%04d", System.currentTimeMillis() % 10000)
        val result = prefix + datePart + counter

        assertEquals("User ID must be exactly 16 characters", 16, result.length)
        assertTrue("Prefix must be uppercase and padded", result.startsWith("ABCX"))
        assertTrue("Date part must be 8 digits", result.substring(4, 12).all { it.isDigit() })
        assertTrue("Counter must be 4 digits", result.substring(12, 16).all { it.isDigit() })
    }

    @Test
    fun generateUserId_truncatesLongUsername()
    {
        val username = "abcdefgh"
        val prefix = username.take(4).uppercase(Locale.US).padEnd(4, 'X')

        assertEquals("Prefix should be exactly 4 characters", 4, prefix.length)
        assertEquals("Prefix should use first 4 characters uppercased", "ABCD", prefix)
    }

    @Test
    fun generateUserId_padsShortUsername()
    {
        val username = "ab"
        val prefix = username.take(4).uppercase(Locale.US).padEnd(4, 'X')

        assertEquals("Prefix should be padded to 4 characters", 4, prefix.length)
        assertEquals("Prefix should be ABXX", "ABXX", prefix)
    }

    // ==================== Password Validation Tests ====================

    @Test
    fun passwordRegex_acceptsValidPasswords()
    {
        val regex = Regex("""^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$""")

        assertTrue(regex.matches("Password1!"))
        assertTrue(regex.matches("Test123@"))
        assertTrue(regex.matches("MyP@ssw0rd"))
        assertTrue(regex.matches("C0mplex!"))
    }

    @Test
    fun passwordRegex_rejectsInvalidPasswords()
    {
        val regex = Regex("""^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$""")

        assertFalse("Too short", regex.matches("short1!"))
        assertFalse("No special character", regex.matches("password123"))
        assertFalse("No number", regex.matches("Password!"))
        assertFalse("No letter", regex.matches("12345678!"))
        assertFalse("Exactly 7 characters", regex.matches("Short1!"))
    }

    // ==================== Expense ID Generation Tests ====================

    @Test
    fun expenseIdGeneration_incrementsCorrectly()
    {
        val expenses = mutableListOf<Expense>()

        val maxId1 = expenses.maxOfOrNull { it.id } ?: 0
        val newId1 = maxId1 + 1
        assertEquals("First expense ID should be 1", 1, newId1)

        expenses.add(Expense(1, 10.0, "2026-04-19", "09:00", "10:00", "Test", 1))

        val maxId2 = expenses.maxOfOrNull { it.id } ?: 0
        val newId2 = maxId2 + 1
        assertEquals("Second expense ID should be 2", 2, newId2)

        expenses.add(Expense(2, 20.0, "2026-04-20", "11:00", "12:00", "Test2", 2))

        val maxId3 = expenses.maxOfOrNull { it.id } ?: 0
        val newId3 = maxId3 + 1
        assertEquals("Third expense ID should be 3", 3, newId3)
    }

    @Test
    fun expenseIdGeneration_handlesGaps()
    {
        val expenses = mutableListOf(
            Expense(5, 10.0, "2026-04-19", "09:00", "10:00", "Test", 1),
            Expense(3, 20.0, "2026-04-20", "11:00", "12:00", "Test2", 2)
        )

        val maxId = expenses.maxOfOrNull { it.id } ?: 0
        val newId = maxId + 1

        assertEquals("Next ID should be 6, not 3", 6, newId)
    }

    // ==================== Goal Validation Tests ====================

    @Test
    fun goalValidation_acceptsValidGoals()
    {
        assertTrue("Valid goals should be accepted", isValidGoal(100.0, 500.0))
        assertTrue("Boundary min goal", isValidGoal(0.01, 100.0))
        assertTrue("Boundary max goal", isValidGoal(1.0, 1000000.0))
        assertTrue("Large valid range", isValidGoal(1000.0, 500000.0))
    }

    @Test
    fun goalValidation_rejectsEqualGoals()
    {
        assertFalse("Equal goals should be rejected", isValidGoal(500.0, 500.0))
        assertFalse("Both zero", isValidGoal(0.0, 0.0))
    }

    @Test
    fun goalValidation_rejectsMinGreaterThanMax()
    {
        assertFalse("Min greater than max should be rejected", isValidGoal(600.0, 500.0))
        assertFalse("Min much larger than max", isValidGoal(10000.0, 100.0))
    }

    @Test
    fun goalValidation_rejectsNegativeMinGoal()
    {
        assertFalse("Negative minimum goal should be rejected", isValidGoal(-10.0, 100.0))
        assertFalse("Both negative", isValidGoal(-100.0, -50.0))
    }

    @Test
    fun goalValidation_rejectsNegativeMaxGoal()
    {
        assertFalse("Negative maximum goal should be rejected", isValidGoal(10.0, -100.0))
    }

    @Test
    fun goalValidation_rejectsExcessiveMaxGoal()
    {
        assertFalse("Max goal exceeding 1,000,000 should be rejected", isValidGoal(100.0, 1000001.0))
        assertFalse("Max goal far exceeding limit", isValidGoal(1.0, 2000000.0))
    }

    @Test
    fun goalValidation_rejectsZeroMinGoal()
    {
        assertFalse("Zero minimum goal should be rejected", isValidGoal(0.0, 100.0))
    }

    // ==================== Category Model Tests ====================

    @Test
    fun categoryModel_storesPropertiesCorrectly()
    {
        val category = Category(1, "Groceries")

        assertEquals("Category ID should match", 1, category.id)
        assertEquals("Category name should match", "Groceries", category.name)
    }

    @Test
    fun categoryModel_acceptsEmptyName()
    {
        val category = Category(2, "")

        assertEquals("Empty name should be stored", "", category.name)
    }

    @Test
    fun categoryModel_defaultGeneralCategory()
    {
        val generalCategory = Category(1, "General")

        assertEquals("General category ID should be 1", 1, generalCategory.id)
        assertEquals("General category name should be General", "General", generalCategory.name)
    }

    // ==================== Expense Model Tests ====================

    @Test
    fun expenseModel_storesAllPropertiesCorrectly()
    {
        val expense = Expense(
            id = 1,
            amount = 25.50,
            date = "2026-04-22",
            startTime = "09:00",
            endTime = "10:00",
            description = "Weekly groceries",
            categoryId = 3,
            photoUri = "content://media/123"
        )

        assertEquals("ID should match", 1, expense.id)
        assertEquals("Amount should match", 25.50, expense.amount, 0.001)
        assertEquals("Date should match", "2026-04-22", expense.date)
        assertEquals("Start time should match", "09:00", expense.startTime)
        assertEquals("End time should match", "10:00", expense.endTime)
        assertEquals("Description should match", "Weekly groceries", expense.description)
        assertEquals("Category ID should match", 3, expense.categoryId)
        assertEquals("Photo URI should match", "content://media/123", expense.photoUri)
    }

    @Test
    fun expenseModel_nullPhotoUri()
    {
        val expense = Expense(
            id = 2,
            amount = 15.00,
            date = "2026-04-22",
            startTime = "14:00",
            endTime = "15:00",
            description = "Transport",
            categoryId = 1
        )

        assertNull("Photo URI should be null when not provided", expense.photoUri)
    }

    @Test
    fun expenseModel_withGeneralCategory()
    {
        val expense = Expense(
            id = 3,
            amount = 50.00,
            date = "2026-05-16",
            startTime = "08:00",
            endTime = "09:00",
            description = "Uncategorized purchase",
            categoryId = 1
        )

        assertEquals("Category ID should default to General category ID", 1, expense.categoryId)
    }

    // ==================== User Model Tests ====================

    @Test
    fun userModel_storesAllPropertiesCorrectly()
    {
        val user = User(
            name = "John Doe",
            surname = "",
            username = "johndoe",
            password = "SecurePass1!",
            userId = "JOHN202604220001"
        )

        assertEquals("Name should match", "John Doe", user.name)
        assertEquals("Surname should match", "", user.surname)
        assertEquals("Username should match", "johndoe", user.username)
        assertEquals("Password should match", "SecurePass1!", user.password)
        assertEquals("User ID should match", "JOHN202604220001", user.userId)
    }

    @Test
    fun userModel_requiresFullName()
    {
        val validUser = User("John Doe", "", "johndoe", "Pass123!", "ID123")
        val nameParts = validUser.name.split(" ").filter { it.isNotEmpty() }

        assertTrue("Valid full name should have at least two parts", nameParts.size >= 2)
    }

    @Test
    fun userModel_rejectsSingleName()
    {
        val singleName = "John"
        val nameParts = singleName.split(" ").filter { it.isNotEmpty() }

        assertFalse("Single name should not be considered a full name", nameParts.size >= 2)
    }

    // ==================== Goal Model Tests ====================

    @Test
    fun goalModel_storesPropertiesCorrectly()
    {
        val goal = Goal(200.0, 800.0)

        assertEquals("Min goal should match", 200.0, goal.minGoal, 0.001)
        assertEquals("Max goal should match", 800.0, goal.maxGoal, 0.001)
    }

    @Test
    fun goalModel_acceptsZeroValues()
    {
        val goal = Goal(0.0, 0.0)

        assertEquals("Min goal should be zero", 0.0, goal.minGoal, 0.001)
        assertEquals("Max goal should be zero", 0.0, goal.maxGoal, 0.001)
    }

    // ==================== Part 3: Badge Model Tests ====================

    @Test
    fun badgeModel_storesPropertiesCorrectly()
    {
        val earnedDate = "2026-05-05"
        val badge = Badge(
            id = 1,
            name = "Consistent Starter",
            description = "Logged expenses for 3 days in a row",
            earnedDate = earnedDate,
            iconResource = 0
        )

        assertEquals("Badge ID should match", 1, badge.id)
        assertEquals("Badge name should match", "Consistent Starter", badge.name)
        assertEquals("Badge description should match", "Logged expenses for 3 days in a row", badge.description)
        assertEquals("Earned date should match", earnedDate, badge.earnedDate)
        assertEquals("Icon resource should default to 0", 0, badge.iconResource)
    }

    @Test
    fun badgeModel_acceptsCustomIconResource()
    {
        val badge = Badge(
            id = 5,
            name = "Budget Champion",
            description = "Stayed within monthly budget",
            earnedDate = "2026-05-05",
            iconResource = 12345
        )

        assertEquals("Custom icon resource should be stored", 12345, badge.iconResource)
    }

    @Test
    fun badgeModel_implementsEqualityCorrectly()
    {
        val badge1 = Badge(1, "Test Badge", "Test Description", "2026-05-05", 0)
        val badge2 = Badge(1, "Test Badge", "Test Description", "2026-05-05", 0)
        val badge3 = Badge(2, "Different Badge", "Different Description", "2026-05-05", 0)

        assertEquals("Identical badges should be equal", badge1, badge2)
        assertNotEquals("Different badge IDs should not be equal", badge1, badge3)
    }

    // ==================== Part 3: StreakData Model Tests ====================

    @Test
    fun streakDataModel_storesPropertiesCorrectly()
    {
        val streakData = StreakData(
            currentStreak = 7,
            longestStreak = 14,
            lastExpenseDate = "2026-05-05"
        )

        assertEquals("Current streak should match", 7, streakData.currentStreak)
        assertEquals("Longest streak should match", 14, streakData.longestStreak)
        assertEquals("Last expense date should match", "2026-05-05", streakData.lastExpenseDate)
    }

    @Test
    fun streakDataModel_acceptsZeroValues()
    {
        val streakData = StreakData(
            currentStreak = 0,
            longestStreak = 0,
            lastExpenseDate = ""
        )

        assertEquals("Current streak should be zero", 0, streakData.currentStreak)
        assertEquals("Longest streak should be zero", 0, streakData.longestStreak)
        assertEquals("Last expense date should be empty", "", streakData.lastExpenseDate)
    }

    @Test
    fun streakDataModel_singleDayStreak()
    {
        val streakData = StreakData(
            currentStreak = 1,
            longestStreak = 1,
            lastExpenseDate = "2026-05-05"
        )

        assertEquals("First day streak should be 1", 1, streakData.currentStreak)
        assertEquals("Longest streak should also be 1", 1, streakData.longestStreak)
    }

    // ==================== Part 3: Streak Calculation Logic Tests ====================

    @Test
    fun streakCalculation_incrementsOnConsecutiveDays()
    {
        val result = calculateNewStreak(5, 1)
        assertEquals("Streak should increment by 1", 6, result)
    }

    @Test
    fun streakCalculation_remainsUnchangedOnSameDay()
    {
        val result = calculateNewStreak(5, 0)
        assertEquals("Streak should remain unchanged", 5, result)
    }

    @Test
    fun streakCalculation_resetsOnGap()
    {
        val result = calculateNewStreak(5, 2)
        assertEquals("Streak should reset to 1", 1, result)
    }

    @Test
    fun streakCalculation_resetsOnLargeGap()
    {
        val result = calculateNewStreak(10, 7)
        assertEquals("Streak should reset to 1 for any gap greater than 1", 1, result)
    }

    @Test
    fun streakCalculation_startsAtOneForFirstExpense()
    {
        val result = calculateNewStreak(0, 1)
        assertEquals("First expense should set streak to 1", 1, result)
    }

    @Test
    fun streakCalculation_updatesLongestStreak()
    {
        val currentStreak = 15
        val longestStreak = 10
        val newLongestStreak = maxOf(currentStreak, longestStreak)

        assertEquals("Longest streak should update to larger value", 15, newLongestStreak)
    }

    @Test
    fun streakCalculation_preservesLongestStreak()
    {
        val currentStreak = 5
        val longestStreak = 10
        val newLongestStreak = maxOf(currentStreak, longestStreak)

        assertEquals("Longest streak should remain at previous record", 10, newLongestStreak)
    }

    // ==================== Percentage Calculation Tests ====================

    @Test
    fun percentageCalculation_isAccurate()
    {
        val part = 25.0
        val whole = 100.0
        val percentage = (part / whole) * 100.0

        assertEquals("Percentage should be 25.0", 25.0, percentage, 0.001)
    }

    @Test
    fun progressPercentage_calculatesCorrectly()
    {
        val percentage50 = calculateProgressPercentage(250.0, 500.0)
        assertEquals("50 percent spending should return 50", 50, percentage50)

        val percentage100 = calculateProgressPercentage(500.0, 500.0)
        assertEquals("100 percent spending should return 100", 100, percentage100)

        val percentage25 = calculateProgressPercentage(125.0, 500.0)
        assertEquals("25 percent spending should return 25", 25, percentage25)
    }

    @Test
    fun progressPercentage_capsAt100Percent()
    {
        val percentage = calculateProgressPercentage(750.0, 500.0)
        assertEquals("Percentage should be capped at 100 for overspending", 100, percentage)

        val percentageExtreme = calculateProgressPercentage(2000.0, 500.0)
        assertEquals("Extreme overspending should also be capped at 100", 100, percentageExtreme)
    }

    @Test
    fun progressPercentage_handlesInvalidMaxGoal()
    {
        val percentageZeroGoal = calculateProgressPercentage(100.0, 0.0)
        assertEquals("Zero max goal should return 0", 0, percentageZeroGoal)

        val percentageNegativeGoal = calculateProgressPercentage(100.0, -50.0)
        assertEquals("Negative max goal should return 0", 0, percentageNegativeGoal)
    }

    @Test
    fun progressPercentage_zeroSpent()
    {
        val percentage = calculateProgressPercentage(0.0, 500.0)
        assertEquals("Zero spent should return 0", 0, percentage)
    }

    // ==================== Part 3: Time Validation Tests ====================

    @Test
    fun timeValidation_acceptsValidTimes()
    {
        // Equal times should be valid
        assertTrue("Equal times should be valid", isTimeValid("09:00", "09:00"))

        // End time after start time should be valid
        assertTrue("End time after start time should be valid", isTimeValid("09:00", "10:00"))

        // Empty times should be valid (handled by required field validation)
        assertTrue("Empty start time should be valid", isTimeValid("", "10:00"))
        assertTrue("Empty end time should be valid", isTimeValid("09:00", ""))
        assertTrue("Both empty should be valid", isTimeValid("", ""))
    }

    @Test
    fun timeValidation_rejectsInvalidTimes()
    {
        // End time before start time should be invalid
        assertFalse("End time before start time should be invalid", isTimeValid("10:00", "09:00"))

        // End time earlier hour should be invalid
        assertFalse("End time earlier hour should be invalid", isTimeValid("09:30", "08:30"))

        // Invalid format should be invalid
        assertFalse("Invalid format should be invalid", isTimeValid("09:00", "invalid"))
        assertFalse("Invalid format should be invalid", isTimeValid("invalid", "10:00"))
    }

    @Test
    fun timeValidation_handlesBoundaryCases()
    {
        // Cross-midnight (23:59 to 00:00) is invalid because 00:00 is earlier than 23:59
        // when compared as minutes since midnight (0 minutes vs 1439 minutes)
        assertFalse("Cross-midnight (23:59 to 00:00) should be invalid",
            isTimeValid("23:59", "00:00"))

        // Start time at midnight to early morning is valid
        assertTrue("Start time at midnight to early morning is valid",
            isTimeValid("00:00", "00:01"))

        // Start time to same time on next day is invalid (not supported without date change)
        // This test verifies the function does not incorrectly accept cross-midnight ranges
        assertFalse("Start time before end time crossing midnight should be invalid",
            isTimeValid("23:00", "01:00"))
    }

    // ==================== Part 3: Badge Milestone Tests ====================

    @Test
    fun streakMilestone_detectsCorrectBadge()
    {
        val milestones = listOf(3, 7, 14, 30)

        assertEquals("3 day streak should be milestone 0", 3, milestones[0])
        assertEquals("7 day streak should be milestone 1", 7, milestones[1])
        assertEquals("14 day streak should be milestone 2", 14, milestones[2])
        assertEquals("30 day streak should be milestone 3", 30, milestones[3])
    }

    @Test
    fun streakMilestone_awardsAllBadgesAt30Days()
    {
        val currentStreak = 30
        val milestones = listOf(3, 7, 14, 30)
        val earnedBadges = milestones.filter { currentStreak >= it }

        assertEquals("30 day streak should earn 4 badges", 4, earnedBadges.size)
        assertTrue("Should include 3 day badge", earnedBadges.contains(3))
        assertTrue("Should include 7 day badge", earnedBadges.contains(7))
        assertTrue("Should include 14 day badge", earnedBadges.contains(14))
        assertTrue("Should include 30 day badge", earnedBadges.contains(30))
    }

    @Test
    fun streakMilestone_awardsIntermediateBadges()
    {
        val currentStreak = 10
        val milestones = listOf(3, 7, 14, 30)
        val earnedBadges = milestones.filter { currentStreak >= it }

        assertEquals("10 day streak should earn 2 badges", 2, earnedBadges.size)
        assertTrue("Should include 3 day badge", earnedBadges.contains(3))
        assertTrue("Should include 7 day badge", earnedBadges.contains(7))
        assertFalse("Should not include 14 day badge", earnedBadges.contains(14))
        assertFalse("Should not include 30 day badge", earnedBadges.contains(30))
    }

    // ==================== Part 3: Budget Badge Logic Tests ====================

    @Test
    fun budgetBadge_awardedWhenWithinBudget()
    {
        assertTrue("Spending exactly at max goal should award badge", shouldAwardBudgetBadge(500.0, 500.0))
        assertTrue("Spending below max goal should award badge", shouldAwardBudgetBadge(400.0, 500.0))
        assertTrue("Spending well below max goal should award badge", shouldAwardBudgetBadge(100.0, 500.0))
    }

    @Test
    fun budgetBadge_notAwardedWhenOverBudget()
    {
        assertFalse("Spending slightly above max goal should not award badge", shouldAwardBudgetBadge(501.0, 500.0))
        assertFalse("Spending significantly above max goal should not award badge", shouldAwardBudgetBadge(1000.0, 500.0))
    }

    @Test
    fun budgetBadge_notAwardedForInvalidMaxGoal()
    {
        assertFalse("Zero max goal should not award badge", shouldAwardBudgetBadge(0.0, 0.0))
        assertFalse("Negative max goal should not award badge", shouldAwardBudgetBadge(100.0, -50.0))
    }

    @Test
    fun budgetBadge_awardedForZeroSpending()
    {
        assertTrue("Zero spending within valid budget should award badge", shouldAwardBudgetBadge(0.0, 500.0))
    }
}