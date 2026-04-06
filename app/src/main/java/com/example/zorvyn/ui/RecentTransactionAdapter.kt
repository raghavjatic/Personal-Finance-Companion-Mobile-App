package com.example.zorvyn.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zorvyn.R
import com.example.zorvyn.data.entity.TransactionEntity
class RecentTransactionAdapter : RecyclerView.Adapter<RecentTransactionAdapter.VH>() {

    private var list: List<TransactionEntity> = emptyList()

    fun submitList(newList: List<TransactionEntity>) {
        list = newList
        notifyDataSetChanged()
    }

    class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvIcon: TextView = view.findViewById(R.id.tvIcon)
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvSub: TextView = view.findViewById(R.id.tvSub)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recent_transaction, parent, false)
        return VH(view)
    }

    override fun getItemCount() = list.size

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = list[position]

        holder.tvCategory.text = item.category
        val typeText = if (item.type == "income") "Income" else "Expense"
        val dateText = android.text.format.DateUtils.getRelativeTimeSpanString(item.date)

        holder.tvSub.text = "$typeText · $dateText"

        val isIncome = item.type == "income"

        holder.tvAmount.text =
            if (isIncome) "+₹${item.amount}" else "-₹${item.amount}"

        holder.tvAmount.setTextColor(
            if (isIncome) 0xFF4CAF50.toInt() else 0xFFF44336.toInt()
        )

        holder.tvIcon.text = if (isIncome) "💰" else "💸"
    }
}