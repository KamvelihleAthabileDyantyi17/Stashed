package com.example.stashed.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Int = 0,
    val userId: String = "",
    val name: String = "",
    val icon: String = "🛒",
    val colorHex: String = "#E2B13C",
    val isDefault: Boolean = false
)