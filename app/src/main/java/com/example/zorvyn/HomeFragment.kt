package com.example.zorvyn.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.zorvyn.R
import com.example.zorvyn.ui.RecentTransactionAdapter
import com.example.zorvyn.ui.viewmodel.TransactionViewModel
import com.github.mikephil.charting.charts.*
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: TransactionViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        // 🔹 View references
        val tvBalance = view.findViewById<TextView>(R.id.tvBalance)
        val tvIncome = view.findViewById<TextView>(R.id.tvIncome)
        val tvExpense = view.findViewById<TextView>(R.id.tvExpense)
        val pieChart = view.findViewById<PieChart>(R.id.pieChart)
        val incomeChart = view.findViewById<BarChart>(R.id.incomeChart)
        val expenseChart = view.findViewById<BarChart>(R.id.expenseChart)
        val recentContainer = view.findViewById<LinearLayout>(R.id.recentContainer)



        val legendContainer = view.findViewById<android.widget.LinearLayout>(R.id.legendContainer)

        val tvSeeAll = view.findViewById<TextView>(R.id.tvSeeAll)

        tvSeeAll.setOnClickListener {
            requireActivity().findViewById<com.google.android.material.bottomnavigation.BottomNavigationView>(
                R.id.bottom_nav
            ).selectedItemId = R.id.transactionsFragment
        }

        // Toggle views
        val toggle = view.findViewById<com.google.android.material.chip.Chip>(R.id.chipToggleChart)
        val graphContainer = view.findViewById<View>(R.id.graphContainer)

        var isPieVisible = true

        toggle.setOnClickListener {

            if (isPieVisible) {
                pieChart.visibility = View.GONE
                graphContainer.visibility = View.VISIBLE
                toggle.text = "Show Pie Chart"
            } else {
                pieChart.visibility = View.VISIBLE
                graphContainer.visibility = View.GONE
                toggle.text = "Show Bar Chart"
            }

            isPieVisible = !isPieVisible

            // refresh layout properly
            view.post { view.requestLayout() }
        }

        // ViewModel observe
        viewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->

            var income = 0.0
            var expense = 0.0

            val incomeMap = mutableMapOf<String, Double>()
            val expenseMap = mutableMapOf<String, Double>()

            for (t in transactions) {
                if (t.type == "income") {
                    income += t.amount
                    incomeMap[t.category] =
                        incomeMap.getOrDefault(t.category, 0.0) + t.amount
                } else {
                    expense += t.amount
                    expenseMap[t.category] =
                        expenseMap.getOrDefault(t.category, 0.0) + t.amount
                }
            }

            val balance = income - expense

            tvBalance.text = "₹$balance"
            tvIncome.text = "₹$income"
            tvExpense.text = "₹$expense"

            setupPieChart(pieChart, income, expense)
            setupCategoryChart(incomeChart, incomeMap, true)
            setupCategoryChart(expenseChart, expenseMap, false)

            //Recent transactions

            recentContainer.removeAllViews()

            val recent = transactions
                .sortedByDescending { it.date }
                .take(3)

            for (item in recent) {
                val itemView = layoutInflater.inflate(
                    R.layout.item_recent_transaction,
                    recentContainer,
                    false
                )

                val tvIcon = itemView.findViewById<TextView>(R.id.tvIcon)
                val iconContainer = itemView.findViewById<View>(R.id.iconContainer)
                val tvCategory = itemView.findViewById<TextView>(R.id.tvCategory)
                val tvSub = itemView.findViewById<TextView>(R.id.tvSub)
                val tvAmount = itemView.findViewById<TextView>(R.id.tvAmount)

                tvCategory.text = item.category

                val isIncome = item.type == "income"

                val typeText = if (isIncome) "Income" else "Expense"
                val dateText = android.text.format.DateUtils.getRelativeTimeSpanString(item.date)
                tvSub.text = "$typeText · $dateText"

                tvAmount.text =
                    if (isIncome) "+₹${item.amount}" else "-₹${item.amount}"

                tvAmount.setTextColor(
                    if (isIncome) 0xFF4CAF50.toInt() else 0xFFF44336.toInt()
                )

                // UPDATED ICON STYLE
                val emoji = when (item.category.lowercase()) {
                    "food" -> "🍔"
                    "travel" -> "🚗"
                    "shopping" -> "🛍"
                    "salary" -> "💼"
                    "bills" -> "🧾"
                    else -> if (isIncome) "💰" else "💸"
                }

                tvIcon.text = emoji

                val bgColor = when (item.category.lowercase()) {
                    "food" -> "#2E7D32"
                    "travel" -> "#1565C0"
                    "shopping" -> "#6A1B9A"
                    "salary" -> "#37474F"
                    "bills" -> "#EF6C00"
                    else -> "#2A2A2A"
                }

                iconContainer.backgroundTintList =
                    android.content.res.ColorStateList.valueOf(Color.parseColor(bgColor))

                recentContainer.addView(itemView)
            }
        }
    }

    // -------- PIE CHART --------
    private fun setupPieChart(chart: PieChart, income: Double, expense: Double) {

        if (income == 0.0 && expense == 0.0) {
            chart.clear()
            chart.centerText = "No Data"
            return
        }

        val entries = listOf(
            PieEntry(income.toFloat(), "Income"),
            PieEntry(expense.toFloat(), "Expense")
        )

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(
            Color.parseColor("#66BB6A"),
            Color.parseColor("#EF5350")
        )
        dataSet.sliceSpace = 3f

        val data = PieData(dataSet)
        data.setValueFormatter(
            com.github.mikephil.charting.formatter.PercentFormatter(chart)
        )
        data.setValueTextColor(Color.WHITE)
        data.setValueTextSize(12f)

        chart.data = data
        chart.setUsePercentValues(true)

        chart.description.isEnabled = false
        chart.legend.isEnabled = false

        chart.isDrawHoleEnabled = true
        chart.holeRadius = 65f
        chart.setHoleColor(Color.TRANSPARENT)

        chart.centerText = "Overview"
        chart.setDrawEntryLabels(false)

        chart.setTransparentCircleAlpha(0)
        chart.setEntryLabelColor(Color.WHITE)
        chart.setEntryLabelTextSize(12f)

        chart.animateY(900)
        chart.invalidate()

        // Update legend
        val container = requireView().findViewById<android.widget.LinearLayout>(R.id.legendContainer)
        container.removeAllViews()

        addLegendItem(container, Color.parseColor("#66BB6A"), "Income")
        addLegendItem(container, Color.parseColor("#EF5350"), "Expense")
    }

    // -------- CATEGORY BAR CHART --------
    private fun setupCategoryChart(
        chart: BarChart,
        dataMap: Map<String, Double>,
        isIncome: Boolean
    ) {

        if (dataMap.isEmpty()) {
            chart.clear()
            chart.setNoDataText("No Data")
            return
        }

        val entries = mutableListOf<BarEntry>()
        val labels = mutableListOf<String>()

        var index = 0f
        for ((category, amount) in dataMap) {
            entries.add(BarEntry(index, amount.toFloat()))
            labels.add(category)
            index++
        }

        val dataSet = BarDataSet(entries, "")
        dataSet.color = if (isIncome)
            Color.parseColor("#66BB6A")
        else
            Color.parseColor("#EF5350")

        val data = BarData(dataSet)
        data.barWidth = 0.5f

        chart.data = data

        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.axisRight.isEnabled = false

        val xAxis = chart.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(labels)
        xAxis.granularity = 1f
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)

        val leftAxis = chart.axisLeft
        leftAxis.setDrawGridLines(false)
        leftAxis.setDrawAxisLine(false)

        chart.setTouchEnabled(false)
        chart.animateY(900)

        chart.invalidate()
    }


    private fun addLegendItem(container: android.widget.LinearLayout, color: Int, label: String) {

        val view = layoutInflater.inflate(R.layout.item_legend_pill, container, false)

        val dot = view.findViewById<View>(R.id.colorDot)
        val text = view.findViewById<TextView>(R.id.labelText)

        dot.setBackgroundColor(color)
        text.text = label

        container.addView(view)
    }
}