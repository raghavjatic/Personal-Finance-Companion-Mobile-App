package com.example.zorvyn.ui.transactions

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.format.DateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.activityViewModels
import com.example.zorvyn.R
import com.example.zorvyn.data.entity.TransactionEntity
import com.example.zorvyn.ui.viewmodel.TransactionViewModel
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class AddTransactionBottomSheet : BottomSheetDialogFragment() {

    private var existingTransaction: TransactionEntity? = null
    private val viewModel: TransactionViewModel by activityViewModels()

    // ✅ Correct placement
    fun setTransaction(transaction: TransactionEntity) {
        existingTransaction = transaction
    }

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
        val tvDate = view.findViewById<TextView>(R.id.tvDate)
        val btnDelete = view.findViewById<Button>(R.id.btnDelete)

        // Category lists
        val expenseCategories = listOf("Food", "Travel", "Shopping", "Bills", "Other")
        val incomeCategories = listOf("Salary", "Pocket Money", "Other")

        var selectedDate = System.currentTimeMillis()

        // Animation
        view.alpha = 0f
        view.animate().alpha(1f).setDuration(200).start()

        // =========================
        // EDIT MODE
        // =========================
        if (existingTransaction != null) {

            btnDelete.visibility = View.VISIBLE

            val transaction = existingTransaction!!

            etAmount.setText(transaction.amount.toString())
            etNotes.setText(transaction.notes)

            // Type
            val isIncome = transaction.type == "income"
            rbIncome.isChecked = isIncome
            rbExpense.isChecked = !isIncome

            // Categories based on type
            val categories = if (isIncome) incomeCategories else expenseCategories

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                categories
            )
            spCategory.adapter = adapter

            // Category selection
            val position = categories.indexOf(transaction.category)
            if (position >= 0) {
                spCategory.setSelection(position)
            } else {
                spCategory.setSelection(categories.indexOf("Other"))
                etCustomCategory.visibility = View.VISIBLE
                etCustomCategory.setText(transaction.category)
            }

            // Date
            selectedDate = transaction.date
            tvDate.text = DateFormat.format("dd MMM yyyy", selectedDate)

            btnSave.text = "Update Transaction"

        } else {
            // =========================
            // ➕ ADD MODE (default)
            // =========================

            rbExpense.isChecked = true

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                expenseCategories
            )
            spCategory.adapter = adapter

            tvDate.text = DateFormat.format("dd MMM yyyy", selectedDate)
        }

        // =========================
        // TYPE SWITCH
        // =========================
        rgType.setOnCheckedChangeListener { _, checkedId ->

            val categories = if (checkedId == R.id.rbIncome) {
                incomeCategories
            } else {
                expenseCategories
            }

            val adapter = ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                categories
            )
            spCategory.adapter = adapter

            etCustomCategory.visibility = View.GONE
        }

        // =========================
        // HANDLE "OTHER"
        // =========================
        spCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selected = parent.getItemAtPosition(position).toString()

                etCustomCategory.visibility =
                    if (selected == "Other") View.VISIBLE else View.GONE
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        // =========================
        // DATE PICKER
        // =========================
        tvDate.setOnClickListener {

            val calendar = Calendar.getInstance()

            val datePicker = DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    selectedDate = calendar.timeInMillis

                    tvDate.text = DateFormat.format("dd MMM yyyy", selectedDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )

            datePicker.show()
        }

        // =========================
        // SAVE / UPDATE
        // =========================
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

            if (existingTransaction == null) {
                val transaction = TransactionEntity(
                    amount = amount,
                    type = type,
                    category = category,
                    date = selectedDate,
                    notes = notes
                )
                viewModel.insert(transaction)

            } else {
                val updatedTransaction = existingTransaction!!.copy(
                    amount = amount,
                    type = type,
                    category = category,
                    date = selectedDate,
                    notes = notes
                )
                viewModel.update(updatedTransaction)
            }

            Toast.makeText(requireContext(), "Transaction Saved", Toast.LENGTH_SHORT).show()
            dismiss()
        }

        // =========================
        // DELETE
        // =========================
        btnDelete.setOnClickListener {

            existingTransaction?.let { transaction ->

                androidx.appcompat.app.AlertDialog.Builder(requireContext())
                    .setTitle("Delete Transaction")
                    .setMessage("Are you sure you want to delete this transaction?")
                    .setPositiveButton("Delete") { _, _ ->

                        viewModel.delete(transaction)

                        Toast.makeText(
                            requireContext(),
                            "Transaction Deleted",
                            Toast.LENGTH_SHORT
                        ).show()

                        dismiss()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }
    }
}