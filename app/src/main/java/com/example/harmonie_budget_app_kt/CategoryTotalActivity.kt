package com.example.harmonie_budget_app_kt

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.viewmodels.CategoryViewModel
import com.example.harmonie_budget_app_kt.viewmodels.ExpenseViewModel
import java.util.Locale

/**
 * CategoryTotalActivity now displays the Pie Chart visualization.
 *
 * Part 3 Enhancement: Added back arrow navigation to return to the Budgets tab.
 */
class CategoryTotalActivity : AppCompatActivity()
{
    private lateinit var tvTotals: TextView
    private lateinit var pieContainer: FrameLayout
    private lateinit var btnReturnHome: Button
    private lateinit var ivBackArrow: TextView
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
        btnReturnHome = findViewById(R.id.btn_return_home)
        ivBackArrow = findViewById(R.id.iv_back_arrow)

        // Back arrow navigation to return to the Budgets tab
        ivBackArrow.setOnClickListener {
            navigateToBudgetsTab()
        }

        btnReturnHome.setOnClickListener {
            navigateToBudgetsTab()
        }

        val expenses = expenseViewModel.getExpenses(this, username)
        val categories = categoryViewModel.getCategories(this, username)

        val categoryMap: Map<Int, Category> = categories.associateBy { it.id }

        val totals = expenses.groupBy { it.categoryId }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val grandTotal = totals.values.sum()

        val pieData = totals.map { (categoryId, amount) ->
            val categoryName = categoryMap[categoryId]?.name ?: "General"
            val percentage = if (grandTotal > 0) (amount / grandTotal * 100) else 0.0
            Pair(categoryName, percentage)
        }

        val pieChart = PieChartView(this, pieData)
        pieContainer.addView(pieChart)

        val builder = StringBuilder()
        for ((categoryId, total) in totals)
        {
            val categoryName = categoryMap[categoryId]?.name ?: "General"
            val percentage = if (grandTotal > 0) (total / grandTotal * 100) else 0.0
            builder.append(
                String.format(
                    Locale.US,
                    "%s: %.2f (%.1f%%) %n",
                    categoryName,
                    total,
                    percentage
                )
            )
        }
        if (totals.isEmpty())
        {
            builder.append("No expenses found")
        }
        tvTotals.text = builder.toString()
    }

    /**
     * Navigates back to the DashboardActivity and selects the Budgets tab.
     */
    private fun navigateToBudgetsTab()
    {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("username", username)
        intent.putExtra("selected_tab", R.id.nav_budgets)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    /**
     * Custom PieChartView that draws the pie chart and a legend.
     */
    private class PieChartView(
        context: Context,
        private val data: List<Pair<String, Double>>
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
        private val labelPaint: Paint = Paint().apply {
            isAntiAlias = true
            textSize = 24f
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
        }

        override fun onDraw(canvas: Canvas)
        {
            super.onDraw(canvas)

            if (data.isEmpty()) return

            val total = data.sumOf { it.second }
            if (total == 0.0) return

            val pieHeight = (height * 0.65f).toInt()
            val pieSize = minOf(width, pieHeight)
            val left = (width - pieSize) / 2f
            val top = 40f
            rect.set(left, top, left + pieSize, top + pieSize)

            var startAngle = 0f
            val colors = listOf(
                "#E53935".toColorInt(),
                "#1E88E5".toColorInt(),
                "#43A047".toColorInt(),
                "#FDD835".toColorInt(),
                "#8E24AA".toColorInt(),
                "#00ACC1".toColorInt(),
                "#FB8C00".toColorInt(),
                "#3949AB".toColorInt()
            )
            val gap = 2f

            data.forEachIndexed { index, (_, value) ->
                val sweepAngle = (value / total * 360f - gap).toFloat().coerceAtLeast(0f)
                paint.color = colors[index % colors.size]
                canvas.drawArc(rect, startAngle, sweepAngle, true, paint)

                if (sweepAngle > 15f)
                {
                    val midAngle = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
                    val radius = rect.width() / 3f
                    val labelX = rect.centerX() + (radius * kotlin.math.cos(midAngle)).toFloat()
                    val labelY = rect.centerY() + (radius * kotlin.math.sin(midAngle)).toFloat()
                    val percentageText = String.format(Locale.US, "%.1f%%", value)
                    labelPaint.color = Color.WHITE
                    canvas.drawText(percentageText, labelX, labelY + 8f, labelPaint)
                }

                startAngle += sweepAngle + gap
            }

            paint.color = Color.WHITE
            canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 5f, paint)

            val legendStartY = pieHeight + 60f
            val legendItemHeight = 48f
            val colorSize = 28f

            data.forEachIndexed { index, (name, value) ->
                val y = legendStartY + (index * legendItemHeight)
                legendPaint.color = colors[index % colors.size]
                canvas.drawRect(40f, y, 40f + colorSize, y + colorSize, legendPaint)

                val percentageText = String.format(Locale.US, "%.1f%%", value)
                textPaint.textSize = 28f
                textPaint.color = Color.BLACK
                canvas.drawText("$name: $percentageText", 90f, y + 26f, textPaint)
            }
        }
    }
}