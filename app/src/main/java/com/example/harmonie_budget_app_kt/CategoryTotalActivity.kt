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
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.viewmodels.CategoryViewModel
import com.example.harmonie_budget_app_kt.viewmodels.ExpenseViewModel
import java.util.Locale

/**
 * CategoryTotalActivity displays the Pie Chart visualization and totals table.
 *
 * Part 3 Enhancement:
 * - Added back arrow navigation to return to the Budgets tab.
 * - Updated to display category totals in a table format matching the mockup.
 * - Table columns: Category, Amount (ZAR), Percentage.
 */
class CategoryTotalActivity : AppCompatActivity()
{
    private lateinit var tvTitle: TextView
    private lateinit var pieContainer: FrameLayout
    private lateinit var btnReturnHome: Button
    private lateinit var ivBackArrow: TextView
    private lateinit var tableRowsContainer: LinearLayout
    private lateinit var username: String

    private val expenseViewModel = ExpenseViewModel()
    private val categoryViewModel = CategoryViewModel()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_total)

        username = intent.getStringExtra("username") ?: "admin"

        tvTitle = findViewById(R.id.tv_title)
        pieContainer = findViewById(R.id.pie_container)
        btnReturnHome = findViewById(R.id.btn_return_home)
        ivBackArrow = findViewById(R.id.iv_back_arrow)
        tableRowsContainer = findViewById(R.id.table_rows_container)

        // Back arrow navigation to return to the Budgets tab
        ivBackArrow.setOnClickListener {
            navigateToBudgetsTab()
        }

        btnReturnHome.setOnClickListener {
            navigateToBudgetsTab()
        }

        loadAndDisplayData()
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
     * Loads expense and category data, then displays the pie chart and totals table.
     */
    private fun loadAndDisplayData()
    {
        val expenses = expenseViewModel.getExpenses(this, username)
        val categories = categoryViewModel.getCategories(this, username)

        val categoryMap: Map<Int, Category> = categories.associateBy { it.id }

        // Group expenses by categoryId and calculate totals
        val totals = expenses.groupBy { it.categoryId }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val grandTotal = totals.values.sum()

        // Build pie data with category names and percentages
        val pieData = totals.map { (categoryId, amount) ->
            val categoryName = categoryMap[categoryId]?.name ?: "General"
            val percentage = if (grandTotal > 0) (amount / grandTotal * 100) else 0.0
            Triple(categoryName, amount, percentage)
        }

        // Create and add the pie chart view
        val pieChart = PieChartView(this, pieData.map { Pair(it.first, it.third) })
        pieContainer.addView(pieChart)

        // Display the totals table
        displayTotalsTable(pieData)
    }

    /**
     * Displays the category totals in a table format matching the mockup.
     * Creates a row for each category showing Name, Amount (ZAR), and Percentage.
     *
     * @param data List of Triples containing category name, amount, and percentage
     */
    private fun displayTotalsTable(data: List<Triple<String, Double, Double>>)
    {
        // Clear existing rows
        tableRowsContainer.removeAllViews()

        if (data.isEmpty())
        {
            val emptyRow = TextView(this)
            emptyRow.text = "No expense data available"
            emptyRow.setTextColor(getColor(R.color.text_secondary_light))
            emptyRow.textSize = 14f
            emptyRow.setPadding(16, 32, 16, 32)
            emptyRow.gravity = android.view.Gravity.CENTER
            tableRowsContainer.addView(emptyRow)
            return
        }

        // Create a row for each category
        for ((categoryName, amount, percentage) in data)
        {
            val rowLayout = LinearLayout(this)
            rowLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            rowLayout.orientation = LinearLayout.HORIZONTAL
            rowLayout.setPadding(0, 12, 0, 12)

            // Category Name Column
            val categoryTextView = TextView(this)
            categoryTextView.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                2f
            )
            categoryTextView.text = categoryName
            categoryTextView.setTextColor(getColor(R.color.text_primary_light))
            categoryTextView.textSize = 14f

            // Amount Column (ZAR currency)
            val amountTextView = TextView(this)
            amountTextView.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            amountTextView.text = String.format(Locale.US, "%.2f", amount)
            amountTextView.setTextColor(getColor(R.color.text_primary_light))
            amountTextView.textSize = 14f
            amountTextView.gravity = android.view.Gravity.END

            // Percentage Column
            val percentageTextView = TextView(this)
            percentageTextView.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            percentageTextView.text = String.format(Locale.US, "(%.1f%%)", percentage)
            percentageTextView.setTextColor(getColor(R.color.text_secondary_light))
            percentageTextView.textSize = 14f
            percentageTextView.gravity = android.view.Gravity.END

            rowLayout.addView(categoryTextView)
            rowLayout.addView(amountTextView)
            rowLayout.addView(percentageTextView)

            tableRowsContainer.addView(rowLayout)
        }
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
        private val percentagePaint: Paint = Paint().apply {
            isAntiAlias = true
            textSize = 20f
            color = Color.BLACK
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

            data.forEachIndexed { index, (name, value) ->
                val sweepAngle = (value / total * 360f - gap).toFloat().coerceAtLeast(0f)
                paint.color = colors[index % colors.size]
                canvas.drawArc(rect, startAngle, sweepAngle, true, paint)

                // Draw percentage label on the segment
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

            // Draw center circle for donut style
            paint.color = Color.WHITE
            canvas.drawCircle(rect.centerX(), rect.centerY(), rect.width() / 5f, paint)

            // Draw legend below the pie chart
            val legendStartY = pieHeight + 60f
            val legendItemHeight = 40f
            val colorSize = 20f

            data.forEachIndexed { index, (name, value) ->
                val y = legendStartY + (index * legendItemHeight)
                legendPaint.color = colors[index % colors.size]
                canvas.drawRect(40f, y, 40f + colorSize, y + colorSize, legendPaint)

                val percentageText = String.format(Locale.US, "%.1f%%", value)
                textPaint.textSize = 24f
                textPaint.color = Color.BLACK
                canvas.drawText("$name: $percentageText", 80f, y + 18f, textPaint)
            }
        }
    }
}