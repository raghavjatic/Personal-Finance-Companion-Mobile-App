package com.example.zorvyn.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.zorvyn.data.AppDatabase
import com.example.zorvyn.data.entity.TransactionEntity
import com.example.zorvyn.data.repository.TransactionRepository
import kotlinx.coroutines.launch

/**
 * ViewModel for managing transaction data.
 * Handles communication between UI and Repository.
 */
class TransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository

    /**
     * LiveData list of all transactions observed by UI.
     */
    val allTransactions: LiveData<List<TransactionEntity>>

    init {
        val database = AppDatabase.getDatabase(application)
        val transactionDao = database.transactionDao()
        repository = TransactionRepository(transactionDao)

        allTransactions = repository.allTransactions
    }

    /**
     * Insert a new transaction.
     */
    fun insert(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.insertTransaction(transaction)
        }
    }

    /**
     * Update an existing transaction.
     */
    fun update(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.updateTransaction(transaction)
        }
    }

    /**
     * Delete a transaction.
     */
    fun delete(transaction: TransactionEntity) {
        viewModelScope.launch {
            repository.deleteTransaction(transaction)
        }
    }
}