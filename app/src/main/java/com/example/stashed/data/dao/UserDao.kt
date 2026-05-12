package com.example.stashed.data.dao


import androidx.room.*
import com.example.stashed.data.entities.User

@Dao
interface UserDao {

    // Register a new user
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun registerUser(user: User): Long

    // Login — find user by username and password
    @Query("SELECT * FROM users WHERE username = :username AND password = :password LIMIT 1")
    suspend fun loginUser(username: String, password: String): User?

    // Check if username already exists (for registration validation)
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    // Get user by ID (useful for session management)
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: Int): User?
}