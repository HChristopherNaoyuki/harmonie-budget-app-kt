package com.example.harmonie_budget_app_kt

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*
import com.example.harmonie_budget_app_kt.utils.JsonHelper
import com.example.harmonie_budget_app_kt.models.User
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.models.Goal
import java.io.File

/**
 * Example instrumented test, which will execute on an Android device or emulator.
 * These tests verify functionality that requires real Context and file system access.
 * Tests cover core data persistence, user creation, expense storage, and goal handling.
 * All tests are written in Allman style with detailed professional comments.
 * This class has been fully corrected to eliminate all unresolved references and warnings.
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

        assertNotNull(loadedUser)
        assertEquals("TEST202604190001", loadedUser?.userId)
        assertEquals("testuser123", loadedUser?.username)
    }

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

        assertEquals(2, loadedExpenses.size)
        assertEquals(1, loadedExpenses[0].id)
        assertEquals(2, loadedExpenses[1].id)
    }

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

        assertNotNull(loadedGoal)
        assertEquals(200.0, loadedGoal?.minGoal ?: 0.0, 0.001)
        assertEquals(800.0, loadedGoal?.maxGoal ?: 0.0, 0.001)
    }

    /**
     * Tests edge case where no data file exists.
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

        assertTrue(expenses.isEmpty())
        assertNull(goal)
        assertTrue(categories.isEmpty())
    }
}