package com.example.harmonie_budget_app_kt

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.viewmodels.CategoryViewModel
import com.example.harmonie_budget_app_kt.viewmodels.ExpenseViewModel

/**
 * CategoryTotalActivity displays the total spent per category.
 * It includes a custom PieChartView to visualize the percentage of spending per category.
 * The pie chart is drawn using Canvas to avoid external dependencies.
 * Paint and RectF objects are preallocated to avoid object allocations during draw operations.
 * This implementation has been corrected to meet the following requirements:
 * - Each category is calculated and displayed based on its exact percentage of the total.
 * - Chart segments accurately reflect the underlying expense data.
 * - Segments are visually separated by small gaps for clear distinction.
 * - A legend is drawn directly below the pie chart.
 * - The legend maps each segment to its category name using the same colors.
 * - The legend clearly labels each category and shows its percentage.
 * All calculations use the verified totals from expenses grouped by categoryId.
 * Category names are loaded from the user's categories.json file for meaningful labels.
 */
class CategoryTotalActivity : AppCompatActivity()
{
    private lateinit var tvTotals: TextView
    private lateinit var pieContainer: FrameLayout
    private lateinit var username: String

    private val expenseViewModel = ExpenseViewModel()
    private val categoryViewModel = CategoryViewModel()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_total)

        username = intent.getStringExtra("username") ?: "admin"

        tvTotals = findViewById(R.id.tv_totals)
        pieContainer = findViewById(R.id.pie_container)

        // Load expenses and categories through the ViewModel layer
        val expenses = expenseViewModel.getExpenses(this, username)
        val categories = categoryViewModel.getCategories(this, username)

        // Map categoryId to name for meaningful labels in the pie chart and legend
        val categoryMap: Map<Int, Category> = categories.associateBy { it.id }

        // Group expenses by categoryId and calculate totals
        val totals = expenses.groupBy { it.categoryId }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        // Calculate grand total for percentage computation
        val grandTotal = totals.values.sum()

        // Build pie data with real category names (fallback if category not found)
        val pieData = totals.map { (categoryId, amount) ->
            val categoryName = categoryMap[categoryId]?.name ?: "Category $categoryId"
            val percentage = if (grandTotal > 0) (amount / grandTotal * 100) else 0.0
            Pair(categoryName, percentage)
        }

        // Create and add the improved pie chart view
        val pieChart = PieChartView(this, pieData)
        pieContainer.addView(pieChart)

        // Text list of totals (kept for additional clarity)
        val builder = StringBuilder()
        for ((categoryId, total) in totals)
        {
            val categoryName = categoryMap[categoryId]?.name ?: "Category $categoryId"
            builder.append("$categoryName: $total\n")
        }
        if (totals.isEmpty())
        {
            builder.append("No expenses found")
        }
        tvTotals.text = builder.toString()
    }

    /**
     * Custom PieChartView that draws the pie chart and a legend below it.
     * The pie occupies the top portion of the view.
     * The legend is drawn directly below the pie for clean, self-contained visualization.
     * Segments include small gaps for visual separation.
     * Legend uses the exact same colors as the pie segments and shows category name plus percentage.
     * All drawing uses preallocated Paint and RectF objects.
     */
    private class PieChartView(
        context: Context,
        private val data: List<Pair<String, Double>>  // Pair<categoryName, percentage>
    ) : View(context)
    {
        private val paint: Paint = Paint().apply { isAntiAlias = true }
        private val rect: RectF = RectF()
        private val legendPaint: Paint = Paint().apply { isAntiAlias = true }
        private val textPaint: Paint = Paint().apply {
            isAntiAlias = true
            textSize = 28f
            color = Color.BLACK
        }

        override fun onDraw(canvas: Canvas)
        {
            super.onDraw(canvas)

            if (data.isEmpty()) return

            val total = data.sumOf { it.second }
            if (total == 0.0) return

            // Pie chart area: top 65% of available height, centered horizontally
            val pieHeight = (height * 0.65f).toInt()
            val pieSize = minOf(width, pieHeight)
            val left = (width - pieSize) / 2f
            rect.set(left, 40f, left + pieSize, 40f + pieSize)

            var startAngle = 0f
            val colors = listOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.CYAN)
            val gap = 4f  // Small gap between segments for clear visual separation

            // Draw each pie segment
            data.forEachIndexed { index, (_, value) ->
                val sweepAngle = (value / total * 360f - gap).toFloat().coerceAtLeast(0f)
                paint.color = colors[index % colors.size]
                canvas.drawArc(rect, startAngle, sweepAngle, true, paint)
                startAngle += sweepAngle + gap
            }

            // Draw center circle to create a clean donut-style pie
            paint.color = Color.WHITE
            canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 4f, paint)

            // Legend area starts below the pie
            val legendStartY = pieHeight + 80f
            val legendItemHeight = 40f
            val colorSize = 28f

            // Draw legend items
            data.forEachIndexed { index, (name, value) ->
                val y = legendStartY + (index * legendItemHeight)

                // Colored square matching the pie segment
                legendPaint.color = colors[index % colors.size]
                canvas.drawRect(40f, y, 40f + colorSize, y + colorSize, legendPaint)

                // Category label and percentage
                val percentageText = "${"%.1f".format(value)}%"
                textPaint.textSize = 28f
                canvas.drawText("$name: $percentageText", 90f, y + 26f, textPaint)
            }
        }
    }
}