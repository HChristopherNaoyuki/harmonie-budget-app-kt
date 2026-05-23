package com.example.harmonie_budget_app_kt

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.toColorInt
import androidx.lifecycle.lifecycleScope
import com.example.harmonie_budget_app_kt.models.Category
import com.example.harmonie_budget_app_kt.models.Expense
import com.example.harmonie_budget_app_kt.viewmodels.CategoryViewModel
import com.example.harmonie_budget_app_kt.viewmodels.ExpenseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

/**
 * CategoryTotalActivity displays the pie chart and totals table with category breakdown.
 * Supports dynamic data loading, sorting, and responsive layout for small screens.
 */
data class CategoryTotalData(
    val categoryName: String,
    val amount: Double,
    val percentage: Double,
    val color: Int
)

class CategoryTotalActivity : AppCompatActivity()
{
    // UI Components
    private lateinit var btnReturnHome: Button
    private lateinit var ivBackArrow: TextView
    private lateinit var pieContainer: FrameLayout
    private lateinit var tableRowsContainer: LinearLayout
    private lateinit var spinnerSort: Spinner

    // Header click listeners for sorting
    private lateinit var headerCategory: TextView
    private lateinit var headerAmount: TextView
    private lateinit var headerPercentage: TextView

    // Data
    private lateinit var username: String
    private var categoryData: List<CategoryTotalData> = emptyList()
    private var currentSortColumn: String = "category"
    private var isAscending: Boolean = true

    // ViewModels
    private val expenseViewModel = ExpenseViewModel()
    private val categoryViewModel = CategoryViewModel()

    // Colorblind-friendly palette (6 distinct colors)
    private val colorPalette = listOf(
        "#1E88E5".toColorInt(),  // Blue
        "#E53935".toColorInt(),  // Red
        "#43A047".toColorInt(),  // Green
        "#FDD835".toColorInt(),  // Yellow
        "#8E24AA".toColorInt(),  // Purple
        "#FB8C00".toColorInt()   // Orange
    )

