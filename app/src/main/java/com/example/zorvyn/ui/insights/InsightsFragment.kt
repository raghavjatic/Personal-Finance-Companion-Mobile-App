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

        // ✅ ViewModel setup (CORRECT WAY)
        val repository = TransactionRepository(
            AppDatabase.getDatabase(requireContext()).transactionDao()
        )
        val factory = InsightsViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[InsightsViewModel::class.java]

        // ===============================
        // 📊 CATEGORY LIST (RecyclerView)
        // ===============================
        val recyclerView = view.findViewById<RecyclerView>(R.id.rvCategories)
        val categoryAdapter = CategoryAdapter()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = categoryAdapter

        viewModel.categoryTotals.observe(viewLifecycleOwner) {
            categoryAdapter.submitList(it)
        }

        // ===============================
        // 🔹 TEXT + CHART VIEWS
        // ===============================
        val tvMonthly = view.findViewById<TextView>(R.id.tvMonthlyTotal)
        val tvWeekly = view.findViewById<TextView>(R.id.tvWeeklyComparison)
        val tvTop = view.findViewById<TextView>(R.id.tvTopCategory)
        val barChart = view.findViewById<BarChart>(R.id.barChart)

        // ✅ Monthly
        viewModel.monthlyExpense.observe(viewLifecycleOwner) { amount ->
            tvMonthly.text = "₹${amount ?: 0}"
        }

        // ✅ Top Category
        viewModel.topCategory.observe(viewLifecycleOwner) { category ->
            tvTop.text = category?.let {
                "${it.category} — ₹${it.total}"
            } ?: "No data"
        }

        // ===============================
        // 📊 WEEKLY (TEXT + BAR CHART)
        // ===============================
        viewModel.weeklyData.observe(viewLifecycleOwner) { data ->

            val thisWeek = data.thisWeek ?: 0.0
            val lastWeek = data.lastWeek ?: 0.0

            // 🔹 TEXT
            if (lastWeek == 0.0) {
                tvWeekly.text = "No previous data"
            } else {
                val percent = ((thisWeek - lastWeek) / lastWeek) * 100
                val arrow = if (percent >= 0) "↑" else "↓"

                tvWeekly.text =
                    "$arrow ${"%.1f".format(kotlin.math.abs(percent))}% vs last week"
            }

            // 🔹 CHART
            val entries = listOf(
                BarEntry(0f, lastWeek.toFloat()),
                BarEntry(1f, thisWeek.toFloat())
            )

            val dataSet = BarDataSet(entries, "Weekly Spending")
            val barData = BarData(dataSet)

            barChart.data = barData

            barChart.xAxis.valueFormatter =
                IndexAxisValueFormatter(listOf("Last", "This"))
            barChart.xAxis.granularity = 1f
            barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM

            barChart.axisRight.isEnabled = false
            barChart.description.isEnabled = false

            barChart.invalidate()
        }

        // ===============================
        // 🔥 HEATMAP
        // ===============================
        val heatmapRv = view.findViewById<RecyclerView>(R.id.rvHeatmap)
        val heatmapAdapter = HeatmapAdapter()

        heatmapRv.layoutManager = GridLayoutManager(requireContext(), 7)
        heatmapRv.adapter = heatmapAdapter

        viewModel.dailyExpenses.observe(viewLifecycleOwner) {
            heatmapAdapter.submitList(it)
        }
    }
}