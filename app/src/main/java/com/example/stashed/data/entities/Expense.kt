package com.example.stashed.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "expenses",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Category::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.SET_DEFAULT
        )
    ],
    indices = [Index("userId"), Index("categoryId")]
)
data class Expense(
    @PrimaryKey(autoGenerate = true) val expenseId: Int = 0,
    val userId: Int,
    val categoryId: Int,
    val amount: Double,
    val note: String = "",
    val dateAdded: Long = System.currentTimeMillis()
)