    companion object
    {
        private const val SORT_CATEGORY = "category"
        private const val SORT_AMOUNT = "amount"
        private const val SORT_PERCENTAGE = "percentage"
        private const val EMPTY_STATE_MESSAGE = "No data available"
    }

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_total)

        username = intent.getStringExtra("username") ?: "admin"

        // Initialize UI components
        initializeViews()
        setupClickListeners()
        setupSortSpinner()

        // Load and display data
        loadData()
    }

    private fun initializeViews()
    {
        btnReturnHome = findViewById(R.id.btn_return_home)
        ivBackArrow = findViewById(R.id.iv_back_arrow)
        pieContainer = findViewById(R.id.pie_container)
        tableRowsContainer = findViewById(R.id.table_rows_container)
        spinnerSort = findViewById(R.id.spinner_sort)

        headerCategory = findViewById(R.id.header_category)
        headerAmount = findViewById(R.id.header_amount)
        headerPercentage = findViewById(R.id.header_percentage)
    }

    private fun setupClickListeners()
    {
        ivBackArrow.setOnClickListener {
            navigateToBudgetsTab()
        }

        btnReturnHome.setOnClickListener {
            navigateToBudgetsTab()
        }

        // Sort by category when header is clicked
        headerCategory.setOnClickListener {
            currentSortColumn = SORT_CATEGORY
            isAscending = !isAscending
            sortAndDisplayData()
            updateHeaderIndicators()
        }

        // Sort by amount when header is clicked
        headerAmount.setOnClickListener {
            currentSortColumn = SORT_AMOUNT
            isAscending = !isAscending
            sortAndDisplayData()
            updateHeaderIndicators()
        }

        // Sort by percentage when header is clicked
        headerPercentage.setOnClickListener {
            currentSortColumn = SORT_PERCENTAGE
            isAscending = !isAscending
            sortAndDisplayData()
            updateHeaderIndicators()
        }
    }

    private fun setupSortSpinner()
    {
        val sortOptions = arrayOf(
            getString(R.string.sort_category_asc),
            getString(R.string.sort_category_desc),
            getString(R.string.sort_amount_asc),
            getString(R.string.sort_amount_desc),
            getString(R.string.sort_percentage_asc),
            getString(R.string.sort_percentage_desc)
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, sortOptions)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerSort.adapter = adapter

        spinnerSort.onItemSelectedListener = object : AdapterView.OnItemSelectedListener
        {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long)
            {
                when (position)
                {
                    0 -> { currentSortColumn = SORT_CATEGORY; isAscending = true }
                    1 -> { currentSortColumn = SORT_CATEGORY; isAscending = false }
                    2 -> { currentSortColumn = SORT_AMOUNT; isAscending = true }
                    3 -> { currentSortColumn = SORT_AMOUNT; isAscending = false }
                    4 -> { currentSortColumn = SORT_PERCENTAGE; isAscending = true }
                    5 -> { currentSortColumn = SORT_PERCENTAGE; isAscending = false }
                }
                sortAndDisplayData()
                updateHeaderIndicators()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    private fun updateHeaderIndicators()
    {
        // Add visual indicator for active sort column
        headerCategory.text = when (currentSortColumn)
        {
            SORT_CATEGORY -> if (isAscending) "Category ▲" else "Category ▼"
            else -> "Category"
        }

        headerAmount.text = when (currentSortColumn)
        {
            SORT_AMOUNT -> if (isAscending) "Amount ▲" else "Amount ▼"
            else -> "Amount"
        }

        headerPercentage.text = when (currentSortColumn)
        {
            SORT_PERCENTAGE -> if (isAscending) "% ▲" else "% ▼"
            else -> "%"
        }
    }

    private fun navigateToBudgetsTab()
    {
        val intent = Intent(this, DashboardActivity::class.java)
        intent.putExtra("username", username)
        intent.putExtra("selected_tab", R.id.nav_budgets)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun loadData()
    {
        // Correct coroutine syntax: opening brace on same line as launch
        lifecycleScope.launch {
            val (expenses, categories) = withContext(Dispatchers.IO) {
                Pair(
                    expenseViewModel.getExpenses(applicationContext, username),
                    categoryViewModel.getCategories(applicationContext, username)
                )
            }

            withContext(Dispatchers.Main) {
                processAndDisplayData(expenses, categories)
            }
        }
    }

    private fun processAndDisplayData(expenses: List<Expense>, categories: List<Category>)
    {
        val categoryMap: Map<Int, Category> = categories.associateBy { it.id }

        // Group expenses by categoryId and calculate totals
        val totals = expenses.groupBy { it.categoryId }
            .mapValues { entry -> entry.value.sumOf { it.amount } }

        val grandTotal = totals.values.sum()

        // Build category data with percentages
        val rawData = totals.map { (categoryId, amount) ->
            val categoryName = categoryMap[categoryId]?.name ?: getString(R.string.default_category_name)
            val percentage = if (grandTotal > 0) (amount / grandTotal * 100) else 0.0
            CategoryTotalData(categoryName, amount, percentage, 0)
        }.sortedByDescending { it.amount }

        // Group negligible categories (less than 0.5%) into "Other"
        val mainCategories = rawData.filter { it.percentage >= 0.5 }
        val otherCategories = rawData.filter { it.percentage < 0.5 }

        val categoryDataList = if (otherCategories.isNotEmpty())
        {
            val otherTotal = otherCategories.sumOf { it.amount }
            val otherPercentage = otherCategories.sumOf { it.percentage }
            val otherData = CategoryTotalData("Other", otherTotal, otherPercentage, 0)
            (mainCategories + otherData).sortedByDescending { it.amount }
        }
        else
        {
            mainCategories
        }

        // Assign colors to each category
        categoryData = categoryDataList.mapIndexed { index, data ->
            data.copy(color = colorPalette[index % colorPalette.size])
        }

        // Sort and display
        sortAndDisplayData()
    }

    private fun sortAndDisplayData()
    {
        val sortedData = when (currentSortColumn)
        {
            SORT_CATEGORY -> if (isAscending) categoryData.sortedBy { it.categoryName.lowercase() }
            else categoryData.sortedByDescending { it.categoryName.lowercase() }
            SORT_AMOUNT -> if (isAscending) categoryData.sortedBy { it.amount }
            else categoryData.sortedByDescending { it.amount }
            SORT_PERCENTAGE -> if (isAscending) categoryData.sortedBy { it.percentage }
            else categoryData.sortedByDescending { it.percentage }
            else -> categoryData
        }

        displayPieChart(sortedData)
        displayTotalsTable(sortedData)
    }

    private fun displayPieChart(data: List<CategoryTotalData>)
    {
        pieContainer.removeAllViews()

        if (data.isEmpty() || data.sumOf { it.amount } == 0.0)
        {
            showEmptyState(EMPTY_STATE_MESSAGE)
            return
        }

        val pieChart = PieChartView(this, data)
        pieContainer.addView(pieChart)
    }

    private fun displayTotalsTable(data: List<CategoryTotalData>)
    {
        tableRowsContainer.removeAllViews()

        if (data.isEmpty())
        {
            val emptyRow = TextView(this)
            emptyRow.text = getString(R.string.no_expense_data_available)
            emptyRow.setTextColor(getColor(R.color.text_secondary_light))
            emptyRow.textSize = 14f
            emptyRow.setPadding(16, 32, 16, 32)
            emptyRow.gravity = Gravity.CENTER
            tableRowsContainer.addView(emptyRow)
            return
        }

        for ((index, categoryInfo) in data.withIndex())
        {
            val rowLayout = LinearLayout(this)
            rowLayout.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            rowLayout.orientation = LinearLayout.HORIZONTAL
            rowLayout.setPadding(0, 12, 0, 12)

            // Alternating background colors for better readability
            if (index % 2 == 1)
            {
                rowLayout.setBackgroundColor(getColor(R.color.background_light))
            }

            // Category Name with color indicator
            val categoryLayout = LinearLayout(this)
            categoryLayout.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                2f
            )
            categoryLayout.orientation = LinearLayout.HORIZONTAL
            categoryLayout.gravity = Gravity.CENTER_VERTICAL

            // Color indicator circle
            val colorView = View(this)
            colorView.layoutParams = LinearLayout.LayoutParams(16, 16).apply {
                setMargins(0, 0, 8, 0)
            }
            colorView.setBackgroundColor(categoryInfo.color)

            val categoryTextView = TextView(this)
            categoryTextView.text = categoryInfo.categoryName
            categoryTextView.setTextColor(getColor(R.color.text_primary_light))
            categoryTextView.textSize = 14f

            categoryLayout.addView(colorView)
            categoryLayout.addView(categoryTextView)

            // Amount
            val amountTextView = TextView(this)
            amountTextView.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            amountTextView.text = String.format(Locale.US, "R %,.2f", categoryInfo.amount)
            amountTextView.setTextColor(getColor(R.color.text_primary_light))
            amountTextView.textSize = 14f
            amountTextView.gravity = Gravity.END

            // Percentage
            val percentageTextView = TextView(this)
            percentageTextView.layoutParams = LinearLayout.LayoutParams(
                0,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                1f
            )
            percentageTextView.text = String.format(Locale.US, "%.1f%%", categoryInfo.percentage)
            percentageTextView.setTextColor(getColor(R.color.text_secondary_light))
            percentageTextView.textSize = 14f
            percentageTextView.gravity = Gravity.END

            rowLayout.addView(categoryLayout)
            rowLayout.addView(amountTextView)
            rowLayout.addView(percentageTextView)

            // Click listener to highlight corresponding pie slice
            val position = index
            rowLayout.setOnClickListener {
                highlightPieSlice(position)
                Toast.makeText(
                    this,
                    "${categoryInfo.categoryName}: R ${String.format(Locale.US, "%,.2f", categoryInfo.amount)} (${String.format(Locale.US, "%.1f", categoryInfo.percentage)}%)",
                    Toast.LENGTH_SHORT
                ).show()
            }

            tableRowsContainer.addView(rowLayout)
        }
    }

    private fun highlightPieSlice(index: Int)
    {
        // Recreate the pie chart with highlighting
        pieContainer.removeAllViews()
        val pieChart = PieChartView(this, categoryData, highlightedSliceIndex = index)
        pieContainer.addView(pieChart)
    }

    private fun showEmptyState(message: String)
    {
        pieContainer.removeAllViews()
        val emptyText = TextView(this)
        emptyText.text = message
        emptyText.setTextColor(getColor(R.color.text_secondary_light))
        emptyText.textSize = 16f
        emptyText.gravity = Gravity.CENTER
        emptyText.setPadding(16, 64, 16, 64)
        pieContainer.addView(emptyText)
    }

    /**
     * Custom PieChartView that draws the pie chart with legend and selection highlighting.
     */
    private class PieChartView(
        context: Context,
        private val data: List<CategoryTotalData>,
        private val highlightedSliceIndex: Int = -1
    ) : View(context)
    {
        private val paint: Paint = Paint().apply { isAntiAlias = true }
        private val highlightPaint: Paint = Paint().apply {
            isAntiAlias = true
            strokeWidth = 4f
            style = Paint.Style.STROKE
            color = Color.BLACK
        }
        private val rect: RectF = RectF()
        private val legendPaint: Paint = Paint().apply { isAntiAlias = true }
        private val textPaint: Paint = Paint().apply {
            isAntiAlias = true
            textSize = 28f
            color = Color.BLACK
        }
        private val labelPaint: Paint = Paint().apply {
            isAntiAlias = true
            textSize = 22f
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
        }

        override fun onDraw(canvas: Canvas)
        {
            super.onDraw(canvas)

            if (data.isEmpty())
            {
                return
            }

            val total = data.sumOf { it.amount }
            if (total == 0.0)
            {
                return
            }

            // Calculate pie chart size (responsive)
            val pieSize = (width * 0.6f).toInt().coerceAtMost(height - 200)
            val left = (width - pieSize) / 2f
            val top = 40f
            rect.set(left, top, left + pieSize, top + pieSize)

            var startAngle = 0f

            // Draw each pie slice
            data.forEachIndexed { index, categoryInfo ->
                val sweepAngle = (categoryInfo.amount / total * 360f).toFloat()
                paint.color = categoryInfo.color

                canvas.drawArc(rect, startAngle, sweepAngle, true, paint)

                // Draw highlight border if this slice is selected
                if (index == highlightedSliceIndex)
                {
                    canvas.drawArc(rect, startAngle, sweepAngle, true, highlightPaint)
                }

                // Draw percentage label on slice if large enough
                if (sweepAngle > 15f)
                {
                    val midAngle = Math.toRadians((startAngle + sweepAngle / 2).toDouble())
                    val radius = rect.width() / 3f
                    val labelX = rect.centerX() + (radius * kotlin.math.cos(midAngle)).toFloat()
                    val labelY = rect.centerY() + (radius * kotlin.math.sin(midAngle)).toFloat()
                    val percentageText = String.format(Locale.US, "%.0f%%", categoryInfo.percentage)

                    // Ensure text is readable against the slice color
                    val isDarkSlice = isColorDark(categoryInfo.color)
                    labelPaint.color = if (isDarkSlice) Color.WHITE else Color.BLACK
                    canvas.drawText(percentageText, labelX, labelY + 8f, labelPaint)
                }

                startAngle += sweepAngle
            }

            // Draw legend below the pie chart
            val legendStartY = pieSize + 80f
            val legendItemHeight = 36f
            val colorSize = 20f
            val maxLegendItems = 6

            data.take(maxLegendItems).forEachIndexed { index, categoryInfo ->
                val y = legendStartY + (index * legendItemHeight)
                legendPaint.color = categoryInfo.color
                canvas.drawRect(40f, y, 40f + colorSize, y + colorSize, legendPaint)

                val legendText = "${categoryInfo.categoryName}: ${String.format(Locale.US, "%.1f", categoryInfo.percentage)}%"
                textPaint.textSize = 22f
                textPaint.color = Color.BLACK
                canvas.drawText(legendText, 80f, y + 18f, textPaint)
            }

            // Show "Other" categories note if any were grouped
            if (data.any { it.categoryName == "Other" })
            {
                val otherText = "Note: Categories with less than 0.5% are grouped as 'Other'"
                textPaint.textSize = 16f
                textPaint.color = Color.GRAY
                canvas.drawText(otherText, 40f, legendStartY + (maxLegendItems * legendItemHeight) + 20f, textPaint)
            }
        }

        private fun isColorDark(color: Int): Boolean
        {
            val red = (color shr 16 and 0xFF)
            val green = (color shr 8 and 0xFF)
            val blue = (color and 0xFF)
            val brightness = (red * 0.299 + green * 0.587 + blue * 0.114)
            return brightness < 128
        }
    }
}