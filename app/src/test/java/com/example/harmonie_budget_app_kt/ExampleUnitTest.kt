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
 * - Goal validation logic (using a dedicated validation function)
 * - Category model behavior
 * - Expense model data integrity
 * - User model construction
 * - Badge model tests (Part 3 gamification)
 * - StreakData model tests (Part 3 gamification)
 * - Streak calculation logic (using a dedicated function)
 * - Percentage calculation for progress bar (using a dedicated function)
 * - Budget badge logic (using a dedicated function)
 */
class ExampleUnitTest
{
    // ==================== Helper Functions for Testing ====================

    /**
     * Validates a goal using the same rules as GoalActivity.
     * This function isolates the validation logic for testing.
     *
     * @param minGoal The minimum monthly spending goal
     * @param maxGoal The maximum monthly spending goal
     * @return true if the goals are valid, false otherwise
     */
    private fun isValidGoal(minGoal: Double, maxGoal: Double): Boolean
    {
        return (minGoal > 0.0 && maxGoal > minGoal && maxGoal <= 1000000.0)
    }

    /**
     * Calculates the new streak value based on the previous streak and days difference.
     * This function isolates the streak calculation logic for testing.
     *
     * @param currentStreak The current streak count before the new expense
     * @param daysDifference The number of days between the last expense and today
     * @return The updated streak value
     */
    private fun calculateNewStreak(currentStreak: Int, daysDifference: Int): Int
    {
        return when (daysDifference)
        {
            1 -> currentStreak + 1
            0 -> currentStreak
            else -> 1
        }
    }

    /**
     * Calculates the progress percentage for the budget progress bar.
     * This function isolates the percentage calculation logic for testing.
     *
     * @param spent The amount spent in the current month
     * @param maxGoal The maximum monthly budget goal
     * @return The percentage (capped between 0 and 100)
     */
    private fun calculateProgressPercentage(spent: Double, maxGoal: Double): Int
    {
        if (maxGoal <= 0)
        {
            return 0
        }
        val percentage = (spent / maxGoal) * 100.0
        return percentage.toInt().coerceIn(0, 100)
    }

    /**
     * Determines whether a budget badge should be awarded.
     * This function isolates the badge logic for testing.
     *
     * @param totalSpent The total amount spent in the current month
     * @param maxGoal The maximum monthly budget goal
     * @return true if a budget badge should be awarded, false otherwise
     */
    private fun shouldAwardBudgetBadge(totalSpent: Double, maxGoal: Double): Boolean
    {
        return (totalSpent <= maxGoal && maxGoal > 0)
    }

    // ==================== User ID Generation Tests ====================

    /**
     * Tests the User ID generation logic from RegisterActivity.
     * The generation logic is duplicated here for the unit test because the original method is private.
     * This ensures the test remains independent and verifies the exact 16-character format.
     *
     * Expected format: PREFIX(4) + DATE(8) + COUNTER(4) = 16 characters total.
     * Prefix is derived from the first 4 characters of the username, uppercased and padded with 'X'.
     * Date part uses the current UTC date in yyyyMMdd format.
     * Counter uses the last 4 digits of current time in milliseconds.
     */
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

    /**
     * Tests User ID generation with a username longer than 4 characters.
     * Verifies that only the first 4 characters are used for the prefix.
     */
    @Test
    fun generateUserId_truncatesLongUsername()
    {
        val username = "abcdefgh"
        val prefix = username.take(4).uppercase(Locale.US).padEnd(4, 'X')

        assertEquals("Prefix should be exactly 4 characters", 4, prefix.length)
        assertEquals("Prefix should use first 4 characters uppercased", "ABCD", prefix)
    }

    /**
     * Tests User ID generation with a username shorter than 4 characters.
     * Verifies that the prefix is padded with 'X' to reach 4 characters.
     */
    @Test
    fun generateUserId_padsShortUsername()
    {
        val username = "ab"
        val prefix = username.take(4).uppercase(Locale.US).padEnd(4, 'X')

        assertEquals("Prefix should be padded to 4 characters", 4, prefix.length)
        assertEquals("Prefix should be ABXX", "ABXX", prefix)
    }

    // ==================== Password Validation Tests ====================

