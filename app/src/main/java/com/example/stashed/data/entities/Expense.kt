package com.example.stashed.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expenses")
data class Expense(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: Int,
    val amount: Double,
    val description: String,
    val category: String,
    val date: Long,
    val isSynced: Boolean = false // NEW FLAG: Defaults to false when created offline
)