package com.example.harmonie_budget_app_kt.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.models.Goal
import com.example.harmonie_budget_app_kt.models.User

/**
 * HarmonieDatabaseHelper manages the embedded SQLite database for Harmonie Budget App.
 *
 * It provides database connectivity, full CRUD operations, dirty-flag tracking for sync,
 * and transactional integrity across entity tables.
 */
class HarmonieDatabaseHelper(context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION)
{
    companion object
    {
        private const val TAG = "HarmonieDatabaseHelper"
        private const val DATABASE_NAME = "harmonie_budget.db"
        private const val DATABASE_VERSION = 1

        private const val TABLE_USERS = "users"
        private const val TABLE_CATEGORIES = "categories"
        private const val TABLE_EXPENSES = "expenses"
        private const val TABLE_GOALS = "goals"
        private const val TABLE_SYNC_LOG = "sync_log"
    }

    override fun onCreate(db: SQLiteDatabase)
    {
        Log.i(TAG, "Creating SQLite database tables for Harmonie Budget App.")

        val createUsersTable = """
            CREATE TABLE $TABLE_USERS (
                user_id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                surname TEXT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL,
                sso_provider TEXT DEFAULT 'LOCAL',
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL,
                is_dirty INTEGER NOT NULL DEFAULT 1
            )
        """.trimIndent()

        val createCategoriesTable = """
            CREATE TABLE $TABLE_CATEGORIES (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL,
                name TEXT NOT NULL,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL,
                is_dirty INTEGER NOT NULL DEFAULT 1
            )
        """.trimIndent()

        val createExpensesTable = """
            CREATE TABLE $TABLE_EXPENSES (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL,
                description TEXT NOT NULL,
                amount REAL NOT NULL,
                category_id INTEGER NOT NULL,
                date TEXT NOT NULL,
                start_time TEXT NOT NULL,
                end_time TEXT NOT NULL,
                photo_uri TEXT,
                created_at INTEGER NOT NULL,
                updated_at INTEGER NOT NULL,
                is_dirty INTEGER NOT NULL DEFAULT 1
            )
        """.trimIndent()

        val createGoalsTable = """
            CREATE TABLE $TABLE_GOALS (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT NOT NULL UNIQUE,
                min_goal REAL NOT NULL,
                max_goal REAL NOT NULL,
                updated_at INTEGER NOT NULL,
                is_dirty INTEGER NOT NULL DEFAULT 1
            )
        """.trimIndent()

        val createSyncLogTable = """
            CREATE TABLE $TABLE_SYNC_LOG (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                sync_timestamp INTEGER NOT NULL,
                status TEXT NOT NULL,
                records_synced INTEGER NOT NULL,
                error_message TEXT
            )
        """.trimIndent()

        db.execSQL(createUsersTable)
        db.execSQL(createCategoriesTable)
        db.execSQL(createExpensesTable)
        db.execSQL(createGoalsTable)
        db.execSQL(createSyncLogTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int)
    {
        Log.w(TAG, "Upgrading database from version $oldVersion to $newVersion.")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SYNC_LOG")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_GOALS")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_EXPENSES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_CATEGORIES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USERS")
        onCreate(db)
    }

    // ==================== User Database Operations ====================

