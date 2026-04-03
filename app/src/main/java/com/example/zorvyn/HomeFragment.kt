package com.example.zorvyn.ui.home

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.zorvyn.R
import com.example.zorvyn.ui.viewmodel.TransactionViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    private val viewModel: TransactionViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val tvBalance = view.findViewById<TextView>(R.id.tvBalance)
        val tvIncome = view.findViewById<TextView>(R.id.tvIncome)
        val tvExpense = view.findViewById<TextView>(R.id.tvExpense)
        val pieChart = view.findViewById<PieChart>(R.id.pieChart)

        viewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->

            var income = 0.0
            var expense = 0.0

            for (t in transactions) {
                if (t.type == "income") income += t.amount
                else expense += t.amount
            }

            val balance = income - expense

            tvBalance.text = "₹$balance"
            tvIncome.text = "Income: ₹$income"
            tvExpense.text = "Expense: ₹$expense"

            setupPieChart(pieChart, income, expense)
        }
    }

    private fun setupPieChart(chart: PieChart, income: Double, expense: Double) {

        val entries = listOf(
            PieEntry(income.toFloat(), "Income"),
            PieEntry(expense.toFloat(), "Expense")
        )

        val dataSet = PieDataSet(entries, "")
        dataSet.colors = listOf(
            android.graphics.Color.GREEN,
            android.graphics.Color.RED
        )

        val data = PieData(dataSet)
        chart.data = data
        chart.invalidate()
    }
}