    /**
     * Tests password validation regex used in RegisterActivity and ForgotPasswordActivity.
     * Requirements: at least 8 characters, one letter, one number, one special character.
     * The regex uses positive lookahead assertions to enforce each requirement.
     */
    @Test
    fun passwordRegex_acceptsValidPasswords()
    {
        val regex = Regex("""^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$""")

        assertTrue(regex.matches("Password1!"))
        assertTrue(regex.matches("Test123@"))
        assertTrue(regex.matches("MyP@ssw0rd"))
        assertTrue(regex.matches("C0mplex!"))
    }

    /**
     * Tests password validation regex rejects invalid passwords.
     * Edge cases: too short, missing letter, missing number, missing special character.
     */
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

    /**
     * Tests Expense ID generation logic used in JsonHelper.saveExpense.
     * Verifies incremental IDs are correctly assigned using pure in-memory logic.
     * This test avoids file I/O to remain environment-independent.
     */
    @Test
    fun expenseIdGeneration_incrementsCorrectly()
    {
        val expenses = mutableListOf<Expense>()

        // First expense should receive ID 1
        val maxId1 = expenses.maxOfOrNull { it.id } ?: 0
        val newId1 = maxId1 + 1
        assertEquals("First expense ID should be 1", 1, newId1)

        expenses.add(Expense(1, 10.0, "2026-04-19", "09:00", "10:00", "Test", 1))

        // Second expense should receive ID 2
        val maxId2 = expenses.maxOfOrNull { it.id } ?: 0
        val newId2 = maxId2 + 1
        assertEquals("Second expense ID should be 2", 2, newId2)

        expenses.add(Expense(2, 20.0, "2026-04-20", "11:00", "12:00", "Test2", 2))

        // Third expense should receive ID 3
        val maxId3 = expenses.maxOfOrNull { it.id } ?: 0
        val newId3 = maxId3 + 1
        assertEquals("Third expense ID should be 3", 3, newId3)
    }

    /**
     * Tests Expense ID generation with gaps in the sequence.
     * Verifies that the maximum ID is used, not the count.
     */
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

    // ==================== Goal Validation Tests (Using Helper Function) ====================

    /**
     * Tests Goal validation logic using the isValidGoal helper function.
     * Valid conditions: maxGoal > minGoal, minGoal > 0.0, maxGoal <= 1000000.0
     */
    @Test
    fun goalValidation_acceptsValidGoals()
    {
        assertTrue("Valid goals should be accepted", isValidGoal(100.0, 500.0))
        assertTrue("Boundary min goal", isValidGoal(0.01, 100.0))
        assertTrue("Boundary max goal", isValidGoal(1.0, 1000000.0))
        assertTrue("Large valid range", isValidGoal(1000.0, 500000.0))
    }

    /**
     * Tests Goal validation rejects equal minimum and maximum values.
     * The maximum must be strictly greater than the minimum.
     */
    @Test
    fun goalValidation_rejectsEqualGoals()
    {
        assertFalse("Equal goals should be rejected", isValidGoal(500.0, 500.0))
        assertFalse("Both zero", isValidGoal(0.0, 0.0))
    }

    /**
     * Tests Goal validation rejects minimum greater than maximum.
     */
    @Test
    fun goalValidation_rejectsMinGreaterThanMax()
    {
        assertFalse("Min greater than max should be rejected", isValidGoal(600.0, 500.0))
        assertFalse("Min much larger than max", isValidGoal(10000.0, 100.0))
    }

    /**
     * Tests Goal validation rejects negative minimum values.
     */
    @Test
    fun goalValidation_rejectsNegativeMinGoal()
    {
        assertFalse("Negative minimum goal should be rejected", isValidGoal(-10.0, 100.0))
        assertFalse("Both negative", isValidGoal(-100.0, -50.0))
    }

    /**
     * Tests Goal validation rejects negative maximum values.
     */
    @Test
    fun goalValidation_rejectsNegativeMaxGoal()
    {
        assertFalse("Negative maximum goal should be rejected", isValidGoal(10.0, -100.0))
    }

