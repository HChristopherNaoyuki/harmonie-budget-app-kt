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
 *
 * Note: All user test data uses full names (first name and surname) to satisfy the
 * full name validation requirement in JsonHelper.saveUser().
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

    @Test
    fun userCreation_savesAndLoadsCorrectly()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()

        // Using full name (first name and surname) to pass validation
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
    }

    @Test
    fun userLoading_returnsNullForNonexistentUser()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()

        val loadedUser = jsonHelper.loadUser(context, "nonexistentuser999")

        assertNull("Should return null for nonexistent user", loadedUser)
    }

    @Test
    fun userUpdate_overwritesExistingData()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "updatetestuser"

        // Using full name (first name and surname) to pass validation
        val originalUser = User("Original Name", "", username, "OldPass1!", "ORIG202604220001")
        jsonHelper.saveUser(context, originalUser)

        // Using full name (first name and surname) to pass validation
        val updatedUser = User("Updated Name", "", username, "NewPass1!", "UPDT202604220001")
        jsonHelper.saveUser(context, updatedUser)

        val loadedUser = jsonHelper.loadUser(context, username)

        assertNotNull("Loaded user should not be null", loadedUser)
        assertEquals("Name should be updated", "Updated Name", loadedUser?.name)
        assertEquals("User ID should be updated", "UPDT202604220001", loadedUser?.userId)
    }

    // ==================== Expense Tests ====================

    @Test
    fun expenseStorage_savesWithIncrementalId()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "testuser123"

        // Ensure user exists with valid full name before testing expenses
        val testUser = User("Test User", "", username, "Test123!", "TEST202604190001")
        jsonHelper.saveUser(context, testUser)

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

    @Test
    fun expenseStorage_preservesDataIntegrity()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "testuser456"

        // Ensure user exists with valid full name before testing expenses
        val testUser = User("Test User", "", username, "Test123!", "TEST202604190001")
        jsonHelper.saveUser(context, testUser)

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

    // ==================== Goal Tests ====================

    @Test
    fun goalStorage_savesAndLoadsCorrectly()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "testuser123"

        // Ensure user exists with valid full name before testing goals
        val testUser = User("Test User", "", username, "Test123!", "TEST202604190001")
        jsonHelper.saveUser(context, testUser)

        val goal = Goal(minGoal = 200.0, maxGoal = 800.0)
        jsonHelper.saveGoal(context, username, goal)

        val loadedGoal = jsonHelper.loadGoal(context, username)

        assertNotNull("Loaded goal should not be null", loadedGoal)
        assertEquals("Min goal should match", 200.0, loadedGoal?.minGoal ?: 0.0, 0.001)
        assertEquals("Max goal should match", 800.0, loadedGoal?.maxGoal ?: 0.0, 0.001)
    }

    @Test
    fun goalLoading_returnsNullWhenNoGoalExists()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "testuser999"

        val loadedGoal = jsonHelper.loadGoal(context, username)

        assertNull("Should return null when no goal exists", loadedGoal)
    }

    // ==================== Category Tests ====================

    @Test
    fun categoryStorage_savesAndLoadsCorrectly()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "testuser123"

        // Ensure user exists with valid full name before testing categories
        val testUser = User("Test User", "", username, "Test123!", "TEST202604190001")
        jsonHelper.saveUser(context, testUser)

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

    @Test
    fun categoryLoading_returnsEmptyListWhenNoFileExists()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "testuser999"

        val loadedCategories = jsonHelper.loadCategories(context, username)

        assertTrue("Should return empty list", loadedCategories.isEmpty())
    }

    // ==================== Part 3: Badge Tests ====================

    @Test
    fun badgeStorage_savesAndLoadsCorrectly()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "badgetestuser"

        // Ensure user exists with valid full name before testing badges
        val testUser = User("Test User", "", username, "Test123!", "BADGE202604190001")
        jsonHelper.saveUser(context, testUser)

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

    @Test
    fun badgeStorage_preventsDuplicateBadges()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "badgeduplicatetest"

        // Ensure user exists with valid full name before testing badges
        val testUser = User("Test User", "", username, "Test123!", "DUPE202604190001")
        jsonHelper.saveUser(context, testUser)

        val badgesFile = File(context.filesDir, "budget_data/${username}_badges.json")
        if (badgesFile.exists()) badgesFile.delete()

        val badge = Badge(1, "Test Badge", "Test Description", "2026-05-05", 0)

        jsonHelper.saveBadge(context, username, badge)
        jsonHelper.saveBadge(context, username, badge)

        val loadedBadges = jsonHelper.loadBadges(context, username)

        assertEquals("Should have exactly 1 badge (no duplicates)", 1, loadedBadges.size)
    }

    @Test
    fun badgeLoading_returnsEmptyListWhenNoFileExists()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "nobadgesuser"

        val loadedBadges = jsonHelper.loadBadges(context, username)

        assertTrue("Should return empty list when no badges exist", loadedBadges.isEmpty())
    }

    // ==================== Part 3: StreakData Tests ====================

    @Test
    fun streakDataStorage_savesAndLoadsCorrectly()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "streaktestuser"

        // Ensure user exists with valid full name before testing streak data
        val testUser = User("Test User", "", username, "Test123!", "STREAK202604190001")
        jsonHelper.saveUser(context, testUser)

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

    @Test
    fun streakDataLoading_returnsNullWhenNoFileExists()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "nostreakuser"

        val loadedStreakData = jsonHelper.loadStreakData(context, username)

        assertNull("Should return null when no streak data exists", loadedStreakData)
    }

    // ==================== Export and Reset Tests ====================

    @Test
    fun exportData_createsExportFiles()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "exporttestuser"

        // Using full name (first name and surname) to pass validation
        val user = User("Export User", "", username, "Pass123!", "EXPO202604220001")
        jsonHelper.saveUser(context, user)
        jsonHelper.saveExpense(context, username, Expense(0, 50.0, "2026-04-22", "10:00", "11:00", "Test", 1))
        jsonHelper.saveCategory(context, username, Category(1, "TestCat"))
        jsonHelper.saveGoal(context, username, Goal(100.0, 500.0))

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

    @Test
    fun resetProgress_deletesDataFiles()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "resettestuser"

        // Using full name (first name and surname) to pass validation
        val user = User("Reset User", "", username, "Pass123!", "RESET202604220001")
        jsonHelper.saveUser(context, user)
        jsonHelper.saveExpense(context, username, Expense(0, 50.0, "2026-04-22", "10:00", "11:00", "Test", 1))
        jsonHelper.saveCategory(context, username, Category(1, "TestCat"))
        jsonHelper.saveGoal(context, username, Goal(100.0, 500.0))

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

    @Test
    fun resetProgress_preservesUserAccount()
    {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val jsonHelper = JsonHelper()
        val username = "accountpreservetest"

        // CORRECTED: Using full name (first name and surname) to pass validation
        // Previously failed because "Account" is a single name
        val user = User("Account User", "", username, "KeepPass1!", "ACCT202604220001")
        jsonHelper.saveUser(context, user)
        jsonHelper.saveExpense(context, username, Expense(0, 50.0, "2026-04-22", "10:00", "11:00", "Test", 1))
        jsonHelper.saveGoal(context, username, Goal(100.0, 500.0))

        jsonHelper.resetProgress(context, username)

        val loadedUser = jsonHelper.loadUser(context, username)
        assertNotNull("User account should be preserved after reset", loadedUser)
        assertEquals("Username should match", username, loadedUser?.username)
        assertEquals("Name should be preserved", "Account User", loadedUser?.name)

        val loadedExpenses = jsonHelper.loadExpenses(context, username)
        assertTrue("Expenses should be empty after reset", loadedExpenses.isEmpty())

        val newExpense = Expense(0, 75.0, "2026-05-05", "14:00", "15:00", "New expense after reset", 1)
        jsonHelper.saveExpense(context, username, newExpense)

        val expensesAfterReset = jsonHelper.loadExpenses(context, username)
        assertEquals("Should be able to create new expenses after reset", 1, expensesAfterReset.size)
        assertEquals("New expense amount should match", 75.0, expensesAfterReset[0].amount, 0.001)
    }

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
}