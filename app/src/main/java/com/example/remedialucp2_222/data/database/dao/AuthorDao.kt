package com.example.remedialucp2_222.data.database.dao

import androidx.room.*
import com.example.remedialucp2_222.data.database.entities.AuthorEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthorDao {
    @Query("SELECT * FROM authors WHERE isDeleted = 0")
    fun getAllAuthors(): Flow<List<AuthorEntity>>

    @Query("SELECT * FROM authors WHERE id = :id AND isDeleted = 0")
    suspend fun getAuthorById(id: String): AuthorEntity?

    @Query("SELECT * FROM authors WHERE name LIKE '%' || :name || '%' AND isDeleted = 0")
    fun searchAuthorsByName(name: String): Flow<List<AuthorEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAuthor(author: AuthorEntity)

    @Update
    suspend fun updateAuthor(author: AuthorEntity)

    @Query("UPDATE authors SET isDeleted = 1, updatedAt = CURRENT_TIMESTAMP WHERE id = :id")
    suspend fun softDeleteAuthor(id: String)

    @Query("DELETE FROM authors WHERE id = :id")
    suspend fun hardDeleteAuthor(id: String)
}
