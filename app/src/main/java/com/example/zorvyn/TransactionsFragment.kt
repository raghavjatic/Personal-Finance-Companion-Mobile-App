package com.example.zorvyn.ui.transactions

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zorvyn.FilterBottomSheet
import com.example.zorvyn.R
import com.example.zorvyn.SortBottomSheet
import com.example.zorvyn.ui.adapter.TransactionAdapter
import com.example.zorvyn.ui.viewmodel.TransactionViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar

class TransactionsFragment : Fragment(R.layout.fragment_transactions) {

    private val viewModel: TransactionViewModel by viewModels()

    private lateinit var adapter: TransactionAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: TextView
    private lateinit var fab: FloatingActionButton
    private lateinit var etSearch: EditText
    private lateinit var btnFilter: ImageButton
    private lateinit var btnSort: ImageButton   // ✅ FIXED

    // 🔥 STATE
    private var currentQuery: String = ""
    private var currentTypeFilter: String = "all"
    private var currentTimeFilter: String = "all"
    private var currentSort: String = "date_desc"   // ✅ FIXED

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewTransactions)
        emptyState = view.findViewById(R.id.tvEmptyState)
        fab = view.findViewById(R.id.fabAddTransaction)
        etSearch = view.findViewById(R.id.etSearch)
        btnFilter = view.findViewById(R.id.btnFilter)
        btnSort = view.findViewById(R.id.btnSort)   // ✅ FIXED

        setupRecyclerView()
        observeData()
        setupSwipeToDelete()
        setupFab()

        // 🔽 FILTER
        btnFilter.setOnClickListener {
            val sheet = FilterBottomSheet { type, time ->
                currentTypeFilter = type
                currentTimeFilter = time
                applyAllFilters()
            }
            sheet.show(parentFragmentManager, "Filter")
        }

        // 🔽 SORT
        btnSort.setOnClickListener {
            val sheet = SortBottomSheet { sort ->
                currentSort = sort
                applyAllFilters()
            }
            sheet.show(parentFragmentManager, "Sort")
        }

        // 🔍 SEARCH
        etSearch.addTextChangedListener { text ->
            currentQuery = text.toString().lowercase()
            applyAllFilters()
        }
    }

    // =========================
    // 🧾 RECYCLER VIEW
    // =========================
    private fun setupRecyclerView() {
        adapter = TransactionAdapter { transaction ->
            val bottomSheet = AddTransactionBottomSheet()
            bottomSheet.setTransaction(transaction)
            bottomSheet.show(parentFragmentManager, "EditTransaction")
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    // =========================
    // 📡 OBSERVE DATA
    // =========================
    private fun observeData() {
        viewModel.allTransactions.observe(viewLifecycleOwner) {
            applyAllFilters()
        }
    }

    // =========================
    // 🧠 FILTER + SORT LOGIC
    // =========================
    private fun applyAllFilters() {

        val originalList = viewModel.allTransactions.value ?: return

        val filtered = originalList.filter { transaction ->

            // 🔍 Search
            val searchMatch =
                transaction.category.lowercase().contains(currentQuery) ||
                        transaction.notes?.lowercase()?.contains(currentQuery) == true

            // 🧾 Type
            val typeMatch = when (currentTypeFilter) {
                "income" -> transaction.type == "income"
                "expense" -> transaction.type == "expense"
                else -> true
            }

            // 📅 Time
            val timeMatch = when (currentTimeFilter) {

                "today" -> {
                    val today = Calendar.getInstance()
                    val transCal = Calendar.getInstance()
                    transCal.timeInMillis = transaction.date

                    today.get(Calendar.YEAR) == transCal.get(Calendar.YEAR) &&
                            today.get(Calendar.DAY_OF_YEAR) == transCal.get(Calendar.DAY_OF_YEAR)
                }

                "month" -> {
                    val today = Calendar.getInstance()
                    val transCal = Calendar.getInstance()
                    transCal.timeInMillis = transaction.date

                    today.get(Calendar.YEAR) == transCal.get(Calendar.YEAR) &&
                            today.get(Calendar.MONTH) == transCal.get(Calendar.MONTH)
                }

                else -> true
            }

            searchMatch && typeMatch && timeMatch
        }

        //SORTING (FINAL STEP)
        val sortedList = when (currentSort) {
            "date_asc" -> filtered.sortedBy { it.date }
            "amount_desc" -> filtered.sortedByDescending { it.amount }
            "amount_asc" -> filtered.sortedBy { it.amount }
            else -> filtered.sortedByDescending { it.date }
        }

        adapter.submitList(sortedList)

        emptyState.visibility =
            if (sortedList.isEmpty()) View.VISIBLE else View.GONE
    }

    // =========================
    // 🧹 SWIPE DELETE
    // =========================
    private fun setupSwipeToDelete() {
        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position = viewHolder.bindingAdapterPosition
                val transaction = adapter.currentList.getOrNull(position) ?: return

                viewModel.delete(transaction)

                Snackbar.make(recyclerView, "Transaction deleted", Snackbar.LENGTH_LONG)
                    .setAction("Undo") {
                        viewModel.insert(transaction)
                    }
                    .show()
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)
    }

    // =========================
    // ➕ ADD
    // =========================
    private fun setupFab() {
        fab.setOnClickListener {
            val bottomSheet = AddTransactionBottomSheet()
            bottomSheet.show(parentFragmentManager, "AddTransaction")
        }
    }
}