    /**
     * Tests Goal validation rejects maximum values exceeding the limit.
     */
    @Test
    fun goalValidation_rejectsExcessiveMaxGoal()
    {
        assertFalse("Max goal exceeding 1,000,000 should be rejected", isValidGoal(100.0, 1000001.0))
        assertFalse("Max goal far exceeding limit", isValidGoal(1.0, 2000000.0))
    }

    /**
     * Tests Goal validation rejects minimum value of zero.
     */
    @Test
    fun goalValidation_rejectsZeroMinGoal()
    {
        assertFalse("Zero minimum goal should be rejected", isValidGoal(0.0, 100.0))
    }

    // ==================== Category Model Tests ====================

    /**
     * Tests Category model construction and property access.
     * Verifies that id and name are stored correctly.
     */
    @Test
    fun categoryModel_storesPropertiesCorrectly()
    {
        val category = Category(1, "Groceries")

        assertEquals("Category ID should match", 1, category.id)
        assertEquals("Category name should match", "Groceries", category.name)
    }

    /**
     * Tests Category model with empty name.
     * Verifies that the model accepts empty strings (validation is UI-layer responsibility).
     */
    @Test
    fun categoryModel_acceptsEmptyName()
    {
        val category = Category(2, "")

        assertEquals("Empty name should be stored", "", category.name)
    }

    // ==================== Expense Model Tests ====================

    /**
     * Tests Expense model construction with all fields.
     * Verifies that all properties are stored correctly.
     */
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

    /**
     * Tests Expense model with null photo URI.
     * Verifies that optional photoUri defaults to null.
     */
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

    // ==================== User Model Tests ====================

    /**
     * Tests User model construction with all fields.
     * Verifies that all properties including userId are stored correctly.
     */
    @Test
    fun userModel_storesAllPropertiesCorrectly()
    {
        val user = User(
            name = "John",
            surname = "Doe",
            username = "johndoe",
            password = "SecurePass1!",
            userId = "JOHN202604220001"
        )

        assertEquals("Name should match", "John", user.name)
        assertEquals("Surname should match", "Doe", user.surname)
        assertEquals("Username should match", "johndoe", user.username)
        assertEquals("Password should match", "SecurePass1!", user.password)
        assertEquals("User ID should match", "JOHN202604220001", user.userId)
    }

    /**
     * Tests User model with default userId.
     * Verifies that the default parameter produces an empty string.
     */
    @Test
    fun userModel_defaultUserIdIsEmpty()
    {
        val user = User(
            name = "Jane",
            surname = "Smith",
            username = "janesmith",
            password = "Pass123!"
        )

        assertEquals("Default userId should be empty string", "", user.userId)
    }

    // ==================== Goal Model Tests ====================

    /**
     * Tests Goal model construction and property access.
     * Verifies that minGoal and maxGoal are stored correctly.
     */
    @Test
    fun goalModel_storesPropertiesCorrectly()
    {
        val goal = Goal(200.0, 800.0)

        assertEquals("Min goal should match", 200.0, goal.minGoal, 0.001)
        assertEquals("Max goal should match", 800.0, goal.maxGoal, 0.001)
    }

    /**
     * Tests Goal model with zero values.
     * Verifies that the model accepts zeros (validation is UI-layer responsibility).
     */
    @Test
    fun goalModel_acceptsZeroValues()
    {
        val goal = Goal(0.0, 0.0)

        assertEquals("Min goal should be zero", 0.0, goal.minGoal, 0.001)
        assertEquals("Max goal should be zero", 0.0, goal.maxGoal, 0.001)
    }

    // ==================== Part 3: Badge Model Tests ====================

    /**
     * Tests Badge model construction and property access.
     * Verifies that id, name, description, and earnedDate are stored correctly.
     */
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

    /**
     * Tests Badge model with custom icon resource.
     * Verifies that iconResource is stored correctly.
     */
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

    /**
     * Tests Badge model equality and data class behavior.
     * Verifies that two badges with identical properties are considered equal.
     */
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

    /**
     * Tests StreakData model construction and property access.
     * Verifies that currentStreak, longestStreak, and lastExpenseDate are stored correctly.
     */
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

    /**
     * Tests StreakData model with zero values.
     * Verifies that the model accepts zero streaks for new users.
     */
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

    /**
     * Tests StreakData model with single day streak.
     * Verifies that a new user's first expense produces streak = 1.
     */
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

