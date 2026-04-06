package com.example.zorvyn.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.OnConflictStrategy
import com.example.zorvyn.data.entity.TransactionEntity
import com.example.zorvyn.ui.insights.model.CategoryTotal
import com.example.zorvyn.ui.insights.model.DailyExpense
import com.example.zorvyn.ui.insights.model.WeeklyData

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): LiveData<List<TransactionEntity>>

    @Query("""
SELECT SUM(amount) FROM transactions
WHERE type = 'expense'
AND date >= strftime('%s','now','localtime','start of month')*1000
""")
    fun getMonthlyExpense(): LiveData<Double?>

    @Query("""
SELECT category, SUM(amount) as total
FROM transactions
WHERE type = 'expense'
GROUP BY category
ORDER BY total DESC
LIMIT 1
""")
    fun getTopCategory(): LiveData<CategoryTotal?>

    @Query("""
SELECT 
    SUM(CASE 
        WHEN date >= strftime('%s','now','-7 days')*1000 
        THEN amount END) as thisWeek,

    SUM(CASE 
        WHEN date < strftime('%s','now','-7 days')*1000 
         AND date >= strftime('%s','now','-14 days')*1000
        THEN amount END) as lastWeek

FROM transactions
WHERE type = 'expense'
AND date >= strftime('%s','now','-14 days')*1000
""")
    fun getWeeklyComparison(): LiveData<WeeklyData>

    @Query("""
SELECT category, SUM(amount) as total
FROM transactions
WHERE type = 'expense'
GROUP BY category
ORDER BY total DESC
""")
    fun getCategoryTotals(): LiveData<List<CategoryTotal>>

    @Query("""
SELECT 
    strftime('%Y-%m-%d', date/1000, 'unixepoch') as date,
    SUM(amount) as total
FROM transactions
WHERE type = 'expense'
GROUP BY date
ORDER BY date ASC
""")
    fun getDailyExpenses(): LiveData<List<DailyExpense>>

    @Query("""
SELECT 
    strftime('%Y-%m-%d', date/1000, 'unixepoch') as date,
    SUM(amount) as total
FROM transactions
WHERE type = 'expense'
AND date >= strftime('%s','now','-7 days')*1000
GROUP BY date
ORDER BY date ASC
""")
    fun getLast7DaysExpenses(): LiveData<List<DailyExpense>>
}

