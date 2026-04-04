package com.example.zorvyn.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.zorvyn.R
import com.example.zorvyn.data.entity.TransactionEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionAdapter(
    private val onItemClick: (TransactionEntity) -> Unit = {}
) : ListAdapter<TransactionEntity, TransactionAdapter.TransactionViewHolder>(DIFF_CALLBACK) {

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<TransactionEntity>() {
            override fun areItemsTheSame(
                oldItem: TransactionEntity,
                newItem: TransactionEntity
            ): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(
                oldItem: TransactionEntity,
                newItem: TransactionEntity
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class TransactionViewHolder(parent: ViewGroup) :
        RecyclerView.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_transaction, parent, false)
        ) {

        private val tvCategory: TextView = itemView.findViewById(R.id.tv_category)
        private val tvDate: TextView = itemView.findViewById(R.id.tv_date)
        private val tvAmount: TextView = itemView.findViewById(R.id.tv_amount)

        fun bind(transaction: TransactionEntity) {

            tvCategory.text = transaction.category

            val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
            tvDate.text = dateFormat.format(Date(transaction.date))

            tvAmount.text = "₹${transaction.amount}"

            val color = if (transaction.type.lowercase() == "income") {
                ContextCompat.getColor(itemView.context, android.R.color.holo_green_dark)
            } else {
                ContextCompat.getColor(itemView.context, android.R.color.holo_red_dark)
            }

            tvAmount.setTextColor(color)

            itemView.setOnClickListener {
                onItemClick(transaction)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        return TransactionViewHolder(parent)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}