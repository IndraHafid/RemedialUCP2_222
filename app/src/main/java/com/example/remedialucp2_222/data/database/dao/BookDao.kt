package com.example.remedialucp2_222.data.database.dao

import androidx.room.*
import com.example.remedialucp2_222.data.database.entities.BookEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books WHERE isDeleted = 0")
    fun getAllBooks(): Flow<List<BookEntity>>

    @Query("SELECT * FROM books WHERE id = :id AND isDeleted = 0")
    suspend fun getBookById(id: String): BookEntity?

    @Query("SELECT * FROM books WHERE status = :status AND isDeleted = 0")
    fun getBooksByStatus(status: String): Flow<List<BookEntity>>

    @Query("""
        SELECT b.* FROM books b
        INNER JOIN book_category_cross_ref bcc ON b.id = bcc.bookId
        WHERE bcc.categoryId IN (:categoryIds) AND b.isDeleted = 0
    """)
    fun getBooksByCategories(categoryIds: List<String>): Flow<List<BookEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(book: BookEntity)

    @Update
    suspend fun updateBook(book: BookEntity)

    @Query("UPDATE books SET isDeleted = 1, updatedAt = CURRENT_TIMESTAMP WHERE id = :id")
    suspend fun softDeleteBook(id: String)

    @Query("DELETE FROM books WHERE id = :id")
    suspend fun hardDeleteBook(id: String)

    @Query("SELECT COUNT(*) FROM books WHERE status = 'borrowed' AND id IN (:bookIds)")
    suspend fun countBorrowedBooks(bookIds: List<String>): Int

    @Transaction
    @Query("SELECT * FROM books WHERE isDeleted = 0")
    fun getBooksWithAuthors(): Flow<List<BookWithAuthors>>
}
