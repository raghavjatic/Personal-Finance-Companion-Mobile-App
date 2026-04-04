package com.example.zorvyn.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.zorvyn.R
import com.example.zorvyn.ui.viewmodel.TransactionViewModel
import com.github.mikephil.charting.charts.*
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter

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

        // NEW toggle views
        val toggle = view.findViewById<TextView>(R.id.tvToggleGraphs)
        val graphContainer = view.findViewById<View>(R.id.graphContainer)

        var expanded = false

        // TOGGLE LOGIC
        toggle.setOnClickListener {
            expanded = !expanded

            if (expanded) {
                // Show graphs, hide pie
                pieChart.visibility = View.GONE
                graphContainer.visibility = View.VISIBLE

                graphContainer.alpha = 0f
                graphContainer.animate().alpha(1f).setDuration(200).start()

                toggle.text = "Back to Overview ↑"

            } else {
                // Show pie, hide graphs
                pieChart.visibility = View.VISIBLE
                graphContainer.visibility = View.GONE

                toggle.text = "See Graphs →"
            }
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

        chart.animateY(900)
        chart.invalidate()
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
}