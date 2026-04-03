package com.example.zorvyn.ui.transactions

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zorvyn.R
import com.example.zorvyn.ui.adapter.TransactionAdapter
import com.example.zorvyn.ui.viewmodel.TransactionViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class TransactionsFragment : Fragment(R.layout.fragment_transactions) {

    private val viewModel: TransactionViewModel by viewModels()

    private lateinit var adapter: TransactionAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: TextView
    private lateinit var fab: FloatingActionButton

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewTransactions)
        emptyState = view.findViewById(R.id.tvEmptyState)
        fab = view.findViewById(R.id.fabAddTransaction)

        // Setup adapter
        adapter = TransactionAdapter { transaction ->
            // Handle item click (for edit later)
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Observe data
        viewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            adapter.updateTransactions(transactions)

            // Show/hide empty state
            if (transactions.isEmpty()) {
                emptyState.visibility = View.VISIBLE
            } else {
                emptyState.visibility = View.GONE
            }
        }

        // FAB click (we will add bottom sheet later)
        fab.setOnClickListener {
            fab.setOnClickListener {
                val bottomSheet = AddTransactionBottomSheet()
                bottomSheet.show(parentFragmentManager, "AddTransaction")
            }
        }
    }
}