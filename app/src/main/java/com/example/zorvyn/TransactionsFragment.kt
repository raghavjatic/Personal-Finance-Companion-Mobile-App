package com.example.zorvyn.ui.transactions

import android.os.Bundle
import android.view.View
import android.widget.TextView
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView = view.findViewById(R.id.recyclerViewTransactions)
        emptyState = view.findViewById(R.id.tvEmptyState)
        fab = view.findViewById(R.id.fabAddTransaction)

        // Setup adapter
        adapter = TransactionAdapter { transaction ->
            // Future: handle edit
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        // Observe data
        viewModel.allTransactions.observe(viewLifecycleOwner) { transactions ->
            adapter.updateTransactions(transactions)

            emptyState.visibility = if (transactions.isEmpty()) View.VISIBLE else View.GONE
        }

        // Swipe to delete
        val itemTouchHelper = ItemTouchHelper(object :
            ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean = false

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

                val position = viewHolder.adapterPosition
                val transaction = adapter.currentList[position]

                viewModel.delete(transaction)

                Snackbar.make(recyclerView, "Transaction deleted", Snackbar.LENGTH_SHORT).show()
            }
        })

        itemTouchHelper.attachToRecyclerView(recyclerView)

        // FAB click → open bottom sheet
        fab.setOnClickListener {
            val bottomSheet = AddTransactionBottomSheet()
            bottomSheet.show(parentFragmentManager, "AddTransaction")
        }
    }
}