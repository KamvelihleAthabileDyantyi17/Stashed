package com.example.stashed.data.entities//this will always match the package name(k.k)

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val username: String,
    val password: String,  // In a real app you'd hash this - fine for now
    val fullName: String,
    val createdAt: Long = System.currentTimeMillis()
)