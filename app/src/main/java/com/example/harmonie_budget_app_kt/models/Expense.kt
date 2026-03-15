// app/src/main/java/com/example/harmonie_budget_app_kt/Expense.kt
data class Expense
    (
    val id: Int,
    val amount: Double,
    val date: String,
    val description: String,
    val categoryId: Int,
    val photoUri: String? = null
)
{
    //
    // Expense model for budget tracking.
    //
    // Supports optional photo URI for receipt.
    // amount is used in CategoryTotalActivity grouping.
    //
}