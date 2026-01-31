package com.example.remedialucp2_222.data.database.dao

import androidx.room.*
import com.example.remedialucp2_222.data.database.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories WHERE isDeleted = 0 ORDER BY level, name")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Query("SELECT * FROM categories WHERE id = :id AND isDeleted = 0")
    suspend fun getCategoryById(id: String): CategoryEntity?

    @Query("SELECT * FROM categories WHERE parentId = :parentId AND isDeleted = 0")
    fun getSubCategories(parentId: String?): Flow<List<CategoryEntity>>

    @Query("""
        WITH RECURSIVE category_tree AS (
            SELECT id, name, description, parentId, level, 0 as depth
            FROM categories 
            WHERE id = :categoryId AND isDeleted = 0
            
            UNION ALL
            
            SELECT c.id, c.name, c.description, c.parentId, c.level, ct.depth + 1
            FROM categories c
            INNER JOIN category_tree ct ON c.parentId = ct.id
            WHERE c.isDeleted = 0
        )
        SELECT id FROM category_tree
    """)
    suspend fun getAllChildCategoryIds(categoryId: String): List<String>

    @Query("""
        WITH RECURSIVE category_tree AS (
            SELECT id, name, description, parentId, level, 0 as depth
            FROM categories 
            WHERE id = :categoryId AND isDeleted = 0
            
            UNION ALL
            
            SELECT c.id, c.name, c.description, c.parentId, c.level, ct.depth + 1
            FROM categories c
            INNER JOIN category_tree ct ON c.id = ct.parentId
            WHERE c.isDeleted = 0
        )
        SELECT id FROM category_tree WHERE id != :categoryId
    """)
    suspend fun getAllParentCategoryIds(categoryId: String): List<String>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: CategoryEntity)

    @Update
    suspend fun updateCategory(category: CategoryEntity)

    @Query("UPDATE categories SET isDeleted = 1, updatedAt = CURRENT_TIMESTAMP WHERE id = :id")
    suspend fun softDeleteCategory(id: String)

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun hardDeleteCategory(id: String)

    @Query("SELECT COUNT(*) > 0 FROM categories WHERE parentId = :categoryId AND isDeleted = 0")
    suspend fun hasChildCategories(categoryId: String): Boolean

    @Query("SELECT EXISTS(SELECT 1 FROM categories c1 WHERE c1.parentId = c2.id AND c1.id = :categoryId AND c2.isDeleted = 0)")
    suspend fun wouldCreateCycle(categoryId: String, newParentId: String?): Boolean

    @Transaction
    suspend fun deleteCategoryWithBooks(
        categoryId: String, 
        deleteBooks: Boolean,
        moveToUncategorized: Boolean
    ) {
        // This will be implemented in the repository with proper transaction handling
    }
}