    fun saveUser(user: User, ssoProvider: String = "LOCAL"): Boolean
    {
        val db = writableDatabase
        val currentTime = System.currentTimeMillis()
        val values = ContentValues().apply {
            put("user_id", user.userId)
            put("name", user.name)
            put("surname", user.surname)
            put("username", user.username)
            put("password", user.password)
            put("sso_provider", ssoProvider)
            put("created_at", currentTime)
            put("updated_at", currentTime)
            put("is_dirty", 1)
        }

        val result = db.insertWithOnConflict(
            TABLE_USERS,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
        return result != -1L
    }

    fun getUser(username: String): User?
    {
        val db = readableDatabase
        val cursor: Cursor = db.query(
            TABLE_USERS,
            null,
            "username = ?",
            arrayOf(username),
            null,
            null,
            null
        )

        cursor.use { c ->
            if (c.moveToFirst())
            {
                val userId = c.getString(c.getColumnIndexOrThrow("user_id"))
                val name = c.getString(c.getColumnIndexOrThrow("name"))
                val surname = c.getString(c.getColumnIndexOrThrow("surname")) ?: ""
                val uname = c.getString(c.getColumnIndexOrThrow("username"))
                val password = c.getString(c.getColumnIndexOrThrow("password"))
                return User(name = name, surname = surname, username = uname, password = password, userId = userId)
            }
        }
        return null
    }

    // ==================== Category Database Operations ====================

    fun saveCategory(username: String, category: Category): Boolean
    {
        val db = writableDatabase
        val currentTime = System.currentTimeMillis()
        val values = ContentValues().apply {
            if (category.id > 0)
            {
                put("id", category.id)
            }
            put("username", username)
            put("name", category.name.trim())
            put("created_at", currentTime)
            put("updated_at", currentTime)
            put("is_dirty", 1)
        }

        val result = db.insertWithOnConflict(
            TABLE_CATEGORIES,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
        return result != -1L
    }

    fun getCategories(username: String): List<Category>
    {
        val list = mutableListOf<Category>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_CATEGORIES,
            null,
            "username = ?",
            arrayOf(username),
            null,
            null,
            "name ASC"
        )

        cursor.use { c ->
            while (c.moveToNext())
            {
                val id = c.getInt(c.getColumnIndexOrThrow("id"))
                val name = c.getString(c.getColumnIndexOrThrow("name"))
                list.add(Category(id = id, name = name))
            }
        }
        return list
    }

    fun deleteCategory(username: String, categoryId: Int): Boolean
    {
        val db = writableDatabase
        val rows = db.delete(
            TABLE_CATEGORIES,
            "username = ? AND id = ?",
            arrayOf(username, categoryId.toString())
        )
        return rows > 0
    }

    // ==================== Expense Database Operations ====================

    fun saveExpense(username: String, expense: Expense): Boolean
    {
        val db = writableDatabase
        val currentTime = System.currentTimeMillis()
        val values = ContentValues().apply {
            if (expense.id > 0)
            {
                put("id", expense.id)
            }
            put("username", username)
            put("description", expense.description)
            put("amount", expense.amount)
            put("category_id", expense.categoryId)
            put("date", expense.date)
            put("start_time", expense.startTime)
            put("end_time", expense.endTime)
            put("photo_uri", expense.photoUri)
            put("created_at", currentTime)
            put("updated_at", currentTime)
            put("is_dirty", 1)
        }

        val result = db.insertWithOnConflict(
            TABLE_EXPENSES,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
        return result != -1L
    }

    fun getExpenses(username: String): List<Expense>
    {
        val list = mutableListOf<Expense>()
        val db = readableDatabase
        val cursor = db.query(
            TABLE_EXPENSES,
            null,
            "username = ?",
            arrayOf(username),
            null,
            null,
            "id DESC"
        )

        cursor.use { c ->
            while (c.moveToNext())
            {
                val id = c.getInt(c.getColumnIndexOrThrow("id"))
                val description = c.getString(c.getColumnIndexOrThrow("description"))
                val amount = c.getDouble(c.getColumnIndexOrThrow("amount"))
                val categoryId = c.getInt(c.getColumnIndexOrThrow("category_id"))
                val date = c.getString(c.getColumnIndexOrThrow("date"))
                val startTime = c.getString(c.getColumnIndexOrThrow("start_time"))
                val endTime = c.getString(c.getColumnIndexOrThrow("end_time"))
                val photoUri = c.getString(c.getColumnIndexOrThrow("photo_uri"))
                list.add(
                    Expense(
                        id = id,
                        amount = amount,
                        date = date,
                        startTime = startTime,
                        endTime = endTime,
                        description = description,
                        categoryId = categoryId,
                        photoUri = photoUri
                    )
                )
            }
        }
        return list
    }

    fun deleteExpense(username: String, expenseId: Int): Boolean
    {
        val db = writableDatabase
        val rows = db.delete(
            TABLE_EXPENSES,
            "username = ? AND id = ?",
            arrayOf(username, expenseId.toString())
        )
        return rows > 0
    }

    // ==================== Goal Database Operations ====================

    fun saveGoal(username: String, goal: Goal): Boolean
    {
        val db = writableDatabase
        val currentTime = System.currentTimeMillis()
        val values = ContentValues().apply {
            put("username", username)
            put("min_goal", goal.minGoal)
            put("max_goal", goal.maxGoal)
            put("updated_at", currentTime)
            put("is_dirty", 1)
        }

        val result = db.insertWithOnConflict(
            TABLE_GOALS,
            null,
            values,
            SQLiteDatabase.CONFLICT_REPLACE
        )
        return result != -1L
    }

    fun getGoal(username: String): Goal?
    {
        val db = readableDatabase
        val cursor = db.query(
            TABLE_GOALS,
            null,
            "username = ?",
            arrayOf(username),
            null,
            null,
            null
        )

        cursor.use { c ->
            if (c.moveToFirst())
            {
                val minGoal = c.getDouble(c.getColumnIndexOrThrow("min_goal"))
                val maxGoal = c.getDouble(c.getColumnIndexOrThrow("max_goal"))
                return Goal(minGoal = minGoal, maxGoal = maxGoal)
            }
        }
        return null
    }

    // ==================== Synchronization & Dirty Record Queries ====================

    fun getDirtyRecordCount(): Int
    {
        val db = readableDatabase
        var totalDirty = 0

        val tables = arrayOf(TABLE_USERS, TABLE_CATEGORIES, TABLE_EXPENSES, TABLE_GOALS)
        for (table in tables)
        {
            val cursor = db.rawQuery("SELECT COUNT(*) FROM $table WHERE is_dirty = 1", null)
            cursor.use { c ->
                if (c.moveToFirst())
                {
                    totalDirty += c.getInt(0)
                }
            }
        }
        return totalDirty
    }

    fun clearDirtyFlags(): Int
    {
        val db = writableDatabase
        var cleared = 0
        val values = ContentValues().apply {
            put("is_dirty", 0)
        }

        val tables = arrayOf(TABLE_USERS, TABLE_CATEGORIES, TABLE_EXPENSES, TABLE_GOALS)
        for (table in tables)
        {
            cleared += db.update(table, values, "is_dirty = 1", null)
        }
        return cleared
    }

    fun logSyncEvent(status: String, recordsSynced: Int, errorMessage: String? = null): Boolean
    {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("sync_timestamp", System.currentTimeMillis())
            put("status", status)
            put("records_synced", recordsSynced)
            put("error_message", errorMessage)
        }
        return db.insert(TABLE_SYNC_LOG, null, values) != -1L
    }
}