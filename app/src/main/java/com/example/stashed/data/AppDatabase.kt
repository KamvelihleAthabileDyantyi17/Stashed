package com.example.stashed.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

// Fixed the imports to correctly point to your DAO folder
import com.example.stashed.data.dao.BadgeDao
import com.example.stashed.data.dao.BudgetDao
import com.example.stashed.data.dao.CategoryDao
import com.example.stashed.data.dao.ExpenseDao
import com.example.stashed.data.dao.GoalDao
import com.example.stashed.data.dao.UserDao

import com.example.stashed.data.entities.Badge
import com.example.stashed.data.entities.BudgetGoal
import com.example.stashed.data.entities.Category
import com.example.stashed.data.entities.Expense
import com.example.stashed.data.entities.User
// Note: If you have a separate "Goal" entity, import it here too!

import com.example.stashed.data.entities.Goal

@Database(
    entities = [User::class, Expense::class, Category::class, BudgetGoal::class, Badge::class, Goal::class],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun budgetDao(): BudgetDao
    abstract fun badgeDao(): BadgeDao

    // 👇 Fixed: Added the missing DAOs that StashedApplication is looking for
    abstract fun categoryDao(): CategoryDao
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Use getDatabase to match usages across the app
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "stashed_database"
                )
                    .fallbackToDestructiveMigration() // Highly recommended to prevent dev crashes!
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}