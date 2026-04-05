package com.example.zorvyn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import com.example.zorvyn.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FilterBottomSheet(
    private val onFilterApplied: (String, String) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottom_sheet_filter, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val rgType = view.findViewById<RadioGroup>(R.id.rgFilterType)
        val rgTime = view.findViewById<RadioGroup>(R.id.rgTime)
        val btnApply = view.findViewById<Button>(R.id.btnApply)

        btnApply.setOnClickListener {

            val type = when (rgType.checkedRadioButtonId) {
                R.id.rbIncome -> "income"
                R.id.rbExpense -> "expense"
                else -> "all"
            }

            val time = when (rgTime.checkedRadioButtonId) {
                R.id.rbToday -> "today"
                R.id.rbMonth -> "month"
                else -> "all"
            }

            onFilterApplied(type, time)
            dismiss()
        }
    }
}