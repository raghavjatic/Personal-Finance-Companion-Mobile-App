package com.example.zorvyn.ui.insights

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.zorvyn.R
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.GridLayoutManager
import com.example.zorvyn.data.AppDatabase
import com.example.zorvyn.data.repository.TransactionRepository

class InsightsFragment : Fragment(R.layout.fragment_insights) {

    private lateinit var viewModel: InsightsViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvMonthLabel = view.findViewById<TextView>(R.id.tvMonthLabel)

        val format = java.text.SimpleDateFormat("MMMM yyyy", java.util.Locale.getDefault())
        val currentMonth = format.format(java.util.Date())
        tvMonthLabel.text = currentMonth

        val repository = TransactionRepository(
            AppDatabase.getDatabase(requireContext()).transactionDao()
        )
        val factory = InsightsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[InsightsViewModel::class.java]

        // ===============================
        // 📊 CATEGORY LIST
        // ===============================
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvCategories)
        val categoryAdapter = CategoryAdapter()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = categoryAdapter

        viewModel.categoryTotals.observe(viewLifecycleOwner) {
            categoryAdapter.submitList(it)
        }

        // ===============================
        // 🔹 TEXT + CHART
        // ===============================
        val tvMonthly = view.findViewById<TextView>(R.id.tvMonthlyTotal)
        val tvWeeklyMain = view.findViewById<TextView>(R.id.tvWeeklyMain)
        val tvTop = view.findViewById<TextView>(R.id.tvTopCategory)
        val barChart = view.findViewById<BarChart>(R.id.barChart)
        val tvGraphPercent = view.findViewById<TextView>(R.id.tvGraphPercent)
        val tvGraphSub = view.findViewById<TextView>(R.id.tvGraphSub)

        viewModel.monthlyExpense.observe(viewLifecycleOwner) {
            tvMonthly.text = "₹${it ?: 0}"
        }

        viewModel.topCategory.observe(viewLifecycleOwner) {
            tvTop.text = it?.let { c ->
                "${c.category} — ₹${c.total}"
            } ?: "No data"
        }

        viewModel.weeklyData.observe(viewLifecycleOwner) { data ->

            val thisWeek = data.thisWeek ?: 0.0
            val lastWeek = data.lastWeek ?: 0.0

            // ✅ THIS WEEK CARD → ONLY ₹
            tvWeeklyMain.text = "₹${thisWeek.toInt()}"
            tvWeeklyMain.setTextColor(resources.getColor(R.color.black, null))

            // ✅ GRAPH TEXT → % + COLOR
            if (lastWeek == 0.0) {
                tvGraphPercent.text = "0%"
                tvGraphPercent.setTextColor(resources.getColor(R.color.graphText, null))
                tvGraphSub.text = "No previous data"
            } else {
                val percent = ((thisWeek - lastWeek) / lastWeek) * 100
                val isIncrease = percent >= 0

                val arrow = if (isIncrease) "↑" else "↓"

                val color = if (isIncrease)
                    resources.getColor(R.color.green, null)
                else
                    resources.getColor(R.color.red, null)

                tvGraphPercent.text =
                    "$arrow ${String.format("%.1f", kotlin.math.abs(percent))}%"

                tvGraphPercent.setTextColor(color)
                tvGraphSub.text = "than previous week"
            }
        }

        // 🔥 MODERN WEEKLY GRAPH
        viewModel.weeklyDailyExpenses.observe(viewLifecycleOwner) { dailyValues ->

            val entries = dailyValues.mapIndexed { index, value ->
                BarEntry(index.toFloat(), value)
            }

            val dataSet = BarDataSet(entries, "").apply {
                color = resources.getColor(R.color.graphBar, null)
                setDrawValues(false)
            }

            val barData = BarData(dataSet)
            barData.barWidth = 0.4f

            barChart.data = barData

            // ✅ X Axis (days)
            barChart.xAxis.apply {
                valueFormatter = IndexAxisValueFormatter(
                    listOf("Sun","Mon","Tue","Wed","Thu","Fri","Sat")
                )
                position = XAxis.XAxisPosition.BOTTOM
                granularity = 1f
                setDrawGridLines(false)
                setDrawAxisLine(false)
                textColor = resources.getColor(R.color.graphText, null)
                textSize = 10f
            }

            // ✅ Y Axis (clean + dotted)
            barChart.axisLeft.apply {
                setDrawAxisLine(false)
                setDrawGridLines(true)
                gridColor = resources.getColor(R.color.graphGrid, null)
                enableGridDashedLine(10f, 10f, 0f) // 🔥 dotted lines
                textColor = resources.getColor(R.color.graphText, null)
                textSize = 10f

                val maxValue = dailyValues.maxOrNull() ?: 0f

                barChart.axisLeft.axisMaximum = maxValue * 1.2f  // add headroom
                barChart.axisLeft.axisMinimum = 0f
            }

            barChart.axisRight.isEnabled = false

            // ✅ Clean UI
            barChart.description.isEnabled = false
            barChart.legend.isEnabled = false
            barChart.setTouchEnabled(false)

            // ✅ Animation
            barChart.animateY(800)

            // ✅ Spacing (very important)
            barChart.setFitBars(true)
            barChart.setExtraOffsets(8f, 16f, 8f, 8f)

            barChart.invalidate()
        }

        // ===============================
        // 🔥 HEATMAP
        // ===============================
        val heatmapRv = view.findViewById<RecyclerView>(R.id.rvHeatmap)
        val heatmapAdapter = HeatmapAdapter()

        heatmapRv.layoutManager = GridLayoutManager(requireContext(), 7)
        heatmapRv.adapter = heatmapAdapter

        viewModel.dailyExpenses.observe(viewLifecycleOwner) { data ->
            val heatmapData = viewModel.getMonthlyHeatmapData(data)
            heatmapAdapter.submitList(heatmapData)
        }
    }
}