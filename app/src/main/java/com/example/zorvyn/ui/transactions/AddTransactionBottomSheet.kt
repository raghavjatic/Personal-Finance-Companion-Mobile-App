package com.example.zorvyn.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
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
        val rbExpense = view.findViewById<RadioButton>(R.id.rbExpense)
        val spCategory = view.findViewById<Spinner>(R.id.spCategory)
        val etCustomCategory = view.findViewById<EditText>(R.id.etCustomCategory)
        val etNotes = view.findViewById<EditText>(R.id.etNotes)
        val btnSave = view.findViewById<Button>(R.id.btnSave)

        // Category lists
        val expenseCategories = listOf("Food", "Travel", "Shopping", "Bills", "Other")
        val incomeCategories = listOf("Salary", "Pocket Money", "Other")

        // Default → Expense selected
        rbExpense.isChecked = true

        var currentCategories = expenseCategories

        var adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_dropdown_item,
            currentCategories
        )
        spCategory.adapter = adapter

        // Switch categories based on type
        rgType.setOnCheckedChangeListener { _, checkedId ->

            currentCategories = if (checkedId == R.id.rbIncome) {
                incomeCategories
            } else {
                expenseCategories
            }

            adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                currentCategories
            )
            spCategory.adapter = adapter

            etCustomCategory.visibility = View.GONE
        }

        // Handle "Other"
        spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = parent.getItemAtPosition(position).toString()

                if (selected == "Other") {
                    etCustomCategory.visibility = View.VISIBLE
                } else {
                    etCustomCategory.visibility = View.GONE
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // Save
        btnSave.setOnClickListener {

            val amountText = etAmount.text.toString()
            if (amountText.isEmpty()) {
                etAmount.error = "Enter amount"
                return@setOnClickListener
            }

            val amount = amountText.toDouble()
            val type = if (rbIncome.isChecked) "income" else "expense"

            val selectedCategory = spCategory.selectedItem.toString()

            val category = if (selectedCategory == "Other") {
                val custom = etCustomCategory.text.toString()

                if (custom.isEmpty()) {
                    etCustomCategory.error = "Enter category"
                    return@setOnClickListener
                }
                custom
            } else {
                selectedCategory
            }

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