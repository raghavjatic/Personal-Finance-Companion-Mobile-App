package com.example.zorvyn.ui.transactions

import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zorvyn.R
import com.example.zorvyn.ui.adapter.TransactionAdapter
import com.example.zorvyn.ui.viewmodel.TransactionViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class TransactionsFragment : Fragment(R.layout.fragment_transactions) {

    private val viewModel: TransactionViewModel by viewModels()

    private lateinit var adapter: TransactionAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: TextView
    private lateinit var fab: FloatingActionButton
    private lateinit var etSearch: EditText

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewTransactions)
        emptyState = view.findViewById(R.id.tvEmptyState)
        fab = view.findViewById(R.id.fabAddTransaction)
        etSearch = view.findViewById(R.id.etSearch)


        setupRecyclerView()
        observeData()
        setupSwipeToDelete()
        setupFab()

        etSearch.addTextChangedListener { text ->

            val query = text.toString().lowercase()

            if (query.isEmpty()) {
                adapter.submitList(viewModel.allTransactions.value)
                return@addTextChangedListener
            }

            val filteredList = viewModel.allTransactions.value?.filter {
                it.category.lowercase().contains(query) ||
                        it.notes?.lowercase()?.contains(query) == true
            }

            adapter.submitList(filteredList)
        }
    }


    private fun setupRecyclerView() {
        adapter = TransactionAdapter { transaction ->
            // TODO: Edit transaction
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun observeData() {
        viewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            adapter.submitList(transactions)

            emptyState.visibility = if (transactions.isEmpty()) View.VISIBLE else View.GONE
        }
    }

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

    private fun setupFab() {
        fab.setOnClickListener {
            val bottomSheet = AddTransactionBottomSheet()
            bottomSheet.show(parentFragmentManager, "AddTransaction")
        }
    }
}