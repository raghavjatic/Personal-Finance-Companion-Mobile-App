package com.example.zorvyn.ui.insights

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.zorvyn.data.repository.TransactionRepository
import com.example.zorvyn.ui.insights.model.CategoryTotal
import com.example.zorvyn.ui.insights.model.DailyExpense
import com.example.zorvyn.ui.insights.model.WeeklyData

class InsightsViewModel(private val repository: TransactionRepository) : ViewModel() {

    val monthlyExpense: LiveData<Double?> = repository.getMonthlyExpense()

    val topCategory: LiveData<CategoryTotal?> = repository.getTopCategory()

    val weeklyData: LiveData<WeeklyData> = repository.getWeeklyComparison()
    val categoryTotals = repository.getCategoryTotals()

    val dailyExpenses = repository.getDailyExpenses()

    fun getMonthlyHeatmapData(data: List<DailyExpense>): List<Double> {

        val map = data.associate { it.date to it.total }

        val result = mutableListOf<Double>()

        val calendar = java.util.Calendar.getInstance()

        // Set to first day of month
        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)

        val firstDayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)

        // Convert Sunday=1 → Monday=1 system
        val offset = if (firstDayOfWeek == 1) 6 else firstDayOfWeek - 2

        // 🔹 Add empty cells before month starts
        repeat(offset) {
            result.add(0.0)
        }

        val daysInMonth = calendar.getActualMaximum(java.util.Calendar.DAY_OF_MONTH)

        val format = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())

        for (day in 1..daysInMonth) {
            calendar.set(java.util.Calendar.DAY_OF_MONTH, day)
            val dateStr = format.format(calendar.time)

            result.add(map[dateStr] ?: 0.0)
        }

        return result
    }}