package com.example.zorvyn.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.zorvyn.R
import com.example.zorvyn.data.entity.TransactionEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionAdapter(
    private var transactions: List<TransactionEntity> = emptyList(),
    private val onItemClick: (TransactionEntity) -> Unit = {}
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    inner class TransactionViewHolder(itemView: android.view.View) :
        RecyclerView.ViewHolder(itemView) {

        private val tvCategory: TextView = itemView.findViewById(R.id.tv_category)
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        private val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)

        fun bind(transaction: TransactionEntity) {

            tvCategory.text = transaction.category

            // Format date
            val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            tvDate.text = dateFormat.format(Date(transaction.date))

            // Format amount
            tvAmount.text = "₹${transaction.amount}"

            // Set color based on type
            val color = if (transaction.type.lowercase() == "income") {
                ContextCompat.getColor(itemView.context, android.R.color.holo_green_dark)
            } else {
                ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark)
            }

            tvAmount.setTextColor(color)

            // Click listener
            itemView.setOnClickListener {
                onItemClick(transaction)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(transactions[position])
    }

    override fun getItemCount(): Int = transactions.size
    val currentList: List<TransactionEntity>
        get() = transactions

    fun updateTransactions(newTransactions: List<TransactionEntity>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }
}