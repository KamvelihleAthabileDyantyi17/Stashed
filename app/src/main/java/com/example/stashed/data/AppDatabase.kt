package com.example.stashed.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.stashed.data.dao.CategoryDao
import com.example.stashed.data.dao.ExpenseDao
import com.example.stashed.data.dao.GoalDao
import com.example.stashed.data.dao.UserDao
import com.example.stashed.data.entities.Category
import com.example.stashed.data.entities.Expense
import com.example.stashed.data.entities.Goal
import com.example.stashed.data.entities.User

@Database(
    entities = [User::class, Expense::class, Category::class, Goal::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun expenseDao(): ExpenseDao
    abstract fun categoryDao(): CategoryDao
    abstract fun goalDao(): GoalDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "stashed_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
