package com.example.zorvyn.ui.insights

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.zorvyn.data.repository.TransactionRepository
import com.example.zorvyn.ui.insights.model.CategoryTotal
import com.example.zorvyn.ui.insights.model.WeeklyData

class InsightsViewModel(private val repository: TransactionRepository) : ViewModel() {

    val monthlyExpense: LiveData<Double?> = repository.getMonthlyExpense()

    val topCategory: LiveData<CategoryTotal?> = repository.getTopCategory()

    val weeklyData: LiveData<WeeklyData> = repository.getWeeklyComparison()
    val categoryTotals = repository.getCategoryTotals()

    val dailyExpenses = repository.getDailyExpenses()
}