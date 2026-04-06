package com.example.zorvyn.ui.insights

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
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

    // 🔥 CORRECT WEEKLY GRAPH DATA
    val weeklyDailyExpenses: LiveData<List<Float>> =
        repository.getWeeklyDailyExpenses().map { list ->

            val result = MutableList(7) { 0f }

            val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
            val calendar = java.util.Calendar.getInstance()

            for (item in list) {
                try {
                    val date = sdf.parse(item.date) ?: continue
                    calendar.time = date

                    val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)

                    val index = when (dayOfWeek) {
                        java.util.Calendar.SUNDAY -> 0
                        java.util.Calendar.MONDAY -> 1
                        java.util.Calendar.TUESDAY -> 2
                        java.util.Calendar.WEDNESDAY -> 3
                        java.util.Calendar.THURSDAY -> 4
                        java.util.Calendar.FRIDAY -> 5
                        java.util.Calendar.SATURDAY -> 6
                        else -> continue
                    }

                    result[index] += item.total.toFloat()

                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            result
        }

    // 🔥 HEATMAP LOGIC (unchanged)
    fun getMonthlyHeatmapData(data: List<DailyExpense>): List<Double> {

        val map = data.associate { it.date to it.total }

        val result = mutableListOf<Double>()

        val calendar = java.util.Calendar.getInstance()

        calendar.set(java.util.Calendar.DAY_OF_MONTH, 1)

        val firstDayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)

        val offset = if (firstDayOfWeek == 1) 6 else firstDayOfWeek - 2

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
    }
}