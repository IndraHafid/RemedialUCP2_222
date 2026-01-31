package com.example.remedialucp2_222.data.database.dao

import androidx.room.*
import com.example.remedialucp2_222.data.database.entities.*

@Dao
interface CrossRefDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookAuthor(bookAuthor: BookAuthorCrossRef)

    @Query("DELETE FROM book_author_cross_ref WHERE bookId = :bookId")
    suspend fun deleteBookAuthors(bookId: String)

    @Query("DELETE FROM book_author_cross_ref WHERE bookId = :bookId AND authorId = :authorId")
    suspend fun deleteBookAuthor(bookId: String, authorId: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookCategory(bookCategory: BookCategoryCrossRef)

    @Query("DELETE FROM book_category_cross_ref WHERE bookId = :bookId")
    suspend fun deleteBookCategories(bookId: String)

    @Query("DELETE FROM book_category_cross_ref WHERE categoryId = :categoryId")
    suspend fun deleteCategoryBooks(categoryId: String)

    @Query("SELECT bookId FROM book_category_cross_ref WHERE categoryId = :categoryId")
    suspend fun getBookIdsByCategory(categoryId: String): List<String>
}
