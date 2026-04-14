package com.example.stashed.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "goals",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("userId")]
)
data class Goal(
    @PrimaryKey(autoGenerate = true) val goalId: Int = 0,
    val userId: Int,
    val goalName: String,
    val targetAmount: Double,
    val savedAmount: Double = 0.0,
    val deadline: Long,
    val isComplete: Boolean = false
)
