package com.example.harmonie_budget_app_kt

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import com.example.harmonie_budget_app_kt.utils.JsonHelper
import com.example.harmonie_budget_app_kt.models.Badge
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.models.Goal
import com.example.harmonie_budget_app_kt.models.StreakData
import com.example.harmonie_budget_app_kt.models.User
import java.io.File

/**
 * Instrumented tests, which execute on an Android device or emulator.
 * These tests verify functionality that requires real Context and file system access.
 * Tests cover core data persistence, user creation, expense storage, category management,
 * goal handling, export operations, reset functionality, and Part 3 gamification features
 * (badges and streak data persistence).
 * All tests are written in Allman style with detailed professional comments.
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest
{
    /**
     * Verifies the app context is correctly loaded.
     * This is the default instrumented test provided by the template.
     */
    @Test
    fun useAppContext()
    {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.harmonie_budget_app_kt", appContext.packageName)
    }

    // ==================== User Tests ====================

    /**
     * Tests full user creation and loading cycle using real file storage.
     * Verifies the generated User ID is correctly saved and retrieved.
     */
    @Test
    fun userCreation_savesAndLoadsCorrectly()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()

        val testUser = User(
            name = "Test User",
            surname = "",
            username = "testuser123",
            password = "Test123!",
            userId = "TEST202604190001"
        )

        jsonHelper.saveUser(context, testUser)
        val loadedUser = jsonHelper.loadUser(context, "testuser123")

        assertNotNull("Loaded user should not be null", loadedUser)
        assertEquals("User ID should match", "TEST202604190001", loadedUser?.userId)
        assertEquals("Username should match", "testuser123", loadedUser?.username)
        assertEquals("Name should match", "Test User", loadedUser?.name)
        assertEquals("Password should match", "Test123!", loadedUser?.password)
    }

    /**
     * Tests user loading with nonexistent username.
     * Verifies that null is returned when no user file exists.
     */
    @Test
    fun userLoading_returnsNullForNonexistentUser()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()

        val loadedUser = jsonHelper.loadUser(context, "nonexistentuser999")

        assertNull("Should return null for nonexistent user", loadedUser)
    }

    /**
     * Tests user update functionality.
     * Verifies that saving a user with the same username overwrites existing data.
     */
    @Test
    fun userUpdate_overwritesExistingData()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "updatetestuser"

        val originalUser = User("Original", "Name", username, "OldPass1!", "ORIG202604220001")
        jsonHelper.saveUser(context, originalUser)

        val updatedUser = User("Updated", "Name", username, "NewPass1!", "UPDT202604220001")
        jsonHelper.saveUser(context, updatedUser)

        val loadedUser = jsonHelper.loadUser(context, username)

        assertNotNull("Loaded user should not be null", loadedUser)
        assertEquals("Name should be updated", "Updated", loadedUser?.name)
        assertEquals("Password should be updated", "NewPass1!", loadedUser?.password)
        assertEquals("User ID should be updated", "UPDT202604220001", loadedUser?.userId)
    }

    // ==================== Expense Tests ====================

    /**
     * Tests expense storage and ID generation using real file system.
     * Verifies incremental ID assignment and data integrity.
     */
    @Test
    fun expenseStorage_savesWithIncrementalId()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "testuser123"

        // Clear any existing expenses for a clean test
        val expensesFile = File(context.filesDir, "budget_data/${username}_expenses.json")
        if (expensesFile.exists()) expensesFile.delete()

        val expense1 = Expense(0, 25.50, "2026-04-19", "09:00", "10:00", "Groceries", 1)
        jsonHelper.saveExpense(context, username, expense1)

        val expense2 = Expense(0, 15.75, "2026-04-19", "11:00", "12:00", "Transport", 2)
        jsonHelper.saveExpense(context, username, expense2)

        val loadedExpenses = jsonHelper.loadExpenses(context, username)

        assertEquals("Should have 2 expenses", 2, loadedExpenses.size)
        assertEquals("First expense ID should be 1", 1, loadedExpenses[0].id)
        assertEquals("Second expense ID should be 2", 2, loadedExpenses[1].id)
    }

    /**
     * Tests expense data integrity after save and load.
     * Verifies that all fields are preserved correctly.
     */
    @Test
    fun expenseStorage_preservesDataIntegrity()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "testuser456"
        val expensesFile = File(context.filesDir, "budget_data/${username}_expenses.json")
        if (expensesFile.exists()) expensesFile.delete()

        val expense = Expense(
            id = 0,
            amount = 99.99,
            date = "2026-04-22",
            startTime = "08:30",
            endTime = "09:30",
            description = "Business lunch",
            categoryId = 5,
            photoUri = "content://media/456"
        )

        jsonHelper.saveExpense(context, username, expense)
        val loadedExpenses = jsonHelper.loadExpenses(context, username)

        assertEquals("Should have 1 expense", 1, loadedExpenses.size)
        val loaded = loadedExpenses[0]
        assertEquals("Amount should match", 99.99, loaded.amount, 0.001)
        assertEquals("Date should match", "2026-04-22", loaded.date)
        assertEquals("Start time should match", "08:30", loaded.startTime)
        assertEquals("End time should match", "09:30", loaded.endTime)
        assertEquals("Description should match", "Business lunch", loaded.description)
        assertEquals("Category ID should match", 5, loaded.categoryId)
        assertEquals("Photo URI should match", "content://media/456", loaded.photoUri)
    }

    /**
     * Tests multiple expense saves verify sequential ID assignment.
     * Verifies that IDs increment monotonically even across multiple save calls.
     */
    @Test
    fun expenseStorage_multipleSavesMaintainSequence()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "sequencetestuser"
        val expensesFile = File(context.filesDir, "budget_data/${username}_expenses.json")
        if (expensesFile.exists()) expensesFile.delete()

        // Save 5 expenses
        for (i in 1..5)
        {
            jsonHelper.saveExpense(context, username, Expense(0, i * 10.0, "2026-04-22", "10:00", "11:00", "Expense $i", 1))
        }

        val loadedExpenses = jsonHelper.loadExpenses(context, username)

        assertEquals("Should have 5 expenses", 5, loadedExpenses.size)
        for (i in 0..4)
        {
            assertEquals("Expense ${i + 1} should have ID ${i + 1}", i + 1, loadedExpenses[i].id)
        }
    }

    // ==================== Goal Tests ====================

    /**
     * Tests goal save and load cycle with real file storage.
     * Verifies minGoal and maxGoal are correctly persisted.
     */
    @Test
    fun goalStorage_savesAndLoadsCorrectly()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "testuser123"

        val goal = Goal(minGoal = 200.0, maxGoal = 800.0)
        jsonHelper.saveGoal(context, username, goal)

        val loadedGoal = jsonHelper.loadGoal(context, username)

        assertNotNull("Loaded goal should not be null", loadedGoal)
        assertEquals("Min goal should match", 200.0, loadedGoal?.minGoal ?: 0.0, 0.001)
        assertEquals("Max goal should match", 800.0, loadedGoal?.maxGoal ?: 0.0, 0.001)
    }

    /**
     * Tests goal loading when no goal file exists.
     * Verifies that null is returned.
     */
    @Test
    fun goalLoading_returnsNullWhenNoGoalExists()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "testuser999"

        val loadedGoal = jsonHelper.loadGoal(context, username)

        assertNull("Should return null when no goal exists", loadedGoal)
    }

    /**
     * Tests goal update functionality.
     * Verifies that saving a goal overwrites the existing goal file.
     */
    @Test
    fun goalUpdate_overwritesExistingGoal()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "goaltestuser"

        val originalGoal = Goal(100.0, 500.0)
        jsonHelper.saveGoal(context, username, originalGoal)

        val updatedGoal = Goal(150.0, 600.0)
        jsonHelper.saveGoal(context, username, updatedGoal)

        val loadedGoal = jsonHelper.loadGoal(context, username)

        assertNotNull("Loaded goal should not be null", loadedGoal)
        assertEquals("Min goal should be updated", 150.0, loadedGoal?.minGoal ?: 0.0, 0.001)
        assertEquals("Max goal should be updated", 600.0, loadedGoal?.maxGoal ?: 0.0, 0.001)
    }

    // ==================== Category Tests ====================

    /**
     * Tests category save and load cycle with real file storage.
     * Verifies that categories are appended and loaded correctly.
     */
    @Test
    fun categoryStorage_savesAndLoadsCorrectly()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "testuser123"
        val categoriesFile = File(context.filesDir, "budget_data/${username}_categories.json")
        if (categoriesFile.exists()) categoriesFile.delete()

        val category1 = Category(1, "Food")
        val category2 = Category(2, "Transport")

        jsonHelper.saveCategory(context, username, category1)
        jsonHelper.saveCategory(context, username, category2)

        val loadedCategories = jsonHelper.loadCategories(context, username)

        assertEquals("Should have 2 categories", 2, loadedCategories.size)
        assertEquals("First category name should match", "Food", loadedCategories[0].name)
        assertEquals("Second category name should match", "Transport", loadedCategories[1].name)
    }

    /**
     * Tests category loading with no existing file.
     * Verifies that an empty list is returned.
     */
    @Test
    fun categoryLoading_returnsEmptyListWhenNoFileExists()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "testuser999"

        val loadedCategories = jsonHelper.loadCategories(context, username)

        assertTrue("Should return empty list", loadedCategories.isEmpty())
    }

    /**
     * Tests category ID auto-generation.
     * Verifies that categories can be saved with manually specified IDs.
     */
    @Test
    fun categoryStorage_acceptsCustomIds()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "categoryidtest"
        val categoriesFile = File(context.filesDir, "budget_data/${username}_categories.json")
        if (categoriesFile.exists()) categoriesFile.delete()

        val category = Category(99, "Custom ID Category")
        jsonHelper.saveCategory(context, username, category)

        val loadedCategories = jsonHelper.loadCategories(context, username)

        assertEquals("Should have 1 category", 1, loadedCategories.size)
        assertEquals("Category ID should be preserved", 99, loadedCategories[0].id)
    }

    // ==================== Part 3: Badge Tests ====================

    /**
     * Tests badge save and load cycle with real file storage.
     * Verifies that badges are correctly persisted and retrieved.
     */
    @Test
    fun badgeStorage_savesAndLoadsCorrectly()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "badgetestuser"
        val badgesFile = File(context.filesDir, "budget_data/${username}_badges.json")
        if (badgesFile.exists()) badgesFile.delete()

        val testDate = "2026-05-05"
        val badge = Badge(
            id = 1,
            name = "Consistent Starter",
            description = "Logged expenses for 3 days in a row",
            earnedDate = testDate,
            iconResource = 0
        )

        jsonHelper.saveBadge(context, username, badge)
        val loadedBadges = jsonHelper.loadBadges(context, username)

        assertEquals("Should have 1 badge", 1, loadedBadges.size)
        assertEquals("Badge ID should match", 1, loadedBadges[0].id)
        assertEquals("Badge name should match", "Consistent Starter", loadedBadges[0].name)
        assertEquals("Badge description should match", "Logged expenses for 3 days in a row", loadedBadges[0].description)
        assertEquals("Earned date should match", testDate, loadedBadges[0].earnedDate)
    }

    /**
     * Tests that duplicate badges are not saved.
     * Verifies that saving a badge with an existing ID does not create a duplicate.
     */
    @Test
    fun badgeStorage_preventsDuplicateBadges()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "badgeduplicatetest"
        val badgesFile = File(context.filesDir, "budget_data/${username}_badges.json")
        if (badgesFile.exists()) badgesFile.delete()

        val badge = Badge(1, "Test Badge", "Test Description", "2026-05-05", 0)

        // Save the same badge twice
        jsonHelper.saveBadge(context, username, badge)
        jsonHelper.saveBadge(context, username, badge)

        val loadedBadges = jsonHelper.loadBadges(context, username)

        assertEquals("Should have exactly 1 badge (no duplicates)", 1, loadedBadges.size)
    }

    /**
     * Tests loading badges when no badge file exists.
     * Verifies that an empty list is returned.
     */
    @Test
    fun badgeLoading_returnsEmptyListWhenNoFileExists()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "nobadgesuser"

        val loadedBadges = jsonHelper.loadBadges(context, username)

        assertTrue("Should return empty list when no badges exist", loadedBadges.isEmpty())
    }

    /**
     * Tests multiple badges are saved and loaded correctly.
     * Verifies that all badges in a list are preserved.
     */
    @Test
    fun badgeStorage_savesMultipleBadges()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "multibadgeuser"
        val badgesFile = File(context.filesDir, "budget_data/${username}_badges.json")
        if (badgesFile.exists()) badgesFile.delete()

        val badge1 = Badge(1, "Badge One", "First badge", "2026-05-01", 0)
        val badge2 = Badge(2, "Badge Two", "Second badge", "2026-05-02", 0)
        val badge3 = Badge(3, "Badge Three", "Third badge", "2026-05-03", 0)

        jsonHelper.saveBadge(context, username, badge1)
        jsonHelper.saveBadge(context, username, badge2)
        jsonHelper.saveBadge(context, username, badge3)

        val loadedBadges = jsonHelper.loadBadges(context, username)

        assertEquals("Should have 3 badges", 3, loadedBadges.size)
        assertEquals("First badge ID should be 1", 1, loadedBadges[0].id)
        assertEquals("Second badge ID should be 2", 2, loadedBadges[1].id)
        assertEquals("Third badge ID should be 3", 3, loadedBadges[2].id)
    }

    // ==================== Part 3: StreakData Tests ====================

    /**
     * Tests streak data save and load cycle with real file storage.
     * Verifies that streak information is correctly persisted.
     */
    @Test
    fun streakDataStorage_savesAndLoadsCorrectly()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "streaktestuser"
        val streakFile = File(context.filesDir, "budget_data/${username}_streak.json")
        if (streakFile.exists()) streakFile.delete()

        val streakData = StreakData(
            currentStreak = 7,
            longestStreak = 14,
            lastExpenseDate = "2026-05-05"
        )

        jsonHelper.saveStreakData(context, username, streakData)
        val loadedStreakData = jsonHelper.loadStreakData(context, username)

        assertNotNull("Loaded streak data should not be null", loadedStreakData)
        assertEquals("Current streak should match", 7, loadedStreakData?.currentStreak)
        assertEquals("Longest streak should match", 14, loadedStreakData?.longestStreak)
        assertEquals("Last expense date should match", "2026-05-05", loadedStreakData?.lastExpenseDate)
    }

    /**
     * Tests streak data loading when no streak file exists.
     * Verifies that null is returned.
     */
    @Test
    fun streakDataLoading_returnsNullWhenNoFileExists()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "nostreakuser"

        val loadedStreakData = jsonHelper.loadStreakData(context, username)

        assertNull("Should return null when no streak data exists", loadedStreakData)
    }

    /**
     * Tests streak data update functionality.
     * Verifies that saving streak data overwrites the existing file.
     */
    @Test
    fun streakDataUpdate_overwritesExistingData()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "streakupdatetest"
        val streakFile = File(context.filesDir, "budget_data/${username}_streak.json")
        if (streakFile.exists()) streakFile.delete()

        val originalStreak = StreakData(3, 3, "2026-05-03")
        jsonHelper.saveStreakData(context, username, originalStreak)

        val updatedStreak = StreakData(5, 5, "2026-05-05")
        jsonHelper.saveStreakData(context, username, updatedStreak)

        val loadedStreakData = jsonHelper.loadStreakData(context, username)

        assertNotNull("Loaded streak data should not be null", loadedStreakData)
        assertEquals("Current streak should be updated", 5, loadedStreakData?.currentStreak)
        assertEquals("Longest streak should be updated", 5, loadedStreakData?.longestStreak)
        assertEquals("Last expense date should be updated", "2026-05-05", loadedStreakData?.lastExpenseDate)
    }

    /**
     * Tests that streak data preserves longest streak even when current streak resets.
     * Verifies the longest streak record is maintained separately from current streak.
     */
    @Test
    fun streakData_preservesLongestStreak()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "longeststreaktest"
        val streakFile = File(context.filesDir, "budget_data/${username}_streak.json")
        if (streakFile.exists()) streakFile.delete()

        // User achieves a streak of 10 days
        val afterHighStreak = StreakData(10, 10, "2026-05-10")
        jsonHelper.saveStreakData(context, username, afterHighStreak)

        // User misses a day, streak resets to 1 but longest remains 10
        val afterReset = StreakData(1, 10, "2026-05-12")
        jsonHelper.saveStreakData(context, username, afterReset)

        val loadedStreakData = jsonHelper.loadStreakData(context, username)

        assertNotNull("Loaded streak data should not be null", loadedStreakData)
        assertEquals("Current streak should be reset to 1", 1, loadedStreakData?.currentStreak)
        assertEquals("Longest streak should remain at 10", 10, loadedStreakData?.longestStreak)
    }

    // ==================== Export and Reset Tests ====================

    /**
     * Tests edge case where no data file exists for any type.
     * Verifies safe loading returns empty list or null as expected.
     */
    @Test
    fun jsonHelper_loadsEmptyDataGracefully()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "nonexistentuser"

        val expenses = jsonHelper.loadExpenses(context, username)
        val goal = jsonHelper.loadGoal(context, username)
        val categories = jsonHelper.loadCategories(context, username)
        val user = jsonHelper.loadUser(context, username)
        val badges = jsonHelper.loadBadges(context, username)
        val streakData = jsonHelper.loadStreakData(context, username)

        assertTrue("Expenses should be empty", expenses.isEmpty())
        assertNull("Goal should be null", goal)
        assertTrue("Categories should be empty", categories.isEmpty())
        assertNull("User should be null", user)
        assertTrue("Badges should be empty", badges.isEmpty())
        assertNull("Streak data should be null", streakData)
    }

    /**
     * Tests data export functionality.
     * Verifies that export creates files in the expected directory including new gamification files.
     */
    @Test
    fun exportData_createsExportFiles()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "exporttestuser"

        // Create prerequisite data
        val user = User("Export", "Test", username, "Pass123!", "EXPO202604220001")
        jsonHelper.saveUser(context, user)
        jsonHelper.saveExpense(context, username, Expense(0, 50.0, "2026-04-22", "10:00", "11:00", "Test", 1))
        jsonHelper.saveCategory(context, username, Category(1, "TestCat"))
        jsonHelper.saveGoal(context, username, Goal(100.0, 500.0))

        // Part 3: Save gamification data for export test
        val badge = Badge(1, "Test Badge", "Test Description", "2026-05-05", 0)
        jsonHelper.saveBadge(context, username, badge)
        val streakData = StreakData(5, 10, "2026-05-05")
        jsonHelper.saveStreakData(context, username, streakData)

        val success = jsonHelper.exportData(context, username)
        assertTrue("Export should succeed", success)

        val exportDir = File(context.filesDir, "budget_data/export_$username")
        assertTrue("Export directory should exist", exportDir.exists())
        assertTrue("User file should exist", File(exportDir, "$username.json").exists())
        assertTrue("Expenses file should exist", File(exportDir, "${username}_expenses.json").exists())
        assertTrue("Categories file should exist", File(exportDir, "${username}_categories.json").exists())
        assertTrue("Goals file should exist", File(exportDir, "${username}_goals.json").exists())
        assertTrue("Badges file should exist", File(exportDir, "${username}_badges.json").exists())
        assertTrue("Streak file should exist", File(exportDir, "${username}_streak.json").exists())
    }

    /**
     * Tests reset progress functionality.
     * Verifies that category, expense, goal, badge, and streak files are deleted.
     * User file should remain intact.
     */
    @Test
    fun resetProgress_deletesDataFiles()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "resettestuser"

        // Create prerequisite data
        val user = User("Reset", "Test", username, "Pass123!", "RESET202604220001")
        jsonHelper.saveUser(context, user)
        jsonHelper.saveExpense(context, username, Expense(0, 50.0, "2026-04-22", "10:00", "11:00", "Test", 1))
        jsonHelper.saveCategory(context, username, Category(1, "TestCat"))
        jsonHelper.saveGoal(context, username, Goal(100.0, 500.0))

        // Part 3: Save gamification data for reset test
        val badge = Badge(1, "Test Badge", "Test Description", "2026-05-05", 0)
        jsonHelper.saveBadge(context, username, badge)
        val streakData = StreakData(5, 10, "2026-05-05")
        jsonHelper.saveStreakData(context, username, streakData)

        val success = jsonHelper.resetProgress(context, username)
        assertTrue("Reset should succeed", success)

        assertTrue("User file should still exist", File(context.filesDir, "budget_data/$username.json").exists())
        assertFalse("Expenses file should be deleted", File(context.filesDir, "budget_data/${username}_expenses.json").exists())
        assertFalse("Categories file should be deleted", File(context.filesDir, "budget_data/${username}_categories.json").exists())
        assertFalse("Goals file should be deleted", File(context.filesDir, "budget_data/${username}_goals.json").exists())
        assertFalse("Badges file should be deleted", File(context.filesDir, "budget_data/${username}_badges.json").exists())
        assertFalse("Streak file should be deleted", File(context.filesDir, "budget_data/${username}_streak.json").exists())
    }

    /**
     * Tests reset progress preserves user account while clearing all progress data.
     * Verifies that after reset, the user can still log in and create new data.
     */
    @Test
    fun resetProgress_preservesUserAccount()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "accountpreservetest"

        // Create user with data
        val user = User("Account", "Test", username, "KeepPass1!", "ACCT202604220001")
        jsonHelper.saveUser(context, user)
        jsonHelper.saveExpense(context, username, Expense(0, 50.0, "2026-04-22", "10:00", "11:00", "Test", 1))
        jsonHelper.saveGoal(context, username, Goal(100.0, 500.0))

        // Reset progress
        jsonHelper.resetProgress(context, username)

        // Load user - should still exist
        val loadedUser = jsonHelper.loadUser(context, username)
        assertNotNull("User account should be preserved after reset", loadedUser)
        assertEquals("Username should match", username, loadedUser?.username)

        // Load expenses - should be empty
        val loadedExpenses = jsonHelper.loadExpenses(context, username)
        assertTrue("Expenses should be empty after reset", loadedExpenses.isEmpty())

        // User can create new expense after reset
        val newExpense = Expense(0, 75.0, "2026-05-05", "14:00", "15:00", "New expense after reset", 1)
        jsonHelper.saveExpense(context, username, newExpense)

        val expensesAfterReset = jsonHelper.loadExpenses(context, username)
        assertEquals("Should be able to create new expenses after reset", 1, expensesAfterReset.size)
        assertEquals("New expense amount should match", 75.0, expensesAfterReset[0].amount, 0.001)
    }
}