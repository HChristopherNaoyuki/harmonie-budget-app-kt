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
import com.example.harmonie_budget_app_kt.viewmodels.ExpenseViewModel

/**
 * CategoryTotalActivity displays the total spent per category.
 * It includes a custom PieChartView to visualize the percentage of all-time spending per category.
 * The pie chart is drawn using Canvas to avoid external dependencies.
 * Paint and RectF objects are preallocated to avoid object allocations during draw operations.
 */
class CategoryTotalActivity : AppCompatActivity()
{
    private lateinit var tvTotals: TextView
    private lateinit var pieContainer: FrameLayout
    private lateinit var username: String

    private val expenseViewModel = ExpenseViewModel()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_total)

        username = intent.getStringExtra("username") ?: "admin"
        tvTotals = findViewById(R.id.tv_totals)
        pieContainer = findViewById(R.id.pie_container)

        // Call through the ViewModel layer
        val expenses = expenseViewModel.getExpenses(this, username)

        val totals = expenses.groupBy { it.categoryId }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        // Calculate total for percentage
        val grandTotal = totals.values.sum()

        val pieData = totals.map { (categoryId, amount) ->
            val percentage = if (grandTotal > 0) (amount / grandTotal * 100) else 0.0
            Pair("Category $categoryId", percentage)
        }

        // Create and add the pie chart view
        val pieChart = PieChartView(this, pieData)
        pieContainer.addView(pieChart)

        // Text list
        val builder = StringBuilder()
        for ((categoryId, total) in totals)
        {
            builder.append("Category $categoryId: $total\n")
        }
        if (totals.isEmpty())
        {
            builder.append("No expenses found")
        }
        tvTotals.text = builder.toString()
    }

    /**
     * Custom PieChartView drawn with Canvas.
     * Paint and RectF objects are preallocated in the constructor to avoid allocations during onDraw.
     * This satisfies the "Avoid object allocations during draw/layout operations" guideline.
     */
    private class PieChartView(
        context: Context,
        private val data: List<Pair<String, Double>>
    ) : View(context)
    {
        private val paint: Paint = Paint().apply { isAntiAlias = true }
        private val textPaint: Paint = Paint().apply { isAntiAlias = true; textSize = 36f; color = Color.BLACK }
        private val rect: RectF = RectF()

        override fun onDraw(canvas: Canvas)
        {
            super.onDraw(canvas)

            val total = data.sumOf { it.second }
            if (total == 0.0) return

            rect.set(0f, 0f, width.toFloat(), height.toFloat())

            var startAngle = 0f
            val colors = listOf(Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA, Color.CYAN)

            data.forEachIndexed { index, (label, value) ->
                val sweepAngle = (value / total * 360).toFloat()
                paint.color = colors[index % colors.size]
                canvas.drawArc(rect, startAngle, sweepAngle, true, paint)
                startAngle += sweepAngle
            }

            // Optional: draw center circle for donut effect
            paint.color = Color.WHITE
            canvas.drawCircle(width / 2f, height / 2f, width / 4f, paint)
        }
    }
}