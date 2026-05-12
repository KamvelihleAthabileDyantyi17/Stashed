package com.example.stashed.data



import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.stashed.data.dao.UserDao

import com.example.stashed.data.dao.BudgetDao
import com.example.stashed.data.entities.Badge
import com.example.stashed.data.entities.BudgetGoal
import com.example.stashed.data.entities.Category
import com.example.stashed.data.entities.Expense
import com.example.stashed.data.entities.ExpenseDao
import com.example.stashed.data.entities.User

@Database(
    entities = [User::class, Expense::class, Category::class, BudgetGoal::class, Badge::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun budgetDao(): BudgetDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "stashed_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}