package com.example.stashed.data.dao

import androidx.room.*
import com.example.stashed.data.entities.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategories(categories: List<Category>)

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM categories WHERE userId = :userId ORDER BY isDefault DESC, name ASC")
    fun getCategoriesForUser(userId: Int): Flow<List<Category>>

    @Query("SELECT * FROM categories WHERE userId = :userId ORDER BY isDefault DESC, name ASC")
    suspend fun getCategoriesForUserSync(userId: Int): List<Category>

    @Query("SELECT * FROM categories WHERE categoryId = :categoryId LIMIT 1")
    suspend fun getCategoryById(categoryId: Int): Category?

    @Query("SELECT COUNT(*) FROM categories WHERE userId = :userId")
    suspend fun getCategoryCount(userId: Int): Int
}
