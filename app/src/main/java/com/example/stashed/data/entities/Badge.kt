package com.example.stashed.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "badges")
data class Badge(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val userId: Int,
    val badgeName: String,          // e.g. "Budget Master"
    val description: String,
    val dateEarned: Long = System.currentTimeMillis(),
    val iconName: String = ""
)