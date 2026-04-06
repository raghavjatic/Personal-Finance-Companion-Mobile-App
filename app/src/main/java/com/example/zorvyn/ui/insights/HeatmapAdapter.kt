package com.example.zorvyn.ui.insights

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.zorvyn.R

class HeatmapAdapter : RecyclerView.Adapter<HeatmapAdapter.ViewHolder>() {

    private var list: List<Double> = emptyList()
    private var max = 0.0

    fun submitList(newList: List<Double>) {
        list = newList
        max = newList.maxOrNull() ?: 0.0
        notifyDataSetChanged()
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val box: View = view
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_heatmap, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val value = list[position]

        val intensity = if (max == 0.0) 0f else (value / max).toFloat()

        val color = when {
            intensity == 0f -> 0xFFE0E0E0.toInt()   // empty day
            intensity < 0.25 -> 0xFFC8E6C9.toInt()
            intensity < 0.5 -> 0xFF81C784.toInt()
            intensity < 0.75 -> 0xFF4CAF50.toInt()
            else -> 0xFF2E7D32.toInt()
        }

        val drawable = holder.box.background as android.graphics.drawable.GradientDrawable
        drawable.setColor(color)
    }
}