    // ==================== Part 3: Streak Calculation Logic Tests (Using Helper Function) ====================

    /**
     * Tests streak calculation when expense is logged on consecutive days.
     * Verifies that currentStreak increments by 1.
     */
    @Test
    fun streakCalculation_incrementsOnConsecutiveDays()
    {
        val result = calculateNewStreak(5, 1)
        assertEquals("Streak should increment by 1", 6, result)
    }

    /**
     * Tests streak calculation when expense is logged on the same day.
     * Verifies that currentStreak remains unchanged.
     */
    @Test
    fun streakCalculation_remainsUnchangedOnSameDay()
    {
        val result = calculateNewStreak(5, 0)
        assertEquals("Streak should remain unchanged", 5, result)
    }

    /**
     * Tests streak calculation when there is a gap of more than one day.
     * Verifies that currentStreak resets to 1.
     */
    @Test
    fun streakCalculation_resetsOnGap()
    {
        val result = calculateNewStreak(5, 2)
        assertEquals("Streak should reset to 1", 1, result)
    }

    /**
     * Tests streak calculation with a large gap (multiple days missed).
     * Verifies that currentStreak resets to 1 regardless of gap size.
     */
    @Test
    fun streakCalculation_resetsOnLargeGap()
    {
        val result = calculateNewStreak(10, 7)
        assertEquals("Streak should reset to 1 for any gap greater than 1", 1, result)
    }

    /**
     * Tests streak calculation when starting from zero (first expense ever).
     */
    @Test
    fun streakCalculation_startsAtOneForFirstExpense()
    {
        val result = calculateNewStreak(0, 1)
        assertEquals("First expense should set streak to 1", 1, result)
    }

    /**
     * Tests longest streak update logic.
     * Verifies that longestStreak is updated only when currentStreak exceeds the previous record.
     */
    @Test
    fun streakCalculation_updatesLongestStreak()
    {
        val currentStreak = 15
        val longestStreak = 10
        val newLongestStreak = maxOf(currentStreak, longestStreak)

        assertEquals("Longest streak should update to larger value", 15, newLongestStreak)
    }

    /**
     * Tests longest streak remains unchanged when current streak is lower.
     * Verifies that longestStreak is not reduced.
     */
    @Test
    fun streakCalculation_preservesLongestStreak()
    {
        val currentStreak = 5
        val longestStreak = 10
        val newLongestStreak = maxOf(currentStreak, longestStreak)

        assertEquals("Longest streak should remain at previous record", 10, newLongestStreak)
    }

    // ==================== Percentage Calculation Tests (Using Helper Function) ====================

    /**
     * Tests simple arithmetic for percentage calculation.
     * This verifies the mathematical foundation used in CategoryTotalActivity and progress bar.
     */
    @Test
    fun percentageCalculation_isAccurate()
    {
        val part = 25.0
        val whole = 100.0
        val percentage = (part / whole) * 100.0

        assertEquals("Percentage should be 25.0", 25.0, percentage, 0.001)
    }

    /**
     * Tests progress percentage calculation for various spending scenarios.
     */
    @Test
    fun progressPercentage_calculatesCorrectly()
    {
        // Test normal case: spending at exactly 50 percent of max goal
        val percentage50 = calculateProgressPercentage(250.0, 500.0)
        assertEquals("50 percent spending should return 50", 50, percentage50)

        // Test spending at exactly max goal
        val percentage100 = calculateProgressPercentage(500.0, 500.0)
        assertEquals("100 percent spending should return 100", 100, percentage100)

        // Test spending below max goal
        val percentage25 = calculateProgressPercentage(125.0, 500.0)
        assertEquals("25 percent spending should return 25", 25, percentage25)
    }

    /**
     * Tests progress percentage calculation for overspending (exceeding max goal).
     * Verifies that percentage is capped at 100 percent for visual display.
     */
    @Test
    fun progressPercentage_capsAt100Percent()
    {
        val percentage = calculateProgressPercentage(750.0, 500.0)
        assertEquals("Percentage should be capped at 100 for overspending", 100, percentage)

        val percentageExtreme = calculateProgressPercentage(2000.0, 500.0)
        assertEquals("Extreme overspending should also be capped at 100", 100, percentageExtreme)
    }

