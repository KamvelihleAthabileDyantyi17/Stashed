package com.example.stashed.data.dao

import androidx.room.*
import com.example.stashed.data.entities.User
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUser(user: User): Long

    @Update
    suspend fun updateUser(user: User)

    @Delete
    suspend fun deleteUser(user: User)

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): User?

    @Query("SELECT * FROM users WHERE userId = :userId LIMIT 1")
    suspend fun getUserById(userId: Int): User?

    @Query("SELECT * FROM users WHERE email = :email AND passwordHash = :hash LIMIT 1")
    suspend fun login(email: String, hash: String): User?
}
