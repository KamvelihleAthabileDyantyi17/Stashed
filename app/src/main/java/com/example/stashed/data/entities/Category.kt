package com.example.stashed.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int = 0,
    val userId: Int,           // Which user this belongs to
    val name: String,          // e.g. "Groceries", "Transport"
    val iconName: String = "", // For displaying an icon
    val colorHex: String = "#FF0000",
    val budgetLimit: Double = 0.0,

    // 👇 This is the magic line that fixes your last 6 errors! 👇
    val isDefault: Boolean = false
)