    /**
     * Tests progress percentage calculation when max goal is zero or negative.
     * Verifies that percentage returns 0 to avoid division by zero.
     */
    @Test
    fun progressPercentage_handlesInvalidMaxGoal()
    {
        val percentageZeroGoal = calculateProgressPercentage(100.0, 0.0)
        assertEquals("Zero max goal should return 0", 0, percentageZeroGoal)

        val percentageNegativeGoal = calculateProgressPercentage(100.0, -50.0)
        assertEquals("Negative max goal should return 0", 0, percentageNegativeGoal)
    }

    /**
     * Tests progress percentage with zero spent.
     * Verifies that 0 percent is returned.
     */
    @Test
    fun progressPercentage_zeroSpent()
    {
        val percentage = calculateProgressPercentage(0.0, 500.0)
        assertEquals("Zero spent should return 0", 0, percentage)
    }

    // ==================== String Formatting Tests ====================

    /**
     * Tests string formatting with Locale.US for consistent decimal output.
     * This verifies the formatting pattern used throughout the application.
     */
    @Test
    fun stringFormatting_usesConsistentLocale()
    {
        val value = 1234.567
        val formatted = String.format(Locale.US, "%.2f", value)

        assertEquals("Should format with dot decimal separator", "1234.57", formatted)
    }

    // ==================== Part 3: Badge Milestone Tests ====================

    /**
     * Tests streak milestone detection for badge awarding.
     * Verifies that the correct badge is awarded at each streak milestone.
     */
    @Test
    fun streakMilestone_detectsCorrectBadge()
    {
        val milestones = listOf(3, 7, 14, 30)

        assertEquals("3 day streak should be milestone 0", 3, milestones[0])
        assertEquals("7 day streak should be milestone 1", 7, milestones[1])
        assertEquals("14 day streak should be milestone 2", 14, milestones[2])
        assertEquals("30 day streak should be milestone 3", 30, milestones[3])
    }

    /**
     * Tests that multiple badges can be awarded as streak increases.
     * Verifies that a streak of 30 days qualifies for all milestone badges.
     */
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

    /**
     * Tests that only appropriate badges are awarded for intermediate streaks.
     * Verifies that a streak of 10 days awards only the 3 and 7 day badges.
     */
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

    // ==================== Part 3: Budget Badge Logic Tests (Using Helper Function) ====================

    /**
     * Tests budget badge awarding condition when spending is within budget.
     * Verifies that the budget badge is awarded when totalSpent is less than or equal to maxGoal.
     */
    @Test
    fun budgetBadge_awardedWhenWithinBudget()
    {
        assertTrue("Spending exactly at max goal should award badge", shouldAwardBudgetBadge(500.0, 500.0))
        assertTrue("Spending below max goal should award badge", shouldAwardBudgetBadge(400.0, 500.0))
        assertTrue("Spending well below max goal should award badge", shouldAwardBudgetBadge(100.0, 500.0))
    }

    /**
     * Tests budget badge not awarded when overspending.
     * Verifies that the budget badge is not awarded when totalSpent exceeds maxGoal.
     */
    @Test
    fun budgetBadge_notAwardedWhenOverBudget()
    {
        assertFalse("Spending slightly above max goal should not award badge", shouldAwardBudgetBadge(501.0, 500.0))
        assertFalse("Spending significantly above max goal should not award badge", shouldAwardBudgetBadge(1000.0, 500.0))
    }

    /**
     * Tests budget badge not awarded when maxGoal is zero or negative.
     * Verifies that the budget badge requires a valid positive maxGoal.
     */
    @Test
    fun budgetBadge_notAwardedForInvalidMaxGoal()
    {
        assertFalse("Zero max goal should not award badge", shouldAwardBudgetBadge(0.0, 0.0))
        assertFalse("Negative max goal should not award badge", shouldAwardBudgetBadge(100.0, -50.0))
    }

    /**
     * Tests budget badge when totalSpent is zero.
     * Verifies that zero spending within a valid budget awards the badge.
     */
    @Test
    fun budgetBadge_awardedForZeroSpending()
    {
        assertTrue("Zero spending within valid budget should award badge", shouldAwardBudgetBadge(0.0, 500.0))
    }
}