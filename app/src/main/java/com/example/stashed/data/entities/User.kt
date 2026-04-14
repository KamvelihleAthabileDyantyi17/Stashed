package com.example.stashed.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class User(
    @PrimaryKey(autoGenerate = true) val userId: Int = 0,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val dateRegistered: Long = System.currentTimeMillis()
)
