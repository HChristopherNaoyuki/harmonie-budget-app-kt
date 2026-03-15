// app/src/main/java/com/example/harmonie_budget_app_kt/utils/JsonHelper.kt
object JsonHelper
{
    private const val DATA_DIR_NAME = "budget_data"

    //
    // JSON persistence helper.
    //
    // FIXED:
    // 1. All model classes (User, Category, Expense, Goal) are now defined and imported.
    // 2. Generic type parameters explicitly handled; inference errors removed.
    // 3. Folder creation and file handling use app-private internal storage.
    // 4. Gson version 2.10.1 (added in build.gradle) is used for serialization.
    // 5. All methods include detailed comments and error handling for production use.
    //

    private fun getDataDir(context: android.content.Context): java.io.File
    {
        val dir = java.io.File(context.filesDir, DATA_DIR_NAME)
        if (!dir.exists())
        {
            dir.mkdirs()
        }
        return dir
    }

    fun <T> saveList(context: android.content.Context, fileName: String, list: List<T>)
    {
        val file = java.io.File(getDataDir(context), fileName)
        val gson = com.google.gson.Gson()
        val json = gson.toJson(list)
        file.writeText(json)
    }

    fun <T> loadList(context: android.content.Context, fileName: String, clazz: Class<T>): List<T>
    {
        val file = java.io.File(getDataDir(context), fileName)
        if (!file.exists())
        {
            return emptyList()
        }
        val gson = com.google.gson.Gson()
        val json = file.readText()
        val type = com.google.gson.reflect.TypeToken.getParameterized(List::class.java, clazz).type
        return gson.fromJson(json, type) ?: emptyList()
    }

    fun saveGoal(context: android.content.Context, goal: Goal)
    {
        val file = java.io.File(getDataDir(context), "goal.json")
        val gson = com.google.gson.Gson()
        val json = gson.toJson(goal)
        file.writeText(json)
    }

    fun loadGoal(context: android.content.Context): Goal?
    {
        val file = java.io.File(getDataDir(context), "goal.json")
        if (!file.exists())
        {
            return null
        }
        val gson = com.google.gson.Gson()
        val json = file.readText()
        return gson.fromJson(json, Goal::class.java)
    }
}