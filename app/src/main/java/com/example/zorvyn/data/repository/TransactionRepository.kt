package com.example.zorvyn.data.repository

import androidx.lifecycle.LiveData
import com.example.zorvyn.data.dao.TransactionDao
import com.example.zorvyn.data.entity.TransactionEntity

/**
 * Repository layer for handling transaction data.
 * Acts as an abstraction between ViewModel and Room Database.
 */
class TransactionRepository(private val transactionDao: TransactionDao) {

    /**
     * LiveData list of all transactions.
     * Automatically updates UI when database changes.
     */
    val allTransactions: LiveData<List<TransactionEntity>> =
        transactionDao.getAllTransactions()

    /**
     * Insert a new transaction into the database.
     */
    suspend fun insertTransaction(transaction: TransactionEntity) {
        transactionDao.insertTransaction(transaction)
    }

    /**
     * Update an existing transaction.
     */
    suspend fun updateTransaction(transaction: TransactionEntity) {
        transactionDao.updateTransaction(transaction)
    }

    /**
     * Delete a transaction from the database.
     */
    suspend fun deleteTransaction(transaction: TransactionEntity) {
        transactionDao.deleteTransaction(transaction)
    }
}