package com.example.stashed.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.stashed.data.entities.Badge

@Dao
interface BadgeDao {

    @Insert
    suspend fun insertBadge(badge: Badge)

    @Query("SELECT * FROM badges WHERE userId = :userId ORDER BY dateEarned DESC")
    suspend fun getBadgesForUser(userId: Int): List<Badge>

    @Query("SELECT COUNT(*) FROM badges WHERE userId = :userId AND badgeName = :name")
    suspend fun badgeExists(userId: Int, name: String): Int
}