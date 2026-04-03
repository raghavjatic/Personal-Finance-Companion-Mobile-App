package com.example.zorvyn.ui.insights

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.zorvyn.R
import com.example.zorvyn.ui.viewmodel.TransactionViewModel

class InsightsFragment : Fragment(R.layout.fragment_insights) {

    private val viewModel: TransactionViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val tvTotal = view.findViewById<TextView>(R.id.tvTotalTransactions)
        val tvTopCategory = view.findViewById<TextView>(R.id.tvTopCategory)
        val tvInsight = view.findViewById<TextView>(R.id.tvInsight)

        viewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->

            // Total transactions
            tvTotal.text = "Total Transactions: ${transactions.size}"

            // Calculate category spending
            val categoryMap = mutableMapOf<String, Double>()

            for (t in transactions) {
                if (t.type == "expense") {
                    categoryMap[t.category] =
                        categoryMap.getOrDefault(t.category, 0.0) + t.amount
                }
            }

            val topCategory = categoryMap.maxByOrNull { it.value }?.key ?: "-"

            tvTopCategory.text = "Top Spending Category: $topCategory"

            // Simple insight
            val insight = if (transactions.isEmpty()) {
                "Start adding transactions to see insights"
            } else {
                "You're spending most on $topCategory"
            }

            tvInsight.text = "Insight: $insight"
        }
    }
}