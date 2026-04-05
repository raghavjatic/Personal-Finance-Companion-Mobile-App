package com.example.zorvyn.ui.insights

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.zorvyn.R
import com.example.zorvyn.ui.insights.model.CategoryTotal

class CategoryAdapter : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var list: List<CategoryTotal> = emptyList()
    private var totalAmount: Double = 0.0

    fun submitList(newList: List<CategoryTotal>) {
        list = newList
        totalAmount = newList.sumOf { it.total }
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvCategory: TextView = view.findViewById(R.id.tvCategory)
        val tvAmount: TextView = view.findViewById(R.id.tvAmount)
        val progressBar: ProgressBar = view.findViewById(R.id.progressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_category, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = list[position]

        holder.tvCategory.text = item.category
        holder.tvAmount.text = "₹${item.total}"

        val percent = if (totalAmount == 0.0) 0
        else ((item.total / totalAmount) * 100).toInt()

        holder.progressBar.progress = percent
    }
}