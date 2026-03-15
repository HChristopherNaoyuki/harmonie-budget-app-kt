// app/src/main/java/com/example/harmonie_budget_app_kt/User.kt
data class User
    (
    val username: String,
    val password: String
)
{
    //
    // User model for login and registration.
    //
    // Used by JsonHelper for JSON persistence.
    // Simple data class; Gson handles serialization automatically.
    //
}