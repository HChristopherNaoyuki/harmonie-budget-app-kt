package com.example.harmonie_budget_app_kt

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.core.graphics.toColorInt
import androidx.fragment.app.Fragment
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.viewmodels.CategoryViewModel
import com.example.harmonie_budget_app_kt.viewmodels.ExpenseViewModel
import java.util.Locale

/**
 * BudgetsFragment displays the spending overview for the user.
 * It includes a custom PieChartView to visualize spending by category.
 *
 * Part 3 Enhancement:
 * - Pie chart and labels are loaded immediately when the fragment is created.
 * - The View Category Totals button remains at the bottom of the screen.
 * - The pie chart is displayed without requiring additional user interaction.
 */
class BudgetsFragment : Fragment()
{
    private lateinit var username: String
    private lateinit var pieContainer: FrameLayout
    private lateinit var tvTotals: TextView
    private lateinit var btnViewTotals: Button

    private val expenseViewModel = ExpenseViewModel()
    private val categoryViewModel = CategoryViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View?
    {
        val view = inflater.inflate(R.layout.fragment_budgets, container, false)

        // Username is received from DashboardActivity arguments
        username = arguments?.getString("username") ?: "admin"

        pieContainer = view.findViewById(R.id.pie_container)
        tvTotals = view.findViewById(R.id.tv_totals)
        btnViewTotals = view.findViewById(R.id.btn_view_totals)

        // Start CategoryTotalActivity and pass the username
        btnViewTotals.setOnClickListener {
            val intent = Intent(requireContext(), CategoryTotalActivity::class.java)
            intent.putExtra("username", username)
            startActivity(intent)
        }

        return view
    }

    override fun onResume()
    {
        super.onResume()
        // Part 3 Enhancement: Load and display pie chart immediately when fragment is visible.
        loadAndDisplayPieChart()
    }

    /**
     * Loads expense and category data and displays the pie chart.
     * This method is called onResume to ensure the chart is visible when the tab is opened.
     */
    private fun loadAndDisplayPieChart()
    {
        // Load expenses and categories through ViewModel layer
        val expenses = expenseViewModel.getExpenses(requireContext(), username)
        val categories = categoryViewModel.getCategories(requireContext(), username)

        // Map categoryId to name for meaningful labels
        val categoryMap: Map<Int, Category> = categories.associateBy { it.id }

        // Group expenses by categoryId and calculate totals
        val totals = expenses.groupBy { it.categoryId }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        // Calculate grand total for percentage computation
        val grandTotal = totals.values.sum()

        // Build pie data with category names
        val pieData = totals.map { (categoryId, amount) ->
            val categoryName = categoryMap[categoryId]?.name ?: "Category $categoryId"
            val percentage = if (grandTotal > 0) (amount / grandTotal * 100) else 0.0
            Pair(categoryName, percentage)
        }

        // Clear existing views and add the updated pie chart
        pieContainer.removeAllViews()
        val pieChart = PieChartView(requireContext(), pieData)
        pieContainer.addView(pieChart)

        // Update the totals text display
        val builder = StringBuilder()
        for ((categoryId, total) in totals)
        {
            val categoryName = categoryMap[categoryId]?.name ?: "Category $categoryId"
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
     * Custom PieChartView that draws the pie chart and a legend.
     * This is a simplified version for display within the fragment.
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

            val pieSize = minOf(width, height - 150)
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

            val legendStartY = height - 100f
            val legendItemHeight = 40f
            val colorSize = 24f

            data.take(4).forEachIndexed { index, (name, value) ->
                val y = legendStartY + (index * legendItemHeight)
                legendPaint.color = colors[index % colors.size]
                canvas.drawRect(40f, y, 40f + colorSize, y + colorSize, legendPaint)

                val percentageText = String.format(Locale.US, "%.1f%%", value)
                textPaint.textSize = 24f
                textPaint.color = Color.BLACK
                canvas.drawText("$name: $percentageText", 80f, y + 20f, textPaint)
            }
        }
    }

    companion object
    {
        fun newInstance(username: String): BudgetsFragment
        {
            val fragment = BudgetsFragment()
            val args = Bundle()
            args.putString("username", username)
            fragment.arguments = args
            return fragment
        }
    }
}