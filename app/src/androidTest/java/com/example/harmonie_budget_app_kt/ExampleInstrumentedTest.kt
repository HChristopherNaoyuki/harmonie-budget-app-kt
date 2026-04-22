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
import com.example.harmonie_budget_app_kt.models.Category
import java.io.File

/**
 * Instrumented tests, which execute on an Android device or emulator.
 * These tests verify functionality that requires real Context and file system access.
 * Tests cover core data persistence, user creation, expense storage, category management,
 * goal handling, export operations, and reset functionality.
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

        assertTrue("Expenses should be empty", expenses.isEmpty())
        assertNull("Goal should be null", goal)
        assertTrue("Categories should be empty", categories.isEmpty())
        assertNull("User should be null", user)
    }

    /**
     * Tests data export functionality.
     * Verifies that export creates files in the expected directory.
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

        val success = jsonHelper.exportData(context, username)
        assertTrue("Export should succeed", success)

        val exportDir = File(context.filesDir, "budget_data/export_$username")
        assertTrue("Export directory should exist", exportDir.exists())
        assertTrue("User file should exist", File(exportDir, "$username.json").exists())
        assertTrue("Expenses file should exist", File(exportDir, "${username}_expenses.json").exists())
        assertTrue("Categories file should exist", File(exportDir, "${username}_categories.json").exists())
        assertTrue("Goals file should exist", File(exportDir, "${username}_goals.json").exists())
    }

    /**
     * Tests reset progress functionality.
     * Verifies that category, expense, and goal files are deleted.
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

        val success = jsonHelper.resetProgress(context, username)
        assertTrue("Reset should succeed", success)

        assertTrue("User file should still exist", File(context.filesDir, "budget_data/$username.json").exists())
        assertFalse("Expenses file should be deleted", File(context.filesDir, "budget_data/${username}_expenses.json").exists())
        assertFalse("Categories file should be deleted", File(context.filesDir, "budget_data/${username}_categories.json").exists())
        assertFalse("Goals file should be deleted", File(context.filesDir, "budget_data/${username}_goals.json").exists())
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
}