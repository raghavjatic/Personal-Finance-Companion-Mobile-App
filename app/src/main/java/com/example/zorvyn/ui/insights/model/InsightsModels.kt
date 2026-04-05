package com.example.zorvyn.ui.insights.model
data class CategoryTotal(
    val category: String,
    val total: Double
)

data class WeeklyData(
    val thisWeek: Double? = 0.0,
    val lastWeek: Double? = 0.0
)