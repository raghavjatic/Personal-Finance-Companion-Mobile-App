package com.example.zorvyn.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.viewModels
import androidx.fragment.app.activityViewModels
import com.example.zorvyn.R
import com.example.zorvyn.data.entity.TransactionEntity
import com.example.zorvyn.ui.viewmodel.TransactionViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddTransactionBottomSheet : BottomSheetDialogFragment() {

    private val viewModel: TransactionViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.bottom_sheet_transaction, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val etAmount = view.findViewById<EditText>(R.id.etAmount)
        val rgType = view.findViewById<RadioGroup>(R.id.rgType)
        val rbIncome = view.findViewById<RadioButton>(R.id.rbIncome)
        val spCategory = view.findViewById<Spinner>(R.id.spCategory)
        val etNotes = view.findViewById<EditText>(R.id.etNotes)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        // Category list
        val categories = listOf("Food", "Travel", "Shopping", "Bills")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, categories)
        spCategory.adapter = adapter

        btnSave.setOnClickListener {

            val amountText = etAmount.text.toString()
            if (amountText.isEmpty()) {
                etAmount.error = "Enter amount"
                return@setOnClickListener
            }

            val amount = amountText.toDouble()
            val type = if (rbIncome.isChecked) "income" else "expense"
            val category = spCategory.selectedItem.toString()
            val notes = etNotes.text.toString()
            val date = System.currentTimeMillis()

            val transaction = TransactionEntity(
                amount = amount,
                type = type,
                category = category,
                date = date,
                notes = notes
            )

            viewModel.insert(transaction)

            dismiss()
        }
    }
}