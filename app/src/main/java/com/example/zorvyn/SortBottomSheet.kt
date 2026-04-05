package com.example.zorvyn

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.RadioGroup
import com.example.zorvyn.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class SortBottomSheet(
    private val onSortApplied: (String) -> Unit
) : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottom_sheet_sort, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val rgSort = view.findViewById<RadioGroup>(R.id.rgSort)
        val btnApply = view.findViewById<Button>(R.id.btnApplySort)

        // Default selection
        rgSort.check(R.id.rbDateDesc)

        btnApply.setOnClickListener {

            val sort = when (rgSort.checkedRadioButtonId) {
                R.id.rbDateAsc -> "date_asc"
                R.id.rbAmountHigh -> "amount_desc"
                R.id.rbAmountLow -> "amount_asc"
                else -> "date_desc"
            }

            onSortApplied(sort)
            dismiss()
        }
